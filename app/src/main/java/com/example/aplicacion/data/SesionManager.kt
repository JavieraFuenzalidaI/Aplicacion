package com.example.aplicacion.data

import android.content.Context
import android.content.SharedPreferences

class SesionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("TaskiPetSesionPrefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_SESION = "sesion_usuario"
        const val KEY_TOKEN = "jwt_token"
    }

    fun guardarSesion(sesion: String?, token: String?) {
        val editor = prefs.edit()
        editor.putString(KEY_SESION, sesion)
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun obtenerSesion(): String? {
        return prefs.getString(KEY_SESION, null)
    }

    fun obtenerToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun cerrarSesion() {
        val editor = prefs.edit()
        editor.remove(KEY_SESION)
        editor.remove(KEY_TOKEN)
        editor.apply()
    }
}
