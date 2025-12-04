package com.example.aplicacion.data.remote

import com.example.aplicacion.viewmodel.RegistroUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistroRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun registrarUsuario(nuevoUsuario: Usuario): RegistroUiState {
        return withContext(dispatcher) {
            try {
                val response = RetrofitClient.instance.createUsuario(nuevoUsuario)

                if (response.isSuccessful && response.body() != null) {
                    RegistroUiState.Success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al registrar."
                    RegistroUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                RegistroUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
