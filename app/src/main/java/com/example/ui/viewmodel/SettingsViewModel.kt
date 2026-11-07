package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.NegocioConfig
import com.example.data.repository.LibretaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val config: NegocioConfig = NegocioConfig(),
    val isSaving: Boolean = false,
    val message: String? = null,
    val backupJson: String? = null
)

class SettingsViewModel(
    private val repository: LibretaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.config.collect { conf ->
                if (conf != null) {
                    _uiState.value = _uiState.value.copy(config = conf)
                }
            }
        }
    }

    fun onBusinessNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(nombreNegocio = name)
        )
    }

    fun onOwnerNameChange(owner: String) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(propietario = owner)
        )
    }

    fun onBusinessPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(telefonoNegocio = phone)
        )
    }

    fun onWhatsAppTemplateChange(template: String) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(mensajeWhatsAppPlantilla = template)
        )
    }

    fun saveConfig() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            repository.saveConfig(_uiState.value.config)
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                message = "Configuración del negocio guardada exitosamente"
            )
        }
    }

    fun generateBackup(onReady: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportBackupJson()
            _uiState.value = _uiState.value.copy(backupJson = json)
            onReady(json)
        }
    }

    fun restoreBackup(jsonString: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.restoreBackupJson(jsonString)
            if (result.isSuccess) {
                val count = result.getOrNull() ?: 0
                val msg = "Copia de seguridad restaurada: $count clientes procesados."
                _uiState.value = _uiState.value.copy(message = msg)
                onComplete(true, msg)
            } else {
                val errorMsg = "Error al restaurar: ${result.exceptionOrNull()?.message ?: "Formato no válido"}"
                _uiState.value = _uiState.value.copy(message = errorMsg)
                onComplete(false, errorMsg)
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    class Factory(private val repository: LibretaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
