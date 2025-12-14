package com.example.aplicacion.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

object PreferenciasDiarias {

    private fun prefs(context: Context, userId: Int): SharedPreferences =
        context.getSharedPreferences("MascotaPrefs_$userId", Context.MODE_PRIVATE)

    fun fechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun obtenerTareasDelDia(
        context: Context,
        userId: Int,
        todas: List<Pair<String, Int>>
    ): List<Pair<String, Int>> {
        val p = prefs(context, userId)
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

    private fun clearTareas(editor: SharedPreferences.Editor) {
        (0 until 3).forEach { i ->
            editor.remove("tarea_${i}_texto")
            editor.remove("tarea_${i}_puntos")
        }
    }

    fun guardarTareaCompletada(context: Context, userId: Int, tarea: String) {
        val p = prefs(context, userId)
        val key = "tareasCompletadas"
        val actuales = p.getStringSet(key, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        actuales.add(tarea)
        p.edit { putStringSet(key, actuales) }
    }

    fun obtenerTareasCompletadas(context: Context, userId: Int): Set<String> {
        val p = prefs(context, userId)
        return p.getStringSet("tareasCompletadas", emptySet()) ?: emptySet()
    }

    fun guardarPasos(context: Context, userId: Int, pasos: Int) {
        val p = prefs(context, userId)
        val hoy = fechaActual()
        p.edit {
            putInt("pasosHoy", pasos)
            putString("fecha_pasos", hoy)
        }
    }

    fun obtenerPasos(context: Context, userId: Int): Int {
        val p = prefs(context, userId)
        val hoy = fechaActual()
        val ultimaFecha = p.getString("fecha_pasos", "")
        return if (ultimaFecha == hoy) p.getInt("pasosHoy", 0) else 0
    }

    fun reiniciarPasosSiEsNuevoDia(context: Context, userId: Int) {
        val p = prefs(context, userId)
        val hoy = fechaActual()
        val ultimaFecha = p.getString("fecha_pasos", "")
        if (ultimaFecha != hoy) {
            p.edit {
                putInt("pasosHoy", 0)
                putString("fecha_pasos", hoy)
            }
        }
    }

    fun reiniciarNivelSiEsNuevoDia(
        context: Context,
        repo: UsuarioRepository,
        userId: Int
    ): Int {
        val p = prefs(context, userId)
        val hoy = fechaActual()
        val ultimaFecha = p.getString("fecha_nivel", null)

        return if (ultimaFecha != hoy) {
            val nuevoNivel = (0..20).random()
            // repo.actualizarNivelUsuario(userId, nuevoNivel)
            p.edit { putString("fecha_nivel", hoy) }
            nuevoNivel
        } else {
            var nivelActual = 0
            runBlocking {
                // val usuario = repo.obtenerUsuarioPorId(userId.toString())
                // nivelActual = usuario.nivel
            }
            nivelActual
        }
    }

    fun resetDiario(context: Context, userId: Int) {
        val p = prefs(context, userId)
        p.edit {
            remove("fecha_tareas")
            remove("fecha_nivel")
            clearTareas(this)
        }
    }

    fun resetTotal(context: Context, repo: UsuarioRepository, userId: Int) {
        val p = prefs(context, userId)
        val hoy = fechaActual()

        p.edit {
            clear()
            putString("fecha_pasos", hoy)
            putInt("pasosHoy", 0)
        }
        // repo.borrarTareasUsuario(userId)
        // repo.actualizarNivelUsuario(userId, 0)
    }
}
