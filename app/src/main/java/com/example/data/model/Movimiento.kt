package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["clienteId"])]
)
data class Movimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clienteId: Long,
    val tipo: TipoMovimiento,
    val monto: Double,
    val nota: String = "",
    val fecha: Long = System.currentTimeMillis()
)
