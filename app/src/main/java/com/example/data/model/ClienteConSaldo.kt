package com.example.data.model

data class ClienteConSaldo(
    val id: Long,
    val nombre: String,
    val telefono: String,
    val nota: String,
    val fechaCreacion: Long,
    val limiteCredito: Double = 0.0,
    val fechaPromesaPago: Long? = null,
    val saldo: Double,
    val totalMovimientos: Int
) {
    /**
     * Checks if this client has an expired payment promise date with active debt
     */
    fun isPaymentOverdue(): Boolean {
        if (saldo <= 0.0 || fechaPromesaPago == null) return false
        return System.currentTimeMillis() > fechaPromesaPago
    }

    /**
     * Percentage of credit limit used (0.0 to 1.0, or >1.0 if exceeded)
     */
    val creditLimitUsage: Float?
        get() = if (limiteCredito > 0.0) {
            (saldo / limiteCredito).toFloat().coerceAtLeast(0f)
        } else null
}
