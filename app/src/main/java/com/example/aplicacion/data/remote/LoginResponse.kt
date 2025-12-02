package com.example.aplicacion.data.remote

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("usuario") val usuario: Usuario
)
