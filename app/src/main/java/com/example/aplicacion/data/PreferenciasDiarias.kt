package com.example.aplicacion.data

import android.content.Context
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.*

object PreferenciasDiarias {

    private const val PREFS_NAME = "MascotaPrefs"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun fechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /** Obtiene las 3 tareas sugeridas actuales, o genera nuevas si es un nuevo día */
    fun obtenerTareasDelDia(
        context: Context,
        todas: List<Pair<String, Int>>
    ): List<Pair<String, Int>> {
        val p = prefs(context)
        val hoy = fechaActual()
        val ultimaFecha = p.getString("fecha_tareas", null)

        return if (ultimaFecha != hoy) {
            val nuevas = todas.shuffled().take(3)
            p.edit {
                clearTareas(this)
                nuevas.forEachIndexed { index, tarea ->
                    putString("tarea_${index}_texto", tarea.first)
                    putInt("tarea_${index}_puntos", tarea.second)
                }
                putString("fecha_tareas", hoy)
            }
            nuevas
        } else {
            (0 until 3).mapNotNull { i ->
                val texto = p.getString("tarea_${i}_texto", null)
                val puntos = p.getInt("tarea_${i}_puntos", 0)
                if (texto != null) texto to puntos else null
            }
        }
    }

    private fun clearTareas(editor: android.content.SharedPreferences.Editor) {
        (0 until 3).forEach { i ->
            editor.remove("tarea_${i}_texto")
            editor.remove("tarea_${i}_puntos")
        }
    }

    /** Reinicia el nivel de la mascota si es un día nuevo */
    fun reiniciarNivelSiEsNuevoDia(
        context: Context,
        repo: UsuarioRepository,
        usuarioId: Int
    ): Int {
        val p = prefs(context)
        val hoy = fechaActual()
        val ultimaFecha = p.getString("fecha_nivel", null)

        return if (ultimaFecha != hoy) {
            val nuevoNivel = (0..20).random()
            repo.actualizarNivelUsuario(usuarioId, nuevoNivel)
            p.edit { putString("fecha_nivel", hoy) }
            nuevoNivel
        } else {
            repo.obtenerUsuarioPorId(usuarioId)?.nivelMascota ?: 0
        }
    }

    /** 🔄 Reset manual del día */
    fun resetDiario(context: Context) {
        val p = prefs(context)
        p.edit {
            remove("fecha_tareas")
            remove("fecha_nivel")
            clearTareas(this)
        }
    }

    /** 💣 Reset completo (también borra tareas de usuario si se desea) */
    fun resetTotal(context: Context, repo: UsuarioRepository, usuarioId: Int) {
        resetDiario(context)
        repo.borrarTareasUsuario(usuarioId)
        repo.actualizarNivelUsuario(usuarioId, 0)
    }
}