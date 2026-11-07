package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TipoMovimiento
import com.example.ui.theme.AbonoGreen
import com.example.ui.theme.AbonoGreenContainer
import com.example.ui.theme.DebtRed
import com.example.ui.theme.DebtRedContainer
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateUtils
import java.util.Calendar

@Composable
fun MovementDialog(
    tipo: TipoMovimiento,
    currentSaldo: Double,
    onDismiss: () -> Unit,
    onConfirm: (monto: Double, nota: String, fecha: Long, onError: (String) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var montoText by remember { mutableStateOf("") }
    var notaText by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isDeuda = tipo == TipoMovimiento.DEUDA
    val accentColor = if (isDeuda) DebtRed else AbonoGreen
    val containerColor = if (isDeuda) DebtRedContainer else AbonoGreenContainer
    val titleText = if (isDeuda) "Agregar Deuda" else "Registrar Abono"
    val subtitleText = if (isDeuda) {
        "Anota una nueva deuda para este cliente"
    } else {
        "Registra un pago o abono a la cuenta"
    }

    val quickConcepts = if (isDeuda) {
        listOf("Arroz y habichuela", "Víveres y aceite", "Embutidos", "Carnes", "Bebidas", "Limpieza")
    } else {
        listOf("Abono quincena", "Efectivo", "Transferencia", "Abono parcial")
    }

    val calendar = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val updated = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            selectedTimestamp = updated.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("movement_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(containerColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDeuda) Icons.Default.ReceiptLong else Icons.Default.Payments,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info for Abono
                if (!isDeuda) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Saldo pendiente actual:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormatter.formatRD(currentSaldo),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DebtRed
                                )
                            }

                            if (currentSaldo > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                SuggestionChip(
                                    onClick = {
                                        montoText = String.format(java.util.Locale.US, "%.2f", currentSaldo)
                                        notaText = "Pago total de la cuenta"
                                        errorMessage = null
                                    },
                                    label = { Text("Pagar saldo completo (${CurrencyFormatter.formatRD(currentSaldo)})", fontSize = 12.sp) },
                                    icon = { Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = AbonoGreenContainer,
                                        labelColor = AbonoGreen
                                    )
                                )
                            }
                        }
                    }
                }

                // Amount input field
                OutlinedTextField(
                    value = montoText,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }
                        // Allow only one decimal point
                        if (filtered.count { it == '.' } <= 1) {
                            montoText = filtered
                            errorMessage = null
                        }
                    },
                    label = { Text("Monto *") },
                    placeholder = { Text("0.00") },
                    prefix = {
                        Text(
                            text = "RD$ ",
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontSize = 18.sp
                        )
                    },
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            Text("Ingresa el monto exacto en pesos dominicanos")
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("movement_amount_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Note input field
                OutlinedTextField(
                    value = notaText,
                    onValueChange = { notaText = it },
                    label = { Text("Nota o concepto (opcional)") },
                    placeholder = {
                        Text(
                            if (isDeuda) "Ej: Arroz, aceite, café, embutidos..." else "Ej: Abono de la quincena..."
                        )
                    },
                    singleLine = false,
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("movement_note_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick concept chips
                Column {
                    Text(
                        text = "Conceptos rápidos:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickConcepts.forEach { concept ->
                            InputChip(
                                selected = notaText == concept,
                                onClick = {
                                    notaText = if (notaText.isEmpty()) concept else "$notaText, $concept"
                                },
                                label = { Text(concept, fontSize = 11.sp) },
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = accentColor.copy(alpha = 0.15f),
                                    selectedLabelColor = accentColor
                                )
                            )
                        }
                    }
                }

                // Date selection button
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("movement_date_picker_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Seleccionar fecha",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fecha: ${DateUtils.formatMovementDate(selectedTimestamp)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = CurrencyFormatter.parseAmount(montoText)
                    if (parsed == null || parsed <= 0.0) {
                        errorMessage = "Ingresa un monto válido mayor a RD$ 0.00"
                        return@Button
                    }
                    if (!isDeuda && parsed > currentSaldo) {
                        val formattedParsed = CurrencyFormatter.formatRD(parsed)
                        val formattedSaldo = CurrencyFormatter.formatRD(currentSaldo)
                        errorMessage = "El abono ($formattedParsed) no puede ser mayor que el saldo pendiente ($formattedSaldo)."
                        return@Button
                    }
                    onConfirm(parsed, notaText, selectedTimestamp) { error ->
                        errorMessage = error
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("movement_dialog_save_button")
            ) {
                Text(
                    text = if (isDeuda) "Guardar Deuda" else "Registrar Abono",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("movement_dialog_cancel_button")
            ) {
                Text("Cancelar")
            }
        }
    )
}
