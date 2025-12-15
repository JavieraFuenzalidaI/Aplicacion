package com.example.aplicacion.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.data.remote.CreateTareaRequest
import com.example.aplicacion.data.remote.Tarea
import com.example.aplicacion.data.remote.UpdateTareaRequest
import com.example.aplicacion.data.remote.Usuario
import com.example.aplicacion.util.StepCounter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MascotaScreenData(
    val usuario: Usuario,
    val tareas: List<Tarea>,
    val kilometros: Float = 0f
)

sealed interface MascotaUiState {
    object Loading : MascotaUiState
    data class Success(val data: MascotaScreenData) : MascotaUiState
    data class Error(val message: String) : MascotaUiState
}

class PantallaMascotaViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MascotaUiState>(MascotaUiState.Loading)
    val uiState: StateFlow<MascotaUiState> = _uiState

    private lateinit var stepCounter: StepCounter
    private var initialSteps = -1

    fun initializeStepCounter(application: Application) {
        if (::stepCounter.isInitialized) return
        stepCounter = StepCounter(application)
        viewModelScope.launch {
            stepCounter.getSteps().collect { steps ->
                if (initialSteps == -1) {
                    initialSteps = steps
                }
                val currentSteps = steps - initialSteps
                val kilometros = convertirPasosAKilometros(currentSteps)
                val currentState = _uiState.value
                if (currentState is MascotaUiState.Success) {
                    _uiState.value = currentState.copy(
                        data = currentState.data.copy(kilometros = kilometros)
                    )
                }
            }
        }
    }

    private fun convertirPasosAKilometros(pasos: Int): Float {
        return (pasos / 1312.0f)
    }

    fun cargarDatosMascota(usuarioId: Int) {
        if (usuarioId == 0) {
            val admin = Usuario(0, "Administrador", "admin@taskipet.com", "", "admin", 100, "admin")
            _uiState.value = MascotaUiState.Success(MascotaScreenData(admin, emptyList()))
            return
        }

        viewModelScope.launch {
            _uiState.value = MascotaUiState.Loading
            try {
                val usuario = repository.obtenerUsuarioPorId(usuarioId.toString())
                val tareas = repository.getTareasUsuario(usuarioId)
                val screenData = MascotaScreenData(usuario, tareas)
                _uiState.value = MascotaUiState.Success(screenData)
            } catch (e: Exception) {
                _uiState.value = MascotaUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun agregarTarea(usuarioId: Int, descripcion: String) {
        if (descripcion.isBlank()) return

        viewModelScope.launch {
            try {
                val request = CreateTareaRequest(usuarioId, descripcion, 15)
                repository.createTarea(request)
                cargarDatosMascota(usuarioId)
            } catch (e: Exception) {
                _uiState.value = MascotaUiState.Error("Error al crear la tarea: ${e.message}")
            }
        }
    }


    fun completarTarea(tarea: Tarea) {
        viewModelScope.launch {
            try {
                val updateRequest = UpdateTareaRequest(completado = 1)
                repository.updateTarea(tarea.id, updateRequest)
                cargarDatosMascota(tarea.usuarioId)
            } catch (e: Exception) {
                _uiState.value = MascotaUiState.Error("Error al completar la tarea: ${e.message}")
            }
        }
    }

}

class PantallaMascotaViewModelFactory(
    private val application: Application,
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PantallaMascotaViewModel::class.java)) {
            val viewModel = PantallaMascotaViewModel(repository)
            viewModel.initializeStepCounter(application)
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
