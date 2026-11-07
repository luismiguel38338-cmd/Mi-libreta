package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.util.DateUtils
import com.example.ui.viewmodel.AddEditClientViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClientScreen(
    viewModel: AddEditClientViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val initialCal = Calendar.getInstance().apply {
        if (uiState.fechaPromesaPago != null) {
            timeInMillis = uiState.fechaPromesaPago!!
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val updated = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            viewModel.onFechaPromesaPagoChange(updated.timeInMillis)
        },
        initialCal.get(Calendar.YEAR),
        initialCal.get(Calendar.MONTH),
        initialCal.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditing) "Editar Cliente" else "Nuevo Cliente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_edit_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Information Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Datos principales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Nombre Field (Mandatory)
                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = { Text("Nombre del cliente *") },
                        placeholder = { Text("Ej: Doña Carmen, Juan Pérez, Colmado La Fe...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        isError = uiState.nombreError != null,
                        supportingText = {
                            if (uiState.nombreError != null) {
                                Text(
                                    text = uiState.nombreError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                Text("Nombre o apodo por el que identificas al cliente")
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_name_input")
                    )

                    // Teléfono Field (Optional)
                    OutlinedTextField(
                        value = uiState.telefono,
                        onValueChange = { viewModel.onTelefonoChange(it) },
                        label = { Text("Teléfono / WhatsApp (opcional)") },
                        placeholder = { Text("Ej: 809-555-1234") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        supportingText = {
                            Text("Se usará para enviar recordatorios automáticos por WhatsApp")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_phone_input")
                    )

                    // Nota Field (Optional)
                    OutlinedTextField(
                        value = uiState.nota,
                        onValueChange = { viewModel.onNotaChange(it) },
                        label = { Text("Nota o referencia (opcional)") },
                        placeholder = { Text("Ej: Vive al lado de la farmacia, trabaja en la escuela...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        supportingText = {
                            Text("Detalles que te ayuden a ubicar o recordar al cliente")
                        },
                        minLines = 2,
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_note_input")
                    )
                }
            }

            // Advanced Financial Controls Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Control de crédito y cobro (avanzado)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // Límite de crédito opcional
                    OutlinedTextField(
                        value = uiState.limiteCredito,
                        onValueChange = { viewModel.onLimiteCreditoChange(it) },
                        label = { Text("Límite de crédito máximo (opcional)") },
                        placeholder = { Text("0.00 (sin límite)") },
                        prefix = {
                            Text(
                                text = "RD$ ",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        supportingText = {
                            Text("La app te avisará si el cliente se acerca o sobrepasa este monto")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_credit_limit_input")
                    )

                    // Fecha prometida de pago
                    Column {
                        Text(
                            text = "Fecha acordada de pago / Promesa (opcional):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { datePickerDialog.show() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.fechaPromesaPago != null) {
                                        DateUtils.formatDueDate(uiState.fechaPromesaPago!!)
                                    } else {
                                        "Seleccionar fecha de cobro"
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (uiState.fechaPromesaPago != null) {
                                IconButton(
                                    onClick = { viewModel.onFechaPromesaPagoChange(null) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Quitar fecha"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons: Guardar and Cancelar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("cancel_client_button")
                ) {
                    Text("Cancelar", style = MaterialTheme.typography.bodyLarge)
                }

                Button(
                    onClick = {
                        viewModel.saveClient(onSuccess = onNavigateBack)
                    },
                    enabled = !uiState.isSaving,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("save_client_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isEditing) "Actualizar" else "Guardar",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
