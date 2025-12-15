package com.example.aplicacion.data

import com.example.aplicacion.data.remote.AdminUpdateUserData
import com.example.aplicacion.data.remote.ApiService
import com.example.aplicacion.data.remote.CreateTareaRequest
import com.example.aplicacion.data.remote.ModeratorUpdateUserData
import com.example.aplicacion.data.remote.Tarea
import com.example.aplicacion.data.remote.UpdateTareaRequest
import com.example.aplicacion.data.remote.Usuario
import com.example.aplicacion.data.remote.handleApiResponse

class UsuarioRepository(private val apiService: ApiService) {

    suspend fun obtenerTodosLosUsuarios(): List<Usuario> {
        return handleApiResponse(apiService.getUsuarios())
    }

    suspend fun obtenerUsuarioPorId(id: String): Usuario {
        return handleApiResponse(apiService.getUsuario(id))
    }

    suspend fun eliminarUsuario(id: Int) {
        handleApiResponse(apiService.deleteUsuario(id))
    }

    suspend fun actualizarUsuarioAdmin(id: String, data: AdminUpdateUserData): Usuario {
        return handleApiResponse(apiService.actualizarUsuarioAdmin(id, data))
    }

    suspend fun actualizarUsuarioModerador(id: String, data: ModeratorUpdateUserData): Usuario {
        return handleApiResponse(apiService.actualizarUsuarioModerador(id, data))
    }

    suspend fun getTareasUsuario(id: Int): List<Tarea> {
        return handleApiResponse(apiService.getTareasUsuario(id))
    }

    suspend fun createTarea(tarea: CreateTareaRequest): Tarea {
        return handleApiResponse(apiService.createTarea(tarea))
    }

    suspend fun updateTarea(id: Int, request: UpdateTareaRequest): Tarea {
        return handleApiResponse(apiService.updateTarea(id, request))
    }
}
