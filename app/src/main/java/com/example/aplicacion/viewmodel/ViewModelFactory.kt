package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.data.remote.RetrofitClient

class ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(VerUsuariosViewModel::class.java)) {
            val userRepository = UsuarioRepository(RetrofitClient.instance)
            return VerUsuariosViewModel(userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
