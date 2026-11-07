package com.example

import com.example.data.model.ClienteConSaldo
import com.example.data.model.Movimiento
import com.example.data.model.NegocioConfig
import com.example.data.model.TipoMovimiento
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.ReceiptGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun currencyFormatter_formatsDominicanPesosCorrectly() {
        val formatted = CurrencyFormatter.formatRD(1250.0)
        assertEquals("RD$ 1,250.00", formatted)

        val zero = CurrencyFormatter.formatRD(0.0)
        assertEquals("RD$ 0.00", zero)

        val cents = CurrencyFormatter.formatRD(450.75)
        assertEquals("RD$ 450.75", cents)
    }

    @Test
    fun currencyFormatter_parsesAmountCorrectly() {
        assertEquals(1250.0, CurrencyFormatter.parseAmount("1250.00")!!, 0.001)
        assertEquals(1250.0, CurrencyFormatter.parseAmount("1,250.00")!!, 0.001)
        assertEquals(50.5, CurrencyFormatter.parseAmount("50.5")!!, 0.001)
    }

    @Test
    fun clienteConSaldo_creditLimitAndOverdueCalculations() {
        val pastDate = System.currentTimeMillis() - 100000L
        val futureDate = System.currentTimeMillis() + 100000000L

        val overdueClient = ClienteConSaldo(
            id = 1,
            nombre = "Carlos",
            telefono = "8095551234",
            nota = "",
            fechaCreacion = System.currentTimeMillis(),
            limiteCredito = 5000.0,
            fechaPromesaPago = pastDate,
            saldo = 2500.0,
            totalMovimientos = 2
        )
        assertTrue(overdueClient.isPaymentOverdue())
        assertEquals(0.5f, overdueClient.creditLimitUsage!!, 0.01f)

        val activeClient = overdueClient.copy(
            fechaPromesaPago = futureDate,
            saldo = 6000.0
        )
        assertFalse(activeClient.isPaymentOverdue())
        assertEquals(1.2f, activeClient.creditLimitUsage!!, 0.01f)
    }

    @Test
    fun receiptGenerator_generatesCleanStatement() {
        val client = ClienteConSaldo(
            id = 1,
            nombre = "Doña Carmen",
            telefono = "8095551234",
            nota = "",
            fechaCreacion = System.currentTimeMillis(),
            limiteCredito = 5000.0,
            fechaPromesaPago = null,
            saldo = 1250.0,
            totalMovimientos = 1
        )
        val config = NegocioConfig(nombreNegocio = "Colmado La Fe")
        val movement = Movimiento(
            id = 1,
            clienteId = 1,
            tipo = TipoMovimiento.DEUDA,
            monto = 500.0,
            nota = "Arroz y aceite"
        )
        val receipt = ReceiptGenerator.generateMovementReceipt(config, client, movement, 1250.0)
        assertTrue(receipt.contains("Colmado La Fe"))
        assertTrue(receipt.contains("Doña Carmen"))
        assertTrue(receipt.contains("RD$ 1,250.00"))
    }
}
