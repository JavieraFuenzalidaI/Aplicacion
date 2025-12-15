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
                Basado en la siguiente petición del usuario: '$promptUsuario'.
                Genera una lista de 3 a 5 subtareas.
                La respuesta DEBE SER EXCLUSIVAMENTE un array JSON.
                Cada objeto en el array debe tener los siguientes campos: 'descripcion' (string) y 'puntos' (un entero, que sea 10 o 20).
                No incluyas nada más en tu respuesta, solo el array JSON. No uses markdown (```json ... ```).
            """

            try {
                val response = GeminiClient.generativeModel.generateContent(promptCompleto)
                val responseText = response.text

                if (responseText != null) {
                    val cleanedJson = responseText
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()

                    val tareasGeneradas = parseTareasFromJson(cleanedJson, idUsuario)
                    if (tareasGeneradas.isNotEmpty()) {
                        _uiState.value = GenerarTareasUiState.Success(tareasGeneradas)
                    } else {
                        _uiState.value = GenerarTareasUiState.Error("La IA no devolvió tareas válidas. Prueba a ser más específico.")
                    }
                } else {
                    _uiState.value = GenerarTareasUiState.Error("La IA no devolvió una respuesta.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = GenerarTareasUiState.Error("Error al conectar con la IA: ${e.message}")
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
            println("Error al parsear el JSON de la IA: ${e.message}")
            emptyList()
        }
    }

    private data class TareaJson(
        val descripcion: String,
        val puntos: Int
    )

    fun resetState() {
        _uiState.value = GenerarTareasUiState.Idle
    }
}
