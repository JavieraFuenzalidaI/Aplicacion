package com.example.aplicacion.data

import android.content.ContentValues
import android.content.Context
import com.example.aplicacion.model.Usuario

class UsuarioRepository(context: Context) {
    private val dbHelper = UsuarioDbHelper(context)

    // --- Usuarios ---

    fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${UsuarioDbHelper.TABLE_USUARIOS} WHERE ${UsuarioDbHelper.COLUMN_CORREO} = ?"
        val cursor = db.rawQuery(query, arrayOf(correo))
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NOMBRE)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CORREO)),
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CONTRASENA)),
                nivelMascota = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NIVEL)),
                rol = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ROL))
            )
        }
        cursor.close()
        db.close()
        return usuario
    }
    fun insertarUsuario(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UsuarioDbHelper.COLUMN_NOMBRE, usuario.nombre)
            put(UsuarioDbHelper.COLUMN_CORREO, usuario.correo)
            put(UsuarioDbHelper.COLUMN_CONTRASENA, usuario.contrasena)
            put(UsuarioDbHelper.COLUMN_NIVEL, 0)
            put(UsuarioDbHelper.COLUMN_ROL, "usuario")
        }
        val resultado = db.insert(UsuarioDbHelper.TABLE_USUARIOS, null, values)
        db.close()
        return resultado
    }

    fun validarUsuario(correo: String, contrasena: String): Usuario? {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${UsuarioDbHelper.TABLE_USUARIOS} WHERE correo = ? AND contrasena = ?"
        val cursor = db.rawQuery(query, arrayOf(correo, contrasena))
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NOMBRE)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CORREO)),
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CONTRASENA)),
                nivelMascota = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NIVEL)),
                rol = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ROL))
            )
        }
        cursor.close()
        db.close()
        return usuario
    }

    fun actualizarNivelUsuario(idUsuario: Int, nuevoNivel: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UsuarioDbHelper.COLUMN_NIVEL, nuevoNivel)
        }
        db.update(UsuarioDbHelper.TABLE_USUARIOS, values, "id = ?", arrayOf(idUsuario.toString()))
        db.close()
    }

    // --- Tareas del usuario ---

    fun insertarTarea(idUsuario: Int, descripcion: String, puntos: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UsuarioDbHelper.COLUMN_TAREA_USUARIO_ID, idUsuario)
            put(UsuarioDbHelper.COLUMN_TAREA_DESC, descripcion)
            put(UsuarioDbHelper.COLUMN_TAREA_PUNTOS, puntos)
            put(UsuarioDbHelper.COLUMN_TAREA_COMPLETADO, 0)
        }
        db.insert(UsuarioDbHelper.TABLE_TAREAS, null, values)
        db.close()
    }

    fun obtenerTareasUsuario(idUsuario: Int): List<Pair<String, Int>> {
        val db = dbHelper.readableDatabase
        val query = "SELECT ${UsuarioDbHelper.COLUMN_TAREA_DESC}, ${UsuarioDbHelper.COLUMN_TAREA_PUNTOS} " +
                "FROM ${UsuarioDbHelper.TABLE_TAREAS} WHERE ${UsuarioDbHelper.COLUMN_TAREA_USUARIO_ID} = ?"
        val cursor = db.rawQuery(query, arrayOf(idUsuario.toString()))
        val lista = mutableListOf<Pair<String, Int>>()
        while (cursor.moveToNext()) {
            val desc = cursor.getString(0)
            val puntos = cursor.getInt(1)
            lista += desc to puntos
        }
        cursor.close()
        db.close()
        return lista
    }

    fun borrarTareasUsuario(idUsuario: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            UsuarioDbHelper.TABLE_TAREAS,
            "${UsuarioDbHelper.COLUMN_TAREA_USUARIO_ID} = ?",
            arrayOf(idUsuario.toString())
        )
        db.close()
    }

    fun obtenerUsuarioPorId(idUsuario: Int): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${UsuarioDbHelper.TABLE_USUARIOS} WHERE ${UsuarioDbHelper.COLUMN_ID} = ?",
            arrayOf(idUsuario.toString())
        )
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NOMBRE)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CORREO)),
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CONTRASENA)),
                nivelMascota = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NIVEL)),
                rol = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ROL))
            )
        }
        cursor.close()
        db.close()
        return usuario
    }
    // --- Gestión general de usuarios ---

    fun obtenerTodosLosUsuarios(): List<Usuario> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${UsuarioDbHelper.TABLE_USUARIOS}", null)
        val lista = mutableListOf<Usuario>()
        while (cursor.moveToNext()) {
            val usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NOMBRE)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CORREO)),
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_CONTRASENA)),
                nivelMascota = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_NIVEL)),
                rol = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioDbHelper.COLUMN_ROL))
            )
            lista.add(usuario)
        }
        cursor.close()
        db.close()
        return lista
    }

    fun eliminarUsuario(idUsuario: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            UsuarioDbHelper.TABLE_TAREAS,
            "${UsuarioDbHelper.COLUMN_TAREA_USUARIO_ID} = ?",
            arrayOf(idUsuario.toString())
        )
        db.delete(
            UsuarioDbHelper.TABLE_USUARIOS,
            "${UsuarioDbHelper.COLUMN_ID} = ?",
            arrayOf(idUsuario.toString())
        )
        db.close()
    }
}//fin clase

