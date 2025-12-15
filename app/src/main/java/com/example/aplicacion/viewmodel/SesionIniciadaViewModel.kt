package com.example.aplicacion.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Usuario
import com.example.aplicacion.data.remote.handleApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SesionIniciadaViewModel : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun cargarUsuarioPorId(id: Int, context: Context) {
        if (id <= 0) return

        viewModelScope.launch {
            try {
                val response = RetrofitClient.getInstance(context).instance.getUsuario(id.toString())
                _usuario.value = handleApiResponse(response)
            } catch (e: Exception) {
                _usuario.value = null
                println("Error al cargar usuario: ${e.message}")
            }
        }
    }
}
