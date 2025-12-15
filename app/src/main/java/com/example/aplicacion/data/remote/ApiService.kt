package com.example.aplicacion.data.remote

import retrofit2.Response
import retrofit2.http.*

data class RegisterRequest(val nombre: String, val correo: String, val contrasena: String, val fecha: String)

data class AdminUpdateUserData(val nombre: String, val correo: String, val fecha: String, val nivel: Int, val rol: String, val contrasena: String?)
data class ModeratorUpdateUserData(val nombre: String, val fecha: String)

data class CreateTareaRequest(val usuario_id: Int, val descripcion: String, val puntos: Int)
data class UpdateTareaRequest(val completado: Int)
// Se elimina UpdateNivelRequest porque ya no se usa

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("usuarios")
    suspend fun createUsuario(@Body request: RegisterRequest): Response<Usuario>

    @GET("usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: String): Response<Usuario>

    @DELETE("usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Unit>

    @PATCH("usuarios/admin/{id}")
    suspend fun actualizarUsuarioAdmin(
        @Path("id") id: String,
        @Body datosUsuario: AdminUpdateUserData
    ): Response<Usuario>

    @PATCH("usuarios/moderador/{id}")
    suspend fun actualizarUsuarioModerador(
        @Path("id") id: String,
        @Body datosUsuario: ModeratorUpdateUserData
    ): Response<Usuario>

    @GET("tareas/usuario/{id}")
    suspend fun getTareasUsuario(@Path("id") id: Int): Response<List<Tarea>>

    @POST("tareas")
    suspend fun createTarea(@Body tarea: CreateTareaRequest): Response<Tarea>

    @PATCH("tareas/{id}")
    suspend fun updateTarea(@Path("id") id: Int, @Body request: UpdateTareaRequest): Response<Tarea>


}
