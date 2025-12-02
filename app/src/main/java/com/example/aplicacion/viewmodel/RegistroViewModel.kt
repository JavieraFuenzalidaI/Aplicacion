package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados para la UI de la pantalla de registro
sealed interface RegistroUiState {
    object Idle : RegistroUiState // Estado inicial
    object Loading : RegistroUiState
    data class Success(val usuario: Usuario) : RegistroUiState
    data class Error(val message: String) : RegistroUiState
}

class RegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Idle)
    val uiState: StateFlow<RegistroUiState> = _uiState

    fun registrarUsuario(nombre: String, correo: String, contrasena: String, fecha: String) {
        viewModelScope.launch {
            _uiState.value = RegistroUiState.Loading
            try {
                // Creamos un objeto Usuario para enviar a la API.
                // El 'id' se puede poner a 0 porque la base de datos lo generará automáticamente.
                // El 'nivel' se puede poner a 0 como valor inicial.
                val nuevoUsuario = Usuario(
                    id = 0, 
                    nombre = nombre, 
                    correo = correo, 
                    contrasena = contrasena, 
                    fecha = fecha, 
                    nivel = 0
                )

                val response = RetrofitClient.instance.createUsuario(nuevoUsuario)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = RegistroUiState.Success(response.body()!!)
                } else {
                    // Si la API devuelve un error (ej: correo duplicado), lo mostramos.
                    val errorMsg = response.errorBody()?.string() ?: "Error al registrar."
                    _uiState.value = RegistroUiState.Error(errorMsg)
                }

            } catch (e: Exception) {
                _uiState.value = RegistroUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // Función para resetear el estado después de una operación.
    fun resetState() {
        _uiState.value = RegistroUiState.Idle
    }
}
