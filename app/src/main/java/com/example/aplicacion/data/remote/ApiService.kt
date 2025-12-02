package com.example.aplicacion.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Endpoint de Login ---

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // --- Endpoints de Usuarios ---

    @GET("usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<Usuario>

    @GET("usuarios/email/{correo}")
    suspend fun getUsuarioPorCorreo(@Path("correo") correo: String): Response<Usuario>

    @POST("usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): Response<Usuario>

    @PUT("usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuario: Usuario): Response<Usuario>

    @DELETE("usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Unit>

    // --- Endpoints de Tareas ---

    @GET("tareas")
    suspend fun getTareas(): Response<List<Tarea>>

    @GET("tareas/usuario/{usuario_id}")
    suspend fun getTareasPorUsuario(@Path("usuario_id") usuarioId: Int): Response<List<Tarea>>

    @GET("tareas/{id}")
    suspend fun getTarea(@Path("id") id: Int): Response<Tarea>

    @POST("tareas")
    suspend fun createTarea(@Body tarea: Tarea): Response<Tarea>

    @PUT("tareas/{id}")
    suspend fun updateTarea(@Path("id") id: Int, @Body tarea: Tarea): Response<Tarea>

    @DELETE("tareas/{id}")
    suspend fun deleteTarea(@Path("id") id: Int): Response<Unit>
}
