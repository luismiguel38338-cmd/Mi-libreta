package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ClienteConSaldo
import com.example.data.model.Movimiento
import com.example.data.model.NegocioConfig
import com.example.data.model.TipoMovimiento
import com.example.ui.theme.AbonoGreen
import com.example.ui.theme.AbonoGreenContainer
import com.example.ui.theme.DebtRed
import com.example.ui.theme.DebtRedContainer
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateUtils
import com.example.ui.util.ReceiptGenerator
import com.example.ui.viewmodel.ClientDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    viewModel: ClientDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditClient: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showNoPhoneDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.client?.nombre ?: "Detalle del cliente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Quick share statement button
                    if (uiState.client != null) {
                        IconButton(
                            onClick = {
                                val statement = ReceiptGenerator.generateAccountStatement(
                                    businessConfig = uiState.businessConfig,
                                    client = uiState.client!!,
                                    recentMovements = uiState.movements
                                )
                                shareText(context, statement, "Estado de cuenta - ${uiState.client!!.nombre}")
                            }
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = "Compartir estado de cuenta")
                        }
                    }

                    IconButton(
                        onClick = { showOptionsMenu = true },
                        modifier = Modifier.testTag("detail_menu_button")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }

                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar cliente") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                showOptionsMenu = false
                                uiState.client?.let { onNavigateToEditClient(it.id) }
                            },
                            modifier = Modifier.testTag("menu_edit_client")
                        )
                        DropdownMenuItem(
                            text = { Text("Compartir estado de cuenta") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showOptionsMenu = false
                                if (uiState.client != null) {
                                    val statement = ReceiptGenerator.generateAccountStatement(
                                        businessConfig = uiState.businessConfig,
                                        client = uiState.client!!,
                                        recentMovements = uiState.movements
                                    )
                                    shareText(context, statement, "Estado de cuenta - ${uiState.client!!.nombre}")
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Eliminar cliente",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                showOptionsMenu = false
                                viewModel.promptDeleteClient()
                            },
                            modifier = Modifier.testTag("menu_delete_client")
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
        if (uiState.isLoading || uiState.client == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val client = uiState.client!!
            val owesMoney = client.saldo > 0
            val saldoColor = if (owesMoney) DebtRed else AbonoGreen
            val saldoBgColor = if (owesMoney) DebtRedContainer else AbonoGreenContainer
            val isOverdue = client.isPaymentOverdue()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("client_detail_list"),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Large Balance Card
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("client_balance_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = saldoBgColor,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = if (owesMoney) "SALDO PENDIENTE" else "CUENTA AL DÍA",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = saldoColor,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            // Big Balance Text
                            Text(
                                text = CurrencyFormatter.formatRD(client.saldo),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = saldoColor,
                                modifier = Modifier.testTag("client_detail_saldo_text")
                            )

                            // Payment Promise / Due date banner
                            if (client.fechaPromesaPago != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isOverdue) DebtRedContainer else MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = if (isOverdue) DebtRed else MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Fecha de cobro: ${DateUtils.formatDueDate(client.fechaPromesaPago)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOverdue) DebtRed else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            // Credit Limit Progress Bar
                            if (client.limiteCredito > 0.0) {
                                Spacer(modifier = Modifier.height(14.dp))
                                val usage = client.creditLimitUsage ?: 0f
                                val progress = usage.coerceIn(0f, 1f)
                                val limitColor = when {
                                    usage > 1.0f -> DebtRed
                                    usage >= 0.8f -> Color(0xFFE65100)
                                    else -> MaterialTheme.colorScheme.primary
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Límite de crédito:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${CurrencyFormatter.formatRD(client.limiteCredito)} (${(usage * 100).toInt()}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = limitColor
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        color = limitColor,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                    if (usage > 1.0f) {
                                        Text(
                                            text = "⚠️ ¡Límite de crédito excedido por ${CurrencyFormatter.formatRD(client.saldo - client.limiteCredito)}!",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = DebtRed
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Contact info & registration date
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (client.telefono.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = client.telefono,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${client.telefono}")
                                                }
                                                context.startActivity(callIntent)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = "Llamar al cliente",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                if (client.nota.isNotBlank()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notes,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = client.nota,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Cliente desde: ${DateUtils.formatClientCreatedDate(client.fechaCreacion)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Two Action Buttons: "Agregar deuda" and "Registrar abono"
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.openAddDebtDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DebtRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("add_debt_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Agregar deuda",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            onClick = { viewModel.openAddAbonoDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AbonoGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("register_abono_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Registrar abono",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // WhatsApp reminder button
                item {
                    FilledTonalButton(
                        onClick = {
                            if (client.telefono.isBlank()) {
                                showNoPhoneDialog = true
                            } else {
                                sendWhatsAppReminder(
                                    context = context,
                                    config = uiState.businessConfig,
                                    clientName = client.nombre,
                                    phone = client.telefono,
                                    saldo = client.saldo
                                )
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = WhatsAppGreen.copy(alpha = 0.15f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                            .testTag("whatsapp_reminder_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "WhatsApp",
                            tint = WhatsAppGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recordar por WhatsApp",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Movement History Header
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial de movimientos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${uiState.movements.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Movements List or Empty State
                if (uiState.movements.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Sin movimientos registrados",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Usa los botones de arriba para agregar una deuda fiada o registrar un abono de pago.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = uiState.movements,
                        key = { it.id }
                    ) { mov ->
                        MovementItemCard(
                            movement = mov,
                            onShareReceipt = {
                                val receipt = ReceiptGenerator.generateMovementReceipt(
                                    businessConfig = uiState.businessConfig,
                                    client = client,
                                    movement = mov,
                                    saldoAfter = client.saldo
                                )
                                shareText(context, receipt, "Comprobante - ${client.nombre}")
                            },
                            onDeleteClick = { viewModel.promptDeleteMovement(mov) }
                        )
                    }
                }
            }
        }

        // Movement Dialog (Add Debt / Register Abono)
        if (uiState.showMovementDialog && uiState.client != null) {
            MovementDialog(
                tipo = uiState.movementDialogType,
                currentSaldo = uiState.client!!.saldo,
                onDismiss = { viewModel.dismissMovementDialog() },
                onConfirm = { monto, nota, fecha, onError ->
                    viewModel.addMovement(
                        tipo = uiState.movementDialogType,
                        monto = monto,
                        nota = nota,
                        fecha = fecha,
                        onSuccess = { /* Dialog dismissed internally */ },
                        onError = onError
                    )
                }
            )
        }

        // Delete Movement Confirmation Dialog
        if (uiState.movementToDelete != null) {
            val mov = uiState.movementToDelete!!
            val isDeuda = mov.tipo == TipoMovimiento.DEUDA
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteMovementPrompt() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("¿Eliminar este movimiento?") },
                text = {
                    Text(
                        "Se eliminará el registro de ${if (isDeuda) "Deuda" else "Abono"} por " +
                                "${CurrencyFormatter.formatRD(mov.monto)}. " +
                                "El saldo actual del cliente se recalculará automáticamente."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmDeleteMovement() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_delete_movement_button")
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.dismissDeleteMovementPrompt() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Delete Client Confirmation Dialog
        if (uiState.showDeleteClientDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDeleteClientPrompt() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("¿Eliminar cliente?") },
                text = {
                    Text(
                        "Esta acción borrará a \"${uiState.client?.nombre}\" y todo su historial de deudas y abonos de manera permanente. ¿Deseas continuar?"
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.confirmDeleteClient(onDeleted = onNavigateBack)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_delete_client_button")
                    ) {
                        Text("Eliminar definitivamente")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.dismissDeleteClientPrompt() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // No Phone Warning Dialog
        if (showNoPhoneDialog) {
            AlertDialog(
                onDismissRequest = { showNoPhoneDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("Teléfono no registrado") },
                text = {
                    Text(
                        "Este cliente no tiene un número de teléfono guardado. " +
                                "Puedes presionar en 'Editar cliente' en el menú superior para registrar su teléfono y enviarle recordatorios por WhatsApp."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showNoPhoneDialog = false
                            uiState.client?.let { onNavigateToEditClient(it.id) }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Editar ahora")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showNoPhoneDialog = false },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}

@Composable
fun MovementItemCard(
    movement: Movimiento,
    onShareReceipt: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isDeuda = movement.tipo == TipoMovimiento.DEUDA
    val tagColor = if (isDeuda) DebtRed else AbonoGreen
    val tagBgColor = if (isDeuda) DebtRedContainer else AbonoGreenContainer
    val typeLabel = if (isDeuda) "DEUDA" else "ABONO"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("movement_card_${movement.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type Pill / Tag
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tagBgColor
            ) {
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = tagColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details: Date and Note
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = DateUtils.formatMovementDate(movement.fecha),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (movement.nota.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = movement.nota,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount
            Text(
                text = "${if (isDeuda) "+ " else "- "}${CurrencyFormatter.formatRD(movement.monto)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tagColor
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Share movement receipt button
            IconButton(
                onClick = onShareReceipt,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir recibo",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Delete Movement button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .testTag("delete_movement_${movement.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar movimiento",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun shareText(context: Context, text: String, title: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(Intent.createChooser(intent, title))
}

/**
 * Prepares and launches WhatsApp with Dominican Republic phone support (+1-809, 829, 849)
 */
fun sendWhatsAppReminder(
    context: Context,
    config: NegocioConfig?,
    clientName: String,
    phone: String,
    saldo: Double
) {
    val cleanDigits = phone.filter { it.isDigit() }
    val formattedSaldo = CurrencyFormatter.formatRD(saldo)
    val businessName = config?.nombreNegocio?.takeIf { it.isNotBlank() } ?: "Mi Negocio"

    val template = config?.mensajeWhatsAppPlantilla?.takeIf { it.isNotBlank() }
        ?: "Hola {cliente}, te recuerdo que tienes un saldo pendiente de {saldo}. ¡Gracias!"

    val message = template
        .replace("{cliente}", clientName)
        .replace("{saldo}", formattedSaldo)
        .replace("{negocio}", businessName)

    val encoded = Uri.encode(message)

    val phoneWithCountry = when {
        cleanDigits.length == 10 -> "1$cleanDigits"
        cleanDigits.startsWith("1") && cleanDigits.length == 11 -> cleanDigits
        else -> cleanDigits
    }

    val url = "https://api.whatsapp.com/send?phone=$phoneWithCountry&text=$encoded"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        shareText(context, message, "Enviar recordatorio a $clientName")
    }
}
