package com.example.aplicacion.data.remote

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("rol") val rol: String,
    @SerializedName("racha") val racha: Int? = 0 // Campo añadido para la racha
)
