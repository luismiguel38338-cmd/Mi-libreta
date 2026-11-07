package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Movimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovimiento(movimiento: Movimiento): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movimientos: List<Movimiento>)

    @Delete
    suspend fun deleteMovimiento(movimiento: Movimiento)

    @Query("DELETE FROM movimientos WHERE id = :id")
    suspend fun deleteMovimientoById(id: Long)

    @Query("SELECT * FROM movimientos WHERE clienteId = :clienteId ORDER BY fecha DESC, id DESC")
    fun getMovimientosForCliente(clienteId: Long): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun getAllMovimientos(): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos")
    suspend fun getAllMovimientosSnapshot(): List<Movimiento>
}
