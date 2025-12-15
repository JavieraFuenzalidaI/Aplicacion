package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.GeminiClient
import com.example.aplicacion.data.remote.Tarea
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface GenerarTareasUiState {
    object Idle : GenerarTareasUiState
    object Loading : GenerarTareasUiState
    data class Success(val tareas: List<Tarea>) : GenerarTareasUiState
    data class Error(val message: String) : GenerarTareasUiState
}

class GenerarTareasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<GenerarTareasUiState>(GenerarTareasUiState.Idle)
    val uiState: StateFlow<GenerarTareasUiState> = _uiState

    fun generarTareas(promptUsuario: String, idUsuario: Int) {
        viewModelScope.launch {
            _uiState.value = GenerarTareasUiState.Loading
            val promptCompleto = """
                Eres un asistente de productividad para la app TaskiPet. 
                El usuario quiere que le ayudes con la siguiente tarea: '$promptUsuario'.
                Genera una lista de 3 a 5 subtareas en formato JSON.
                Cada tarea en el JSON debe tener los campos 'usuario_id' (int), 'descripcion' (string), 'puntos' (int) con valor 10 o 20 y 'completado' (int) en 0.
                La respuesta debe ser únicamente el JSON, sin ningún texto adicional.
            """

            try {
                val response = GeminiClient.generativeModel.generateContent(promptCompleto)
                val responseText = response.text
                if (responseText != null) {
                    val tareasGeneradas = parseTareasFromJson(responseText, idUsuario)
                    _uiState.value = GenerarTareasUiState.Success(tareasGeneradas)
                } else {
                    _uiState.value = GenerarTareasUiState.Error("La IA no devolvió una respuesta.")
                }

            } catch (e: Exception) {
                _uiState.value = GenerarTareasUiState.Error("Error al contactar con la IA: ${e.message}")
            }
        }
    }

    private fun parseTareasFromJson(jsonString: String, idUsuario: Int): List<Tarea> {
        return try {
            val gson = Gson()
            val tipoLista = object : TypeToken<List<TareaJson>>() {}.type
            val tareasJson: List<TareaJson> = gson.fromJson(jsonString, tipoLista)

            tareasJson.map {
                Tarea(
                    id = 0,
                    descripcion = it.descripcion,
                    puntos = it.puntos,
                    completado = 0,
                    usuarioId = idUsuario
                )
            }
        } catch (e: Exception) {
            println("Error al parsear el JSON: ${e.message}")
            emptyList()
        }
    }

    private data class TareaJson(
        val descripcion: String,
        val puntos: Int,
        val usuario_id: Int
    )

    fun resetState() {
        _uiState.value = GenerarTareasUiState.Idle
    }
}
