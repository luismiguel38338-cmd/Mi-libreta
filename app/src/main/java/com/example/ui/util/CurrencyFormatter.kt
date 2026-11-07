package com.example.ui.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {
    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    private val decimalFormat = DecimalFormat("#,##0.00", symbols)

    /**
     * Formats an amount into Dominican Pesos: "RD$ 1,250.00"
     */
    fun formatRD(amount: Double): String {
        val safeAmount = if (amount.isNaN() || amount.isInfinite()) 0.0 else amount
        return "RD$ ${decimalFormat.format(safeAmount)}"
    }

    /**
     * Formats raw numeric string for clean input display
     */
    fun parseAmount(text: String): Double? {
        val clean = text.trim().replace(",", "")
        return clean.toDoubleOrNull()
    }
}
