package com.example.aplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.model.Usuario

class RegistroViewModel(private val repository: UsuarioRepository) : ViewModel() {
    fun registrarUsuario(nombre: String, correo: String, contrasena: String, fecha: String): Boolean {
        val usuario = Usuario(nombre = nombre, correo = correo, contrasena = contrasena, fechaNacimiento = fecha)
        val resultado = repository.insertarUsuario(usuario)
        return resultado != -1L
    }
}

class RegistroViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}