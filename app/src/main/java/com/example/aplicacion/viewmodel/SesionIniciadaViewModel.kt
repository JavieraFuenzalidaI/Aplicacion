package com.example.aplicacion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estado para la carga del usuario desde la API
sealed interface UsuarioUiState {
    data class Success(val usuario: Usuario) : UsuarioUiState
    data class Error(val message: String) : UsuarioUiState
    object Loading : UsuarioUiState
}

class SesionIniciadaViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UsuarioUiState>(UsuarioUiState.Loading)
    val uiState: StateFlow<UsuarioUiState> = _uiState

    fun cargarUsuario(correo: String) {
        viewModelScope.launch {
            _uiState.value = UsuarioUiState.Loading
            try {
                val response = RetrofitClient.instance.getUsuarioPorCorreo(correo)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = UsuarioUiState.Success(response.body()!!)
                } else {
                    _uiState.value = UsuarioUiState.Error("Error al cargar el usuario.")
                }
            } catch (e: Exception) {
                _uiState.value = UsuarioUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
