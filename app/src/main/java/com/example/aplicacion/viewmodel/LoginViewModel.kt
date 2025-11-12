package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.model.Usuario

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        return repository.validarUsuario(correo, contrasena)
    }
}

class LoginViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}