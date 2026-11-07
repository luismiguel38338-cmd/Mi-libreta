package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AbonoGreen
import com.example.ui.theme.AbonoGreenContainer
import com.example.ui.theme.DebtRed
import com.example.ui.theme.DebtRedContainer
import com.example.ui.util.CurrencyFormatter
import com.example.ui.viewmodel.ReportsUiState
import com.example.ui.viewmodel.ReportsViewModel
import com.example.ui.viewmodel.TopDebtor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Estadísticas y Reportes",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Métricas financieras de tu negocio",
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
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Outstanding Card
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "CAPITAL TOTAL PENDIENTE DE COBRO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = CurrencyFormatter.formatRD(uiState.totalOutstandingDebt),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MetricBadge(
                                    label = "Clientes con deuda",
                                    value = "${uiState.totalDebtorsCount}",
                                    color = DebtRed,
                                    bgColor = DebtRedContainer
                                )
                                MetricBadge(
                                    label = "Clientes al día",
                                    value = "${uiState.totalUpToDateCount}",
                                    color = AbonoGreen,
                                    bgColor = AbonoGreenContainer
                                )
                            }
                        }
                    }
                }

                // Monthly Performance Card
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
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Rendimiento de este mes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                PerformanceBox(
                                    title = "Fiado este mes",
                                    amount = uiState.totalDebtMonth,
                                    color = DebtRed,
                                    bgColor = DebtRedContainer,
                                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                                    modifier = Modifier.weight(1f)
                                )

                                PerformanceBox(
                                    title = "Cobrado este mes",
                                    amount = uiState.totalCollectedMonth,
                                    color = AbonoGreen,
                                    bgColor = AbonoGreenContainer,
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Recovery rate progress bar
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tasa de recuperación:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${uiState.recoveryRate.toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.recoveryRate >= 70f) AbonoGreen else Color(0xFFE65100)
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { uiState.recoveryRate / 100f },
                                    color = if (uiState.recoveryRate >= 70f) AbonoGreen else Color(0xFFE65100),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Text(
                                    text = "Porcentaje de deudas que han sido saldadas en abonos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Today Activity Card
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
                            Text(
                                text = "Movimientos de hoy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                PerformanceBox(
                                    title = "Fiado hoy",
                                    amount = uiState.totalDebtToday,
                                    color = DebtRed,
                                    bgColor = DebtRedContainer,
                                    icon = Icons.Default.AttachMoney,
                                    modifier = Modifier.weight(1f)
                                )

                                PerformanceBox(
                                    title = "Cobrado hoy",
                                    amount = uiState.totalCollectedToday,
                                    color = AbonoGreen,
                                    bgColor = AbonoGreenContainer,
                                    icon = Icons.Default.MonetizationOn,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Top 5 Debtors Section
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Leaderboard,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Mayores deudas acumuladas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (uiState.topDebtors.isEmpty()) {
                                Text(
                                    text = "No hay clientes con deuda pendiente actualmente. ¡Tu negocio está al día!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                uiState.topDebtors.forEachIndexed { index, debtor ->
                                    TopDebtorRow(index = index + 1, debtor = debtor)
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
fun MetricBadge(label: String, value: String, color: Color, bgColor: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = "$value $label",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun PerformanceBox(
    title: String,
    amount: Double,
    color: Color,
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
            Text(
                text = CurrencyFormatter.formatRD(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}

@Composable
fun TopDebtorRow(index: Int, debtor: TopDebtor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = debtor.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            LinearProgressIndicator(
                progress = { debtor.percentageOfTotal },
                color = DebtRed,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = CurrencyFormatter.formatRD(debtor.saldo),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = DebtRed
        )
    }
}
