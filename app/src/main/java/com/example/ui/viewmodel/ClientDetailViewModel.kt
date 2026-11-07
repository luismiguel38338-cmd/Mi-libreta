package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ClienteConSaldo
import com.example.data.model.Movimiento
import com.example.data.model.NegocioConfig
import com.example.data.model.TipoMovimiento
import com.example.data.repository.LibretaRepository
import com.example.ui.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class DetailExtraState(
    val showDeleteClientDialog: Boolean = false,
    val movementToDelete: Movimiento? = null,
    val showMovementDialog: Boolean = false,
    val movementDialogType: TipoMovimiento = TipoMovimiento.DEUDA,
    val userMessage: String? = null
)

data class ClientDetailUiState(
    val client: ClienteConSaldo? = null,
    val movements: List<Movimiento> = emptyList(),
    val businessConfig: NegocioConfig? = null,
    val isLoading: Boolean = true,
    val showDeleteClientDialog: Boolean = false,
    val movementToDelete: Movimiento? = null,
    val showMovementDialog: Boolean = false,
    val movementDialogType: TipoMovimiento = TipoMovimiento.DEUDA,
    val userMessage: String? = null
)

class ClientDetailViewModel(
    private val repository: LibretaRepository,
    private val clienteId: Long
) : ViewModel() {

    private val _extraState = MutableStateFlow(DetailExtraState())

    val uiState: StateFlow<ClientDetailUiState> = combine(
        repository.getClienteConSaldo(clienteId),
        repository.getMovimientos(clienteId),
        repository.config,
        _extraState
    ) { client, movements, config, extra ->
        ClientDetailUiState(
            client = client,
            movements = movements,
            businessConfig = config,
            isLoading = false,
            showDeleteClientDialog = extra.showDeleteClientDialog,
            movementToDelete = extra.movementToDelete,
            showMovementDialog = extra.showMovementDialog,
            movementDialogType = extra.movementDialogType,
            userMessage = extra.userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ClientDetailUiState(isLoading = true)
    )

    fun openAddDebtDialog() {
        _extraState.update {
            it.copy(
                movementDialogType = TipoMovimiento.DEUDA,
                showMovementDialog = true
            )
        }
    }

    fun openAddAbonoDialog() {
        val currentClient = uiState.value.client
        if (currentClient != null && currentClient.saldo <= 0) {
            _extraState.update {
                it.copy(userMessage = "Este cliente no tiene deuda pendiente para abonar.")
            }
            return
        }
        _extraState.update {
            it.copy(
                movementDialogType = TipoMovimiento.ABONO,
                showMovementDialog = true
            )
        }
    }

    fun dismissMovementDialog() {
        _extraState.update { it.copy(showMovementDialog = false) }
    }

    /**
     * Adds a movement with strict validation:
     * - Amount must be > 0
     * - If type is ABONO, amount cannot exceed current balance
     */
    fun addMovement(
        tipo: TipoMovimiento,
        monto: Double,
        nota: String,
        fecha: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (monto <= 0.0) {
            onError("El monto debe ser mayor que RD$ 0.00")
            return
        }

        val currentClient = uiState.value.client
        if (tipo == TipoMovimiento.ABONO) {
            val currentSaldo = currentClient?.saldo ?: 0.0
            if (monto > currentSaldo) {
                val formattedMonto = CurrencyFormatter.formatRD(monto)
                val formattedSaldo = CurrencyFormatter.formatRD(currentSaldo)
                onError("El abono ($formattedMonto) no puede ser mayor que el saldo pendiente ($formattedSaldo).")
                return
            }
        }

        viewModelScope.launch {
            repository.insertMovimiento(
                Movimiento(
                    clienteId = clienteId,
                    tipo = tipo,
                    monto = monto,
                    nota = nota.trim(),
                    fecha = fecha
                )
            )
            _extraState.update { it.copy(showMovementDialog = false) }
            onSuccess()
        }
    }

    fun promptDeleteMovement(movimiento: Movimiento) {
        _extraState.update { it.copy(movementToDelete = movimiento) }
    }

    fun dismissDeleteMovementPrompt() {
        _extraState.update { it.copy(movementToDelete = null) }
    }

    fun confirmDeleteMovement() {
        val mov = _extraState.value.movementToDelete ?: return
        viewModelScope.launch {
            repository.deleteMovimiento(mov.id)
            _extraState.update {
                it.copy(
                    movementToDelete = null,
                    userMessage = "Movimiento eliminado. El saldo ha sido recalculado."
                )
            }
        }
    }

    fun promptDeleteClient() {
        _extraState.update { it.copy(showDeleteClientDialog = true) }
    }

    fun dismissDeleteClientPrompt() {
        _extraState.update { it.copy(showDeleteClientDialog = false) }
    }

    fun confirmDeleteClient(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCliente(clienteId)
            _extraState.update { it.copy(showDeleteClientDialog = false) }
            onDeleted()
        }
    }

    fun clearUserMessage() {
        _extraState.update { it.copy(userMessage = null) }
    }

    class Factory(
        private val repository: LibretaRepository,
        private val clienteId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClientDetailViewModel(repository, clienteId) as T
        }
    }
}
