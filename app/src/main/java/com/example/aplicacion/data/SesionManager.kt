package com.example.aplicacion.data

import android.content.Context
import android.content.SharedPreferences

class SesionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)

    fun guardarSesion(correo: String) {
        prefs.edit().putString("usuario_correo", correo).apply()
    }

    fun obtenerSesion(): String? {
        return prefs.getString("usuario_correo", null)
    }

    fun cerrarSesion() {
        prefs.edit().remove("usuario_correo").apply()
    }
}