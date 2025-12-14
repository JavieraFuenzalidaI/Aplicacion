package com.example.aplicacion.data

import com.example.aplicacion.data.remote.AdminUpdateUserData
import com.example.aplicacion.data.remote.ApiService
import com.example.aplicacion.data.remote.ModeratorUpdateUserData
import com.example.aplicacion.data.remote.Usuario
import com.example.aplicacion.data.remote.handleApiResponse

// Este es el NUEVO repositorio. Solo habla con la API (ApiService).
class UsuarioRepository(private val apiService: ApiService) {

    suspend fun obtenerTodosLosUsuarios(): List<Usuario> {
        // Llama al endpoint GET /usuarios y maneja la respuesta
        return handleApiResponse(apiService.getUsuarios())
    }

    suspend fun obtenerUsuarioPorId(id: String): Usuario {
        // Llama al endpoint GET /usuarios/{id} y maneja la respuesta
        return handleApiResponse(apiService.getUsuario(id))
    }

    suspend fun eliminarUsuario(id: Int) {
        // Llama al endpoint DELETE /usuarios/{id} y solo verifica que no haya error
        handleApiResponse(apiService.deleteUsuario(id))
    }

    suspend fun actualizarUsuarioAdmin(id: String, data: AdminUpdateUserData): Usuario {
        // Llama al endpoint PATCH /usuarios/admin/{id} y maneja la respuesta
        return handleApiResponse(apiService.actualizarUsuarioAdmin(id, data))
    }

    suspend fun actualizarUsuarioModerador(id: String, data: ModeratorUpdateUserData): Usuario {
        // Llama al endpoint PATCH /usuarios/moderador/{id} y maneja la respuesta
        return handleApiResponse(apiService.actualizarUsuarioModerador(id, data))
    }
}
