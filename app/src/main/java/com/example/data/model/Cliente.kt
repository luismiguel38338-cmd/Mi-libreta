package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val telefono: String = "",
    val nota: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val limiteCredito: Double = 0.0,
    val fechaPromesaPago: Long? = null
)
