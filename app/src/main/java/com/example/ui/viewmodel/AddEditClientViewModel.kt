package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Cliente
import com.example.data.repository.LibretaRepository
import com.example.ui.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditClientUiState(
    val clienteId: Long = 0,
    val isEditing: Boolean = false,
    val nombre: String = "",
    val telefono: String = "",
    val nota: String = "",
    val limiteCredito: String = "",
    val fechaPromesaPago: Long? = null,
    val nombreError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

class AddEditClientViewModel(
    private val repository: LibretaRepository,
    private val clienteId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditClientUiState(clienteId = clienteId, isEditing = clienteId > 0))
    val uiState: StateFlow<AddEditClientUiState> = _uiState.asStateFlow()

    init {
        if (clienteId > 0) {
            viewModelScope.launch {
                val client = repository.getCliente(clienteId).firstOrNull()
                if (client != null) {
                    _uiState.update {
                        it.copy(
                            nombre = client.nombre,
                            telefono = client.telefono,
                            nota = client.nota,
                            limiteCredito = if (client.limiteCredito > 0.0) client.limiteCredito.toString() else "",
                            fechaPromesaPago = client.fechaPromesaPago,
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun onNombreChange(newValue: String) {
        _uiState.update {
            it.copy(
                nombre = newValue,
                nombreError = if (newValue.trim().isEmpty()) "El nombre del cliente no puede estar vacío" else null
            )
        }
    }

    fun onTelefonoChange(newValue: String) {
        _uiState.update { it.copy(telefono = newValue) }
    }

    fun onNotaChange(newValue: String) {
        _uiState.update { it.copy(nota = newValue) }
    }

    fun onLimiteCreditoChange(newValue: String) {
        val filtered = newValue.filter { it.isDigit() || it == '.' }
        if (filtered.count { it == '.' } <= 1) {
            _uiState.update { it.copy(limiteCredito = filtered) }
        }
    }

    fun onFechaPromesaPagoChange(timestamp: Long?) {
        _uiState.update { it.copy(fechaPromesaPago = timestamp) }
    }

    fun saveClient(onSuccess: () -> Unit) {
        val current = _uiState.value
        val trimmedName = current.nombre.trim()

        if (trimmedName.isEmpty()) {
            _uiState.update {
                it.copy(nombreError = "El nombre del cliente es obligatorio")
            }
            return
        }

        val parsedLimit = CurrencyFormatter.parseAmount(current.limiteCredito) ?: 0.0

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            if (current.isEditing) {
                val existing = repository.getCliente(current.clienteId).firstOrNull()
                val updated = Cliente(
                    id = current.clienteId,
                    nombre = trimmedName,
                    telefono = current.telefono.trim(),
                    nota = current.nota.trim(),
                    fechaCreacion = existing?.fechaCreacion ?: System.currentTimeMillis(),
                    limiteCredito = parsedLimit,
                    fechaPromesaPago = current.fechaPromesaPago
                )
                repository.updateCliente(updated)
            } else {
                val newClient = Cliente(
                    nombre = trimmedName,
                    telefono = current.telefono.trim(),
                    nota = current.nota.trim(),
                    fechaCreacion = System.currentTimeMillis(),
                    limiteCredito = parsedLimit,
                    fechaPromesaPago = current.fechaPromesaPago
                )
                repository.insertCliente(newClient)
            }
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
            onSuccess()
        }
    }

    class Factory(
        private val repository: LibretaRepository,
        private val clienteId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditClientViewModel(repository, clienteId) as T
        }
    }
}
