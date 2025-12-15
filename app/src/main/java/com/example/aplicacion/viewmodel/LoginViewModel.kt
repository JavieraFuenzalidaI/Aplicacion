package com.example.aplicacion.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.LoginRequest
import com.example.aplicacion.data.remote.LoginResponse
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.handleApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val loginResponse: LoginResponse) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun iniciarSesion(correo: String, contrasena: String, context: Context) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val request = LoginRequest(correo, contrasena)
                val response = RetrofitClient.getInstance(context).instance.login(request)
                _uiState.value = LoginUiState.Success(handleApiResponse(response))
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
