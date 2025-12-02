package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados para la UI de la pantalla de sesión iniciada
sealed interface UsuarioUiState {
    object Loading : UsuarioUiState
    data class Success(val usuario: Usuario) : UsuarioUiState
    data class Error(val message: String) : UsuarioUiState
}

class SesionIniciadaViewModel : ViewModel() {

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
                    val errorBody = response.errorBody()?.string() ?: "Usuario no encontrado"
                    _uiState.value = UsuarioUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _uiState.value = UsuarioUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
