package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.LoginRequest
import com.example.aplicacion.data.remote.LoginResponse
import com.example.aplicacion.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val loginResponse: LoginResponse) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    val uiState: StateFlow<LoginUiState> = _uiState


    fun iniciarSesion(correo: String, contrasena: String) {

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                val request = LoginRequest(correo, contrasena)
                val response = RetrofitClient.instance.login(request)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = LoginUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _uiState.value = LoginUiState.Error(errorBody)
                }

            } catch (e: Exception) {
                // Error de red (ej. sin conexión): Actualizar la UI con el mensaje de error
                _uiState.value = LoginUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    // Función para resetear el estado si el usuario quiere reintentar
    fun resetState(){
        _uiState.value = LoginUiState.Idle
    }
}
