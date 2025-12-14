package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.data.remote.AdminUpdateUserData
import com.example.aplicacion.data.remote.ModeratorUpdateUserData
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

// Estados para la lista de usuarios
sealed class VerUsuariosUiState {
    object Loading : VerUsuariosUiState()
    data class Success(val usuarios: List<Usuario>) : VerUsuariosUiState()
    data class Error(val message: String) : VerUsuariosUiState()
}

// Estados para la pantalla de edición
sealed class EditarUsuarioUiState {
    object Idle : EditarUsuarioUiState()
    object Loading : EditarUsuarioUiState()
    data class Success(val usuario: Usuario) : EditarUsuarioUiState()
    object UpdateSuccess : EditarUsuarioUiState()
    data class Error(val message: String) : EditarUsuarioUiState()
}

class VerUsuariosViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<VerUsuariosUiState>(VerUsuariosUiState.Loading)
    val uiState: StateFlow<VerUsuariosUiState> = _uiState.asStateFlow()

    private val _editUiState = MutableStateFlow<EditarUsuarioUiState>(EditarUsuarioUiState.Idle)
    val editUiState: StateFlow<EditarUsuarioUiState> = _editUiState.asStateFlow()

    fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.value = VerUsuariosUiState.Loading
            try {
                _uiState.value = VerUsuariosUiState.Success(repository.obtenerTodosLosUsuarios())
            } catch (e: Exception) {
                _uiState.value = VerUsuariosUiState.Error("Error al cargar usuarios: ${e.message}")
            }
        }
    }

    fun eliminarUsuario(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarUsuario(id)
                cargarUsuarios() // Recargamos la lista tras eliminar
            } catch (e: Exception) {
                _uiState.value = VerUsuariosUiState.Error("Error al eliminar: ${e.message}")
            }
        }
    }

    fun cargarUsuarioParaEditar(id: String) {
        viewModelScope.launch {
            _editUiState.value = EditarUsuarioUiState.Loading
            try {
                _editUiState.value = EditarUsuarioUiState.Success(repository.obtenerUsuarioPorId(id))
            } catch (e: Exception) {
                _editUiState.value = EditarUsuarioUiState.Error("No se pudo cargar el usuario: ${e.message}")
            }
        }
    }

    fun guardarCambiosAdmin(id: String, data: AdminUpdateUserData) {
        viewModelScope.launch {
            _editUiState.value = EditarUsuarioUiState.Loading
            try {
                repository.actualizarUsuarioAdmin(id, data)
                _editUiState.value = EditarUsuarioUiState.UpdateSuccess
            } catch (e: Exception) {
                _editUiState.value = EditarUsuarioUiState.Error("Error al guardar: ${e.message}")
            }
        }
    }

    fun guardarCambiosModerador(id: String, data: ModeratorUpdateUserData) {
        viewModelScope.launch {
            _editUiState.value = EditarUsuarioUiState.Loading
            try {
                repository.actualizarUsuarioModerador(id, data)
                _editUiState.value = EditarUsuarioUiState.UpdateSuccess
            } catch (e: Exception) {
                _editUiState.value = EditarUsuarioUiState.Error("Error al guardar: ${e.message}")
            }
        }
    }

    fun resetEditState() {
        _editUiState.value = EditarUsuarioUiState.Idle
    }
}
