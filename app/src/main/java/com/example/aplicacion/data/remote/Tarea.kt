package com.example.aplicacion.data.remote

import com.google.gson.annotations.SerializedName

data class Tarea(
    @SerializedName("id") val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("puntos") val puntos: Int,
    @SerializedName("completado") val completado: Int
)
