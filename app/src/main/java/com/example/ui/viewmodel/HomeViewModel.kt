package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ClienteConSaldo
import com.example.data.model.NegocioConfig
import com.example.data.repository.LibretaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterMode {
    ALL,
    WITH_DEBT,
    UP_TO_DATE,
    OVERDUE,
    NEAR_LIMIT
}

data class HomeUiState(
    val clients: List<ClienteConSaldo> = emptyList(),
    val allClientsSnapshot: List<ClienteConSaldo> = emptyList(),
    val businessConfig: NegocioConfig? = null,
    val searchQuery: String = "",
    val filterMode: FilterMode = FilterMode.ALL,
    val totalOwed: Double = 0.0,
    val debtorsCount: Int = 0,
    val upToDateCount: Int = 0,
    val overdueCount: Int = 0,
    val nearLimitCount: Int = 0,
    val totalClientsCount: Int = 0,
    val isLoading: Boolean = false,
    val userNotification: String? = null
)

class HomeViewModel(
    private val repository: LibretaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterMode = MutableStateFlow(FilterMode.ALL)
    private val _userNotification = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.clientesConSaldo,
        repository.config,
        _searchQuery,
        _filterMode,
        _userNotification
    ) { allClients, config, query, filter, notification ->
        val totalOwed = allClients.filter { it.saldo > 0 }.sumOf { it.saldo }
        val debtorsCount = allClients.count { it.saldo > 0 }
        val upToDateCount = allClients.count { it.saldo <= 0 }
        val overdueCount = allClients.count { it.isPaymentOverdue() }
        val nearLimitCount = allClients.count {
            it.limiteCredito > 0.0 && it.saldo >= it.limiteCredito * 0.8
        }
        val totalCount = allClients.size

        val queryTrimmed = query.trim()
        val searchFiltered = if (queryTrimmed.isEmpty()) {
            allClients
        } else {
            allClients.filter { client ->
                client.nombre.contains(queryTrimmed, ignoreCase = true) ||
                        client.telefono.contains(queryTrimmed, ignoreCase = true) ||
                        client.nota.contains(queryTrimmed, ignoreCase = true)
            }
        }

        val fullyFiltered = when (filter) {
            FilterMode.ALL -> searchFiltered
            FilterMode.WITH_DEBT -> searchFiltered.filter { it.saldo > 0 }
            FilterMode.UP_TO_DATE -> searchFiltered.filter { it.saldo <= 0 }
            FilterMode.OVERDUE -> searchFiltered.filter { it.isPaymentOverdue() }
            FilterMode.NEAR_LIMIT -> searchFiltered.filter {
                it.limiteCredito > 0.0 && it.saldo >= it.limiteCredito * 0.8
            }
        }

        HomeUiState(
            clients = fullyFiltered,
            allClientsSnapshot = allClients,
            businessConfig = config,
            searchQuery = query,
            filterMode = filter,
            totalOwed = totalOwed,
            debtorsCount = debtorsCount,
            upToDateCount = upToDateCount,
            overdueCount = overdueCount,
            nearLimitCount = nearLimitCount,
            totalClientsCount = totalCount,
            isLoading = false,
            userNotification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterModeChanged(mode: FilterMode) {
        _filterMode.value = mode
    }

    fun clearNotification() {
        _userNotification.value = null
    }

    fun exportDebtsCsv(onReady: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.exportDebtsAsCsv(uiState.value.allClientsSnapshot)
            onReady(csv)
        }
    }

    class Factory(private val repository: LibretaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
