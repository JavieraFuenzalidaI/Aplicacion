package com.example.aplicacion.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Tarea
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
        if (usuarioId == 0) { // El ID 0 ahora es para el admin
            val admin = Usuario(0, "Administrador", "admin@taskipet.com", "", "admin", 100, "admin")
            _uiState.value = MascotaUiState.Success(MascotaScreenData(admin, emptyList()))
            return
        }

        viewModelScope.launch {
            _uiState.value = MascotaUiState.Loading
            try {
                // Se usan las funciones del repositorio que ahora manejan la API
                val usuario = repository.obtenerUsuarioPorId(usuarioId.toString())
                // val tareas = repository.obtenerTareasPorUsuario(usuarioId) // Necesitarías esta función en el repo

                // Por ahora, asumimos una lista de tareas vacía para que compile
                val tareas = emptyList<Tarea>()

                val screenData = MascotaScreenData(usuario, tareas)
                _uiState.value = MascotaUiState.Success(screenData)

            } catch (e: Exception) {
                _uiState.value = MascotaUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun agregarTarea(usuarioId: Int, descripcion: String) {
        // Esta función necesitaría su propia implementación en ApiService y Repository
    }

    fun completarTarea(tarea: Tarea, nuevoNivel: Int) {
        // Esta función necesitaría su propia implementación en ApiService y Repository
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
