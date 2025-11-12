package com.example.aplicacion.data

import android.content.ContentValues
import android.content.Context
import com.example.aplicacion.model.Usuario

class UsuarioRepository(context: Context) {
    private val dbHelper = UsuarioDbHelper(context)

    fun insertarUsuario(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UsuarioDbHelper.COLUMN_NOMBRE, usuario.nombre)
            put(UsuarioDbHelper.COLUMN_CORREO, usuario.correo)
            put(UsuarioDbHelper.COLUMN_CONTRASENA, usuario.contrasena)
            put(UsuarioDbHelper.COLUMN_FECHA, usuario.fechaNacimiento)
        }
        val resultado = db.insert(UsuarioDbHelper.TABLE_NAME, null, values)
        db.close()
        return resultado
    }

    fun validarUsuario(correo: String, contrasena: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?"
        val cursor = db.rawQuery(query, arrayOf(correo, contrasena))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }
}