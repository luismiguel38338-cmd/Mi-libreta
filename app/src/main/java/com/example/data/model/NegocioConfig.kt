package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuracion_negocio")
data class NegocioConfig(
    @PrimaryKey
    val id: Int = 1,
    val nombreNegocio: String = "Mi Colmado / Negocio",
    val propietario: String = "",
    val telefonoNegocio: String = "",
    val direccion: String = "",
    val mensajeWhatsAppPlantilla: String = "Hola {cliente}, te recuerdo desde {negocio} que tienes un saldo pendiente de {saldo}. ¡Gracias por tu pago!",
    val monedaSimbolo: String = "RD$"
)
