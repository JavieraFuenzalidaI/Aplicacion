package com.example.aplicacion.data.remote

import android.content.Context
import com.example.aplicacion.viewmodel.RegistroUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistroRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun registrarUsuario(request: RegisterRequest, context: Context): RegistroUiState {
        return withContext(dispatcher) {
            try {
                val response = RetrofitClient.getInstance(context).instance.createUsuario(request)
                if (response.isSuccessful && response.body() != null) {
                    RegistroUiState.Success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido al registrar."
                    RegistroUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                RegistroUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
