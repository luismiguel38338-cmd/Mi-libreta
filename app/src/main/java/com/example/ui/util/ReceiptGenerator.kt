package com.example.ui.util

import com.example.data.model.ClienteConSaldo
import com.example.data.model.Movimiento
import com.example.data.model.NegocioConfig
import com.example.data.model.TipoMovimiento

object ReceiptGenerator {

    /**
     * Builds a printable and shareable receipt for a specific movement
     */
    fun generateMovementReceipt(
        businessConfig: NegocioConfig?,
        client: ClienteConSaldo,
        movement: Movimiento,
        saldoAfter: Double
    ): String {
        val businessName = businessConfig?.nombreNegocio?.takeIf { it.isNotBlank() } ?: "MI LIBRETA"
        val phone = businessConfig?.telefonoNegocio?.takeIf { it.isNotBlank() } ?: ""
        val isDeuda = movement.tipo == TipoMovimiento.DEUDA

        val typeTitle = if (isDeuda) "COMPROBANTE DE FIADO (DEUDA)" else "RECIBO DE ABONO / PAGO"

        val sb = StringBuilder()
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("   🏪 $businessName\n")
        if (phone.isNotBlank()) sb.append("   📞 Tel: $phone\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📄 $typeTitle\n\n")
        sb.append("👤 Cliente: ${client.nombre}\n")
        if (client.telefono.isNotBlank()) sb.append("📱 Teléfono: ${client.telefono}\n")
        sb.append("📅 Fecha: ${DateUtils.formatMovementDate(movement.fecha)}\n\n")

        sb.append("MONTO REGISTRADO:\n")
        sb.append("👉 ${if (isDeuda) "+ " else "- "}${CurrencyFormatter.formatRD(movement.monto)}\n")

        if (movement.nota.isNotBlank()) {
            sb.append("📝 Concepto: ${movement.nota}\n")
        }

        sb.append("\n────────────────────────────\n")
        sb.append("NUEVO SALDO PENDIENTE:\n")
        sb.append("💰 ${CurrencyFormatter.formatRD(saldoAfter)}\n")
        if (client.limiteCredito > 0.0) {
            sb.append("Límite de crédito: ${CurrencyFormatter.formatRD(client.limiteCredito)}\n")
        }
        sb.append("────────────────────────────\n")
        sb.append("¡Gracias por su preferencia!\n\n")
        sb.append("ℹ️ NOTA INFORMATIVA:\n")
        sb.append("Comprobante de control contable interno entre las partes. No constituye título financiero regulado.\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")

        return sb.toString()
    }

    /**
     * Builds a full account statement (Estado de cuenta) for the client
     */
    fun generateAccountStatement(
        businessConfig: NegocioConfig?,
        client: ClienteConSaldo,
        recentMovements: List<Movimiento>
    ): String {
        val businessName = businessConfig?.nombreNegocio?.takeIf { it.isNotBlank() } ?: "MI LIBRETA"
        val phone = businessConfig?.telefonoNegocio?.takeIf { it.isNotBlank() } ?: ""

        val sb = StringBuilder()
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("   🏪 $businessName\n")
        if (phone.isNotBlank()) sb.append("   📞 Tel: $phone\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📋 ESTADO DE CUENTA\n\n")
        sb.append("👤 Cliente: ${client.nombre}\n")
        if (client.telefono.isNotBlank()) sb.append("📱 Teléfono: ${client.telefono}\n")
        sb.append("📅 Fecha de emisión: ${DateUtils.formatMovementDate(System.currentTimeMillis())}\n\n")

        sb.append("💰 SALDO ACTUAL PENDIENTE:\n")
        sb.append("   ${CurrencyFormatter.formatRD(client.saldo)}\n\n")

        if (client.fechaPromesaPago != null) {
            sb.append("⏳ Fecha de pago acordada: ${DateUtils.formatDueDate(client.fechaPromesaPago)}\n\n")
        }

        if (recentMovements.isNotEmpty()) {
            sb.append("📝 ÚLTIMOS MOVIMIENTOS:\n")
            recentMovements.take(5).forEach { m ->
                val sign = if (m.tipo == TipoMovimiento.DEUDA) "[+ DEUDA]" else "[- ABONO]"
                val desc = if (m.nota.isNotBlank()) " (${m.nota})" else ""
                sb.append(" • ${DateUtils.formatMovementDate(m.fecha)}: $sign ${CurrencyFormatter.formatRD(m.monto)}$desc\n")
            }
        }

        sb.append("\n────────────────────────────\n")
        sb.append("Por favor conserve este comprobante.\n")
        sb.append("¡Gracias por confiar en nosotros!\n\n")
        sb.append("ℹ️ NOTA INFORMATIVA:\n")
        sb.append("Documento privado de control mercantil interno entre comerciantes y clientes. No constituye pagaré notarial ni instrumento financiero.\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")

        return sb.toString()
    }
}
