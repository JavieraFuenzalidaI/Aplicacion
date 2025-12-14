package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RegisterRequest
import com.example.aplicacion.data.remote.RegistroRepository
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface RegistroUiState {
    object Idle : RegistroUiState
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

            val request = RegisterRequest(
                nombre = nombre,
                correo = correo,
                contrasena = contrasena,
                fecha = fecha
            )

            _uiState.value = repository.registrarUsuario(request)
        }
    }

    fun resetState() {
        _uiState.value = RegistroUiState.Idle
    }
}
