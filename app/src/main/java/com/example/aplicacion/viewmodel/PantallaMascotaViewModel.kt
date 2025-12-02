package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Tarea
import com.example.aplicacion.data.remote.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Clase de datos para contener la información combinada de la pantalla
data class MascotaScreenData(
    val usuario: Usuario,
    val tareas: List<Tarea>
)

// Estados de la UI para la pantalla de la mascota
sealed interface MascotaUiState {
    object Loading : MascotaUiState
    data class Success(val data: MascotaScreenData) : MascotaUiState
    data class Error(val message: String) : MascotaUiState
}

class PantallaMascotaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MascotaUiState>(MascotaUiState.Loading)
    val uiState: StateFlow<MascotaUiState> = _uiState

    fun cargarDatosMascota(usuarioId: Int) {
        // Si el ID es -1 (admin), no cargamos nada.
        if (usuarioId == -1) {
            val admin = Usuario(-1, "Administrador", "admin", "", "", 100)
            _uiState.value = MascotaUiState.Success(MascotaScreenData(admin, emptyList()))
            return
        }

        viewModelScope.launch {
            _uiState.value = MascotaUiState.Loading
            try {
                val usuarioResponse = RetrofitClient.instance.getUsuario(usuarioId)
                val tareasResponse = RetrofitClient.instance.getTareasPorUsuario(usuarioId)

                if (usuarioResponse.isSuccessful && usuarioResponse.body() != null &&
                    tareasResponse.isSuccessful && tareasResponse.body() != null
                ) {
                    val screenData = MascotaScreenData(usuarioResponse.body()!!, tareasResponse.body()!!)
                    _uiState.value = MascotaUiState.Success(screenData)
                } else {
                    val errorMsg = (usuarioResponse.errorBody()?.string() ?: "Error al cargar usuario") + " | " +
                            (tareasResponse.errorBody()?.string() ?: "Error al cargar tareas")
                    _uiState.value = MascotaUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = MascotaUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun agregarTarea(usuarioId: Int, descripcion: String) {
        viewModelScope.launch {
            if (descripcion.isBlank()) return@launch
            try {
                val nuevaTarea = Tarea(0, usuarioId, descripcion, 10, 0) // API genera ID
                val response = RetrofitClient.instance.createTarea(nuevaTarea)
                if (response.isSuccessful) {
                    cargarDatosMascota(usuarioId) // Recargamos para ver la nueva tarea
                }
            } catch (e: Exception) {
                _uiState.value = MascotaUiState.Error("Error de red al crear tarea: ${e.message}")
            }
        }
    }

    fun completarTarea(tarea: Tarea, nuevoNivel: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is MascotaUiState.Success) {
                try {
                    // 1. Marcamos la tarea como completada
                    val tareaActualizada = tarea.copy(completado = 1)
                    RetrofitClient.instance.updateTarea(tarea.id, tareaActualizada)

                    // 2. Actualizamos el nivel del usuario
                    val usuarioActual = currentState.data.usuario
                    val usuarioActualizado = usuarioActual.copy(nivel = nuevoNivel)
                    RetrofitClient.instance.updateUsuario(usuarioActual.id, usuarioActualizado)

                    // 3. Recargamos todo para mantener la UI consistente
                    cargarDatosMascota(usuarioActual.id)

                } catch (e: Exception) {
                    _uiState.value = MascotaUiState.Error("Error de red al completar tarea: ${e.message}")
                }
            }
        }
    }
}
