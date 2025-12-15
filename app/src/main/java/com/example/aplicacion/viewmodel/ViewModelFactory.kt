package com.example.aplicacion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.data.remote.RetrofitClient

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Cuando se pida un VerUsuariosViewModel, se crea con el repositorio.
        if (modelClass.isAssignableFrom(VerUsuariosViewModel::class.java)) {
            val apiService = RetrofitClient.getInstance(context).instance
            val userRepository = UsuarioRepository(apiService)
            return VerUsuariosViewModel(userRepository) as T
        }

        // Cuando se pida un PantallaMascotaViewModel, se crea con el repositorio y la app.
        if (modelClass.isAssignableFrom(PantallaMascotaViewModel::class.java)) {
            if (context.applicationContext is Application) {
                val apiService = RetrofitClient.getInstance(context).instance
                val userRepository = UsuarioRepository(apiService)
                val viewModel = PantallaMascotaViewModel(userRepository)
                viewModel.initializeStepCounter(context.applicationContext as Application)
                return viewModel as T
            }
        }

        // Si no sabe cómo crearlo, lanza un error.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
