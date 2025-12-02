package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados para la UI de la pantalla de ver usuarios
sealed interface VerUsuariosUiState {
    object Loading : VerUsuariosUiState
    data class Success(val usuarios: List<Usuario>) : VerUsuariosUiState
    data class Error(val message: String) : VerUsuariosUiState
}

class VerUsuariosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<VerUsuariosUiState>(VerUsuariosUiState.Loading)
    val uiState: StateFlow<VerUsuariosUiState> = _uiState

    // Función para cargar la lista inicial de usuarios
    fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.value = VerUsuariosUiState.Loading
            try {
                val response = RetrofitClient.instance.getUsuarios()
                if (response.isSuccessful && response.body() != null) {
                    // Filtramos al admin para que no aparezca en la lista
                    val usuariosSinAdmin = response.body()!!.filter { it.correo != "admin" }
                    _uiState.value = VerUsuariosUiState.Success(usuariosSinAdmin)
                } else {
                    _uiState.value = VerUsuariosUiState.Error(response.errorBody()?.string() ?: "Error al cargar usuarios")
                }
            } catch (e: Exception) {
                _uiState.value = VerUsuariosUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // Función para eliminar un usuario y refrescar la lista
    fun eliminarUsuario(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.deleteUsuario(id)
                if (response.isSuccessful) {
                    // Si se elimina con éxito, volvemos a cargar la lista para que se actualice la UI.
                    cargarUsuarios()
                } else {
                    // Si hay un error al eliminar, lo mostramos (o podríamos manejarlo de otra forma)
                    _uiState.value = VerUsuariosUiState.Error(response.errorBody()?.string() ?: "Error al eliminar el usuario")
                }
            } catch (e: Exception) {
                _uiState.value = VerUsuariosUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
