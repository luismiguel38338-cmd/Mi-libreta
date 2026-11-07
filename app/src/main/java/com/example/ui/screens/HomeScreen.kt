package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ClienteConSaldo
import com.example.ui.theme.AbonoGreen
import com.example.ui.theme.AbonoGreenContainer
import com.example.ui.theme.DebtRed
import com.example.ui.theme.DebtRedContainer
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateUtils
import com.example.ui.viewmodel.FilterMode
import com.example.ui.viewmodel.HomeUiState
import com.example.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddClient: () -> Unit,
    onNavigateToClientDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val businessTitle = uiState.businessConfig?.nombreNegocio?.takeIf { it.isNotBlank() } ?: "Mi Libreta"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = businessTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Control de deudas de clientes",
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddClient,
                icon = { Icon(Icons.Default.Add, contentDescription = "Agregar cliente") },
                text = { Text("Nuevo cliente", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_client_fab")
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("home_clients_list"),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Summary Card Section
                item {
                    SummaryHeaderCard(uiState = uiState)
                }

                // Search Bar and Filter Chips
                item {
                    SearchAndFilterSection(
                        searchQuery = uiState.searchQuery,
                        filterMode = uiState.filterMode,
                        debtorsCount = uiState.debtorsCount,
                        upToDateCount = uiState.upToDateCount,
                        overdueCount = uiState.overdueCount,
                        nearLimitCount = uiState.nearLimitCount,
                        totalCount = uiState.totalClientsCount,
                        onSearchChange = { viewModel.onSearchQueryChanged(it) },
                        onFilterChange = { viewModel.onFilterModeChanged(it) }
                    )
                }

                // Empty State or List
                if (uiState.clients.isEmpty()) {
                    item {
                        EmptyStateView(
                            isSearching = uiState.searchQuery.isNotEmpty() || uiState.filterMode != FilterMode.ALL,
                            totalClientsCount = uiState.totalClientsCount,
                            searchQuery = uiState.searchQuery,
                            onClearSearch = {
                                viewModel.onSearchQueryChanged("")
                                viewModel.onFilterModeChanged(FilterMode.ALL)
                            },
                            onAddFirstClient = onNavigateToAddClient
                        )
                    }
                } else {
                    items(
                        items = uiState.clients,
                        key = { it.id }
                    ) { client ->
                        ClientItemCard(
                            client = client,
                            onClick = { onNavigateToClientDetail(client.id) },
                            onCallClick = {
                                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${client.telefono}")
                                }
                                context.startActivity(callIntent)
                            },
                            onWhatsAppClick = {
                                sendWhatsAppReminder(
                                    context = context,
                                    config = uiState.businessConfig,
                                    clientName = client.nombre,
                                    phone = client.telefono,
                                    saldo = client.saldo
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryHeaderCard(uiState: HomeUiState) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "TOTAL QUE ME DEBEN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.totalClientsCount} clientes",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Large debt amount in RD$
            Text(
                text = CurrencyFormatter.formatRD(uiState.totalOwed),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.testTag("total_debt_text")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-metrics badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Debtors pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (uiState.debtorsCount > 0) DebtRedContainer else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (uiState.debtorsCount > 0) DebtRed else Color.Gray,
                                    CircleShape
                                )
                        )
                        Column {
                            Text(
                                text = "${uiState.debtorsCount} con deuda",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.debtorsCount > 0) DebtRed else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pendientes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Up to date pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AbonoGreenContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AbonoGreen
                        )
                        Column {
                            Text(
                                text = "${uiState.upToDateCount} al día",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AbonoGreen
                            )
                            Text(
                                text = "Sin pendientes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    filterMode: FilterMode,
    debtorsCount: Int,
    upToDateCount: Int,
    overdueCount: Int,
    nearLimitCount: Int,
    totalCount: Int,
    onSearchChange: (String) -> Unit,
    onFilterChange: (FilterMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Buscar cliente por nombre o teléfono...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_client_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontally Scrollable Filter Chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterMode == FilterMode.ALL,
                onClick = { onFilterChange(FilterMode.ALL) },
                label = { Text("Todos ($totalCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.testTag("filter_all_chip")
            )

            FilterChip(
                selected = filterMode == FilterMode.WITH_DEBT,
                onClick = { onFilterChange(FilterMode.WITH_DEBT) },
                label = { Text("Con deuda ($debtorsCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DebtRedContainer,
                    selectedLabelColor = DebtRed
                ),
                modifier = Modifier.testTag("filter_debt_chip")
            )

            FilterChip(
                selected = filterMode == FilterMode.UP_TO_DATE,
                onClick = { onFilterChange(FilterMode.UP_TO_DATE) },
                label = { Text("Al día ($upToDateCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AbonoGreenContainer,
                    selectedLabelColor = AbonoGreen
                ),
                modifier = Modifier.testTag("filter_uptodate_chip")
            )

            if (overdueCount > 0) {
                FilterChip(
                    selected = filterMode == FilterMode.OVERDUE,
                    onClick = { onFilterChange(FilterMode.OVERDUE) },
                    label = { Text("⚠️ Vencidos ($overdueCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DebtRedContainer,
                        selectedLabelColor = DebtRed
                    )
                )
            }

            if (nearLimitCount > 0) {
                FilterChip(
                    selected = filterMode == FilterMode.NEAR_LIMIT,
                    onClick = { onFilterChange(FilterMode.NEAR_LIMIT) },
                    label = { Text("Cerca de límite ($nearLimitCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFE0B2),
                        selectedLabelColor = Color(0xFFE65100)
                    )
                )
            }
        }
    }
}

@Composable
fun ClientItemCard(
    client: ClienteConSaldo,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    val owesMoney = client.saldo > 0
    val saldoColor = if (owesMoney) DebtRed else AbonoGreen
    val saldoContainerColor = if (owesMoney) DebtRedContainer else AbonoGreenContainer
    val isOverdue = client.isPaymentOverdue()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(onClick = onClick)
            .testTag("client_card_${client.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Initials Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (owesMoney) DebtRedContainer.copy(alpha = 0.7f) else AbonoGreenContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = client.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (owesMoney) DebtRed else AbonoGreen
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Client Info (Name, Phone, Note)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = client.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (client.telefono.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = client.telefono,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (client.nota.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = client.nota,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Saldo and status pill
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = CurrencyFormatter.formatRD(client.saldo),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = saldoColor
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = saldoContainerColor
                    ) {
                        Text(
                            text = if (owesMoney) "Debe" else "Al día ✓",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = saldoColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Ver detalles",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            // Quick Indicators & Action Row (Due date / Credit limit / Quick contact)
            if (isOverdue || client.fechaPromesaPago != null || client.limiteCredito > 0.0 || client.telefono.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Due Date / Credit Warning
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isOverdue) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DebtRedContainer
                            ) {
                                Text(
                                    text = "⚠️ Vencido",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DebtRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else if (client.fechaPromesaPago != null && owesMoney) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = DateUtils.formatDueDate(client.fechaPromesaPago),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (client.limiteCredito > 0.0) {
                            val usage = client.creditLimitUsage ?: 0f
                            Text(
                                text = "Límite: ${CurrencyFormatter.formatRD(client.limiteCredito)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (usage > 1.0f) DebtRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Direct Call & WhatsApp buttons
                    if (client.telefono.isNotBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = onCallClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Llamar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            if (owesMoney) {
                                IconButton(
                                    onClick = onWhatsAppClick,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = "WhatsApp",
                                        tint = WhatsAppGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    isSearching: Boolean,
    totalClientsCount: Int,
    searchQuery: String,
    onClearSearch: () -> Unit,
    onAddFirstClient: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSearching) Icons.Default.Search else Icons.Default.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isSearching) {
                Text(
                    text = "No se encontraron clientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (searchQuery.isNotEmpty()) {
                        "No hay resultados para \"$searchQuery\" con los filtros actuales."
                    } else {
                        "No hay clientes en esta categoría."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                OutlinedButton(
                    onClick = onClearSearch,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Limpiar filtros")
                }
            } else {
                Text(
                    text = "Aún no tienes clientes registrados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Empieza a llevar el control de fiados y deudas de tu colmado o negocio de forma fácil y 100% segura en tu teléfono.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.Button(
                    onClick = onAddFirstClient,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Agregar mi primer cliente")
                }
            }
        }
    }
}
