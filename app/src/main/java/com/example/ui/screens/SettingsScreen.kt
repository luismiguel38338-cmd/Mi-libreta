package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AbonoGreen
import com.example.ui.theme.AbonoGreenContainer
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.util.LegalContent
import com.example.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onExportCsv: (onReady: (String) -> Unit) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }
    var activeLegalDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeLegalDialogContent by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Mi Negocio y Respaldo",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Configuración, legal y copia de seguridad",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Business Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Perfil de tu Colmado o Negocio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = uiState.config.nombreNegocio,
                            onValueChange = { viewModel.onBusinessNameChange(it) },
                            label = { Text("Nombre del colmado o tienda") },
                            placeholder = { Text("Ej: Colmado Don Juan, Tienda La Bendición...") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = uiState.config.propietario,
                            onValueChange = { viewModel.onOwnerNameChange(it) },
                            label = { Text("Nombre del propietario / vendedor (opcional)") },
                            placeholder = { Text("Ej: Juan Bautista") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = uiState.config.telefonoNegocio,
                            onValueChange = { viewModel.onBusinessPhoneChange(it) },
                            label = { Text("Teléfono de contacto del negocio") },
                            placeholder = { Text("Ej: 809-555-0199") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { viewModel.saveConfig() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar datos del negocio", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // WhatsApp Message Template Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = WhatsAppGreen
                            )
                            Text(
                                text = "Plantilla de recordatorio por WhatsApp",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Personaliza el mensaje que se enviará automáticamente a tus clientes:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = uiState.config.mensajeWhatsAppPlantilla,
                            onValueChange = { viewModel.onWhatsAppTemplateChange(it) },
                            label = { Text("Mensaje automático") },
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Toca para insertar variables:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            InputChip(
                                selected = false,
                                onClick = {
                                    viewModel.onWhatsAppTemplateChange("${uiState.config.mensajeWhatsAppPlantilla} {cliente}")
                                },
                                label = { Text("{cliente}", fontSize = 11.sp) }
                            )
                            InputChip(
                                selected = false,
                                onClick = {
                                    viewModel.onWhatsAppTemplateChange("${uiState.config.mensajeWhatsAppPlantilla} {saldo}")
                                },
                                label = { Text("{saldo}", fontSize = 11.sp) }
                            )
                            InputChip(
                                selected = false,
                                onClick = {
                                    viewModel.onWhatsAppTemplateChange("${uiState.config.mensajeWhatsAppPlantilla} {negocio}")
                                },
                                label = { Text("{negocio}", fontSize = 11.sp) }
                            )
                        }

                        Button(
                            onClick = { viewModel.saveConfig() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar plantilla de WhatsApp", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Legal & Compliance Card (Blindaje Jurídico y Protección)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Aviso Legal y Privacidad (Protección)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Local & Offline Guarantee badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AbonoGreenContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AbonoGreen
                                )
                                Column {
                                    Text(
                                        text = "100% Privado y en tu Teléfono",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = AbonoGreen
                                    )
                                    Text(
                                        text = "Ningún dato de tus clientes se sube a internet ni servidores. No existe rastreo ni venta de datos.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Text(
                            text = LegalContent.DISCLAIMER_SUMMARY,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    activeLegalDialogTitle = "Términos de Servicio"
                                    activeLegalDialogContent = LegalContent.TERMS_OF_SERVICE
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Términos", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    activeLegalDialogTitle = "Política de Privacidad"
                                    activeLegalDialogContent = LegalContent.PRIVACY_POLICY
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Policy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Privacidad", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Backup & Export 100% Offline Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backup,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Respaldo y Exportación (100% Offline)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Tus datos viven únicamente en este teléfono. Puedes exportar una copia de seguridad para no perder tus clientes si cambias de celular.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Backup Button
                        FilledTonalButton(
                            onClick = {
                                viewModel.generateBackup { json ->
                                    shareText(context, json, "Copia de Seguridad - Mi Libreta")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exportar copia de seguridad (JSON)", fontWeight = FontWeight.Bold)
                        }

                        // Restore Button
                        OutlinedButton(
                            onClick = { showRestoreDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restaurar copia de seguridad")
                        }

                        // Export CSV
                        OutlinedButton(
                            onClick = {
                                onExportCsv { csv ->
                                    shareText(context, csv, "Reporte de Cuentas por Cobrar (CSV)")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exportar lista de clientes a Excel (CSV)")
                        }
                    }
                }
            }

            // About Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Mi Libreta • Versión ${LegalContent.APP_VERSION}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Desarrollada para colmados, tiendas y vendedores independientes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Legal Document Dialog
        if (activeLegalDialogTitle != null && activeLegalDialogContent != null) {
            AlertDialog(
                onDismissRequest = {
                    activeLegalDialogTitle = null
                    activeLegalDialogContent = null
                },
                title = { Text(activeLegalDialogTitle!!) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = activeLegalDialogContent!!,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            activeLegalDialogTitle = null
                            activeLegalDialogContent = null
                        }
                    ) {
                        Text("Entendido y Acepto")
                    }
                }
            )
        }

        // Restore JSON Dialog with Safety Warning
        if (showRestoreDialog) {
            AlertDialog(
                onDismissRequest = { showRestoreDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("Restaurar Copia de Seguridad") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Pega aquí el contenido JSON de tu copia de seguridad para incorporar los clientes y movimientos a tu libreta local:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = restoreJsonText,
                            onValueChange = { restoreJsonText = it },
                            placeholder = { Text("{\"version\": 2, \"clientes\": [...]}") },
                            minLines = 4,
                            maxLines = 8,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "⚠️ Asegúrate de que el contenido provenga de una copia legítima exportada por esta app.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.restoreBackup(restoreJsonText) { success, _ ->
                                if (success) {
                                    showRestoreDialog = false
                                    restoreJsonText = ""
                                }
                            }
                        },
                        enabled = restoreJsonText.isNotBlank()
                    ) {
                        Text("Confirmar y Restaurar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showRestoreDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
