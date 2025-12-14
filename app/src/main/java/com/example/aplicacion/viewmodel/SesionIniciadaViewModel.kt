package com.example.aplicacion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
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

class SesionIniciadaViewModel : ViewModel() {

    // Cambiamos el estado para que pueda ser nulo al principio
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun cargarUsuarioPorId(id: Int) {
        // Evitamos llamadas innecesarias si el ID es 0 o inválido
        if (id <= 0) return

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getUsuario(id.toString())
                if (response.isSuccessful) {
                    _usuario.value = response.body()
                } else {
                    // Si hay un error en la respuesta, lo dejamos nulo
                    _usuario.value = null
                    println("Error al cargar usuario: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // Si hay un error de conexión, también lo dejamos nulo
                _usuario.value = null
                println("Error de conexión: ${e.message}")
            }
        }
    }
}
