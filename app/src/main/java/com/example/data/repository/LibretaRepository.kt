package com.example.data.repository

import com.example.data.dao.ClienteDao
import com.example.data.dao.ConfigDao
import com.example.data.dao.MovimientoDao
import com.example.data.model.Cliente
import com.example.data.model.ClienteConSaldo
import com.example.data.model.Movimiento
import com.example.data.model.NegocioConfig
import com.example.data.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LibretaRepository(
    private val clienteDao: ClienteDao,
    private val movimientoDao: MovimientoDao,
    private val configDao: ConfigDao
) {
    val clientesConSaldo: Flow<List<ClienteConSaldo>> = clienteDao.getClientesConSaldo()
    val allMovimientos: Flow<List<Movimiento>> = movimientoDao.getAllMovimientos()
    val config: Flow<NegocioConfig?> = configDao.getConfig()

    fun getClienteConSaldo(id: Long): Flow<ClienteConSaldo?> =
        clienteDao.getClienteConSaldoById(id)

    fun getCliente(id: Long): Flow<Cliente?> =
        clienteDao.getClienteById(id)

    fun getMovimientos(clienteId: Long): Flow<List<Movimiento>> =
        movimientoDao.getMovimientosForCliente(clienteId)

    suspend fun insertCliente(cliente: Cliente): Long =
        clienteDao.insertCliente(cliente)

    suspend fun updateCliente(cliente: Cliente) =
        clienteDao.updateCliente(cliente)

    suspend fun deleteCliente(clienteId: Long) =
        clienteDao.deleteClienteById(clienteId)

    suspend fun insertMovimiento(movimiento: Movimiento): Long =
        movimientoDao.insertMovimiento(movimiento)

    suspend fun deleteMovimiento(movimientoId: Long) =
        movimientoDao.deleteMovimientoById(movimientoId)

    suspend fun saveConfig(config: NegocioConfig) =
        configDao.saveConfig(config)

    /**
     * Generates a CSV text report of all clients and debts
     */
    suspend fun exportDebtsAsCsv(clients: List<ClienteConSaldo>): String {
        val sb = StringBuilder()
        sb.append("ID,Nombre,Telefono,Nota,Saldo,LimiteCredito,FechaPromesaPago,TotalMovimientos\n")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (c in clients) {
            val dueStr = c.fechaPromesaPago?.let { dateFormat.format(Date(it)) } ?: ""
            val escapedName = "\"${c.nombre.replace("\"", "\"\"")}\""
            val escapedNota = "\"${c.nota.replace("\"", "\"\"")}\""
            sb.append("${c.id},$escapedName,${c.telefono},$escapedNota,${c.saldo},${c.limiteCredito},$dueStr,${c.totalMovimientos}\n")
        }
        return sb.toString()
    }

    /**
     * Generates a complete JSON backup of the store database
     */
    suspend fun exportBackupJson(): String {
        val clients = clienteDao.getAllClientesSnapshot()
        val movements = movimientoDao.getAllMovimientosSnapshot()

        val root = JSONObject()
        root.put("version", 2)
        root.put("exportedAt", System.currentTimeMillis())

        val clientsArray = JSONArray()
        for (c in clients) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("nombre", c.nombre)
                put("telefono", c.telefono)
                put("nota", c.nota)
                put("fechaCreacion", c.fechaCreacion)
                put("limiteCredito", c.limiteCredito)
                if (c.fechaPromesaPago != null) put("fechaPromesaPago", c.fechaPromesaPago)
            }
            clientsArray.put(obj)
        }
        root.put("clientes", clientsArray)

        val movsArray = JSONArray()
        for (m in movements) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("clienteId", m.clienteId)
                put("tipo", m.tipo.name)
                put("monto", m.monto)
                put("nota", m.nota)
                put("fecha", m.fecha)
            }
            movsArray.put(obj)
        }
        root.put("movimientos", movsArray)

        return root.toString(2)
    }

    /**
     * Restores backup from JSON string
     */
    suspend fun restoreBackupJson(jsonString: String): Result<Int> {
        return try {
            val root = JSONObject(jsonString)
            val clientsArray = root.optJSONArray("clientes") ?: return Result.failure(Exception("JSON no contiene lista de clientes"))
            val movsArray = root.optJSONArray("movimientos") ?: JSONArray()

            var count = 0
            for (i in 0 until clientsArray.length()) {
                val obj = clientsArray.getJSONObject(i)
                val cliente = Cliente(
                    id = obj.optLong("id", 0L),
                    nombre = obj.optString("nombre", ""),
                    telefono = obj.optString("telefono", ""),
                    nota = obj.optString("nota", ""),
                    fechaCreacion = obj.optLong("fechaCreacion", System.currentTimeMillis()),
                    limiteCredito = obj.optDouble("limiteCredito", 0.0),
                    fechaPromesaPago = if (obj.has("fechaPromesaPago")) obj.getLong("fechaPromesaPago") else null
                )
                if (cliente.nombre.isNotBlank()) {
                    clienteDao.insertCliente(cliente)
                    count++
                }
            }

            for (i in 0 until movsArray.length()) {
                val obj = movsArray.getJSONObject(i)
                val tipoStr = obj.optString("tipo", "DEUDA")
                val tipo = try { TipoMovimiento.valueOf(tipoStr) } catch (e: Exception) { TipoMovimiento.DEUDA }
                val mov = Movimiento(
                    id = obj.optLong("id", 0L),
                    clienteId = obj.getLong("clienteId"),
                    tipo = tipo,
                    monto = obj.getDouble("monto"),
                    nota = obj.optString("nota", ""),
                    fecha = obj.optLong("fecha", System.currentTimeMillis())
                )
                movimientoDao.insertMovimiento(mov)
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
