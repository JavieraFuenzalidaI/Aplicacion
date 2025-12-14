package com.example.aplicacion.data.remote

import retrofit2.Response
import retrofit2.http.*

// --- Data classes para las peticiones ---


data class RegisterRequest(val nombre: String, val correo: String, val contrasena: String, val fecha: String)

// Data classes para las actualizaciones de rol
data class AdminUpdateUserData(val nombre: String, val correo: String, val fecha: String, val nivel: Int, val rol: String, val contrasena: String?)
data class ModeratorUpdateUserData(val nombre: String, val fecha: String)


// --- Definición de la Interfaz ---

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
}
