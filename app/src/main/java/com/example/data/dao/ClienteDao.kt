package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Cliente
import com.example.data.model.ClienteConSaldo
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: Cliente): Long

    @Update
    suspend fun updateCliente(cliente: Cliente)

    @Delete
    suspend fun deleteCliente(cliente: Cliente)

    @Query("DELETE FROM clientes WHERE id = :id")
    suspend fun deleteClienteById(id: Long)

    @Query("SELECT * FROM clientes WHERE id = :id")
    fun getClienteById(id: Long): Flow<Cliente?>

    @Query("SELECT * FROM clientes")
    suspend fun getAllClientesSnapshot(): List<Cliente>

    @Query("""
        SELECT 
            c.id,
            c.nombre,
            c.telefono,
            c.nota,
            c.fechaCreacion,
            c.limiteCredito,
            c.fechaPromesaPago,
            COALESCE(SUM(CASE WHEN m.tipo = 'DEUDA' THEN m.monto WHEN m.tipo = 'ABONO' THEN -m.monto ELSE 0 END), 0.0) AS saldo,
            COUNT(m.id) AS totalMovimientos
        FROM clientes c
        LEFT JOIN movimientos m ON c.id = m.clienteId
        GROUP BY c.id
        ORDER BY saldo DESC, c.nombre COLLATE NOCASE ASC
    """)
    fun getClientesConSaldo(): Flow<List<ClienteConSaldo>>

    @Query("""
        SELECT 
            c.id,
            c.nombre,
            c.telefono,
            c.nota,
            c.fechaCreacion,
            c.limiteCredito,
            c.fechaPromesaPago,
            COALESCE(SUM(CASE WHEN m.tipo = 'DEUDA' THEN m.monto WHEN m.tipo = 'ABONO' THEN -m.monto ELSE 0 END), 0.0) AS saldo,
            COUNT(m.id) AS totalMovimientos
        FROM clientes c
        LEFT JOIN movimientos m ON c.id = m.clienteId
        WHERE c.id = :id
        GROUP BY c.id
    """)
    fun getClienteConSaldoById(id: Long): Flow<ClienteConSaldo?>
}
