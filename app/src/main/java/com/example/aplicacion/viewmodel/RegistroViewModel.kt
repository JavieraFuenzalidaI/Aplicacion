package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RegistroRepository
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

class RegistroViewModel(
    private val repository: RegistroRepository = RegistroRepository(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Idle)
    val uiState: StateFlow<RegistroUiState> = _uiState

    fun registrarUsuario(nombre: String, correo: String, contrasena: String, fecha: String) {
        viewModelScope.launch(dispatcher) {
            _uiState.value = RegistroUiState.Loading
            val nuevoUsuario = Usuario(
                id = 0, 
                nombre = nombre, 
                correo = correo, 
                contrasena = contrasena, 
                fecha = fecha, 
                nivel = 0,
                // AÑADIMOS EL ROL POR DEFECTO
                rol = "usuario"
            )
            _uiState.value = repository.registrarUsuario(nuevoUsuario)
        }
    }

    // Función para resetear el estado después de una operación.
    fun resetState() {
        _uiState.value = RegistroUiState.Idle
    }
}
