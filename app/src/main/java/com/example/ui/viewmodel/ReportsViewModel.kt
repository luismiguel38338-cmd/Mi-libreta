package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ClienteConSaldo
import com.example.data.model.Movimiento
import com.example.data.model.TipoMovimiento
import com.example.data.repository.LibretaRepository
import com.example.ui.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TopDebtor(
    val name: String,
    val phone: String,
    val saldo: Double,
    val percentageOfTotal: Float
)

data class ReportsUiState(
    val totalOutstandingDebt: Double = 0.0,
    val totalDebtMonth: Double = 0.0,
    val totalCollectedMonth: Double = 0.0,
    val totalDebtToday: Double = 0.0,
    val totalCollectedToday: Double = 0.0,
    val recoveryRate: Float = 0f,
    val totalDebtorsCount: Int = 0,
    val totalUpToDateCount: Int = 0,
    val topDebtors: List<TopDebtor> = emptyList(),
    val totalMovementsCount: Int = 0,
    val isLoading: Boolean = false
)

class ReportsViewModel(
    private val repository: LibretaRepository
) : ViewModel() {

    val uiState: StateFlow<ReportsUiState> = combine(
        repository.clientesConSaldo,
        repository.allMovimientos
    ) { clients, movements ->
        val totalOutstanding = clients.filter { it.saldo > 0 }.sumOf { it.saldo }
        val startOfToday = DateUtils.getStartOfToday()
        val startOfMonth = DateUtils.getStartOfMonth()

        var debtMonth = 0.0
        var collMonth = 0.0
        var debtToday = 0.0
        var collToday = 0.0
        var totalHistoricalDebt = 0.0
        var totalHistoricalColl = 0.0

        for (m in movements) {
            if (m.tipo == TipoMovimiento.DEUDA) {
                totalHistoricalDebt += m.monto
                if (m.fecha >= startOfMonth) debtMonth += m.monto
                if (m.fecha >= startOfToday) debtToday += m.monto
            } else {
                totalHistoricalColl += m.monto
                if (m.fecha >= startOfMonth) collMonth += m.monto
                if (m.fecha >= startOfToday) collToday += m.monto
            }
        }

        val recoveryRate = if (totalHistoricalDebt > 0.0) {
            ((totalHistoricalColl / totalHistoricalDebt) * 100f).toFloat().coerceIn(0f, 100f)
        } else 100f

        val topDebtors = clients
            .filter { it.saldo > 0 }
            .sortedByDescending { it.saldo }
            .take(5)
            .map { c ->
                TopDebtor(
                    name = c.nombre,
                    phone = c.telefono,
                    saldo = c.saldo,
                    percentageOfTotal = if (totalOutstanding > 0) (c.saldo / totalOutstanding).toFloat() else 0f
                )
            }

        ReportsUiState(
            totalOutstandingDebt = totalOutstanding,
            totalDebtMonth = debtMonth,
            totalCollectedMonth = collMonth,
            totalDebtToday = debtToday,
            totalCollectedToday = collToday,
            recoveryRate = recoveryRate,
            totalDebtorsCount = clients.count { it.saldo > 0 },
            totalUpToDateCount = clients.count { it.saldo <= 0 },
            topDebtors = topDebtors,
            totalMovementsCount = movements.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState(isLoading = true)
    )

    class Factory(private val repository: LibretaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportsViewModel(repository) as T
        }
    }
}
