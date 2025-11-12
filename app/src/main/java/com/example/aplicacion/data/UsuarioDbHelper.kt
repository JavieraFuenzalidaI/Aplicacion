package com.example.aplicacion.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsuarioDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "usuarios.db"
        const val DATABASE_VERSION = 2

        // Tabla de usuarios
        const val TABLE_USUARIOS = "usuarios"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_CONTRASENA = "contrasena"
        const val COLUMN_FECHA = "fechaNacimiento"
        const val COLUMN_NIVEL = "nivelMascota"

        // Tabla de tareas
        const val TABLE_TAREAS = "tareas_usuario"
        const val COLUMN_TAREA_ID = "id"
        const val COLUMN_TAREA_USUARIO_ID = "idUsuario"
        const val COLUMN_TAREA_DESC = "descripcion"
        const val COLUMN_TAREA_PUNTOS = "puntos"
        const val COLUMN_TAREA_COMPLETADO = "completado"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT UNIQUE NOT NULL,
                $COLUMN_CONTRASENA TEXT NOT NULL,
                $COLUMN_FECHA TEXT NOT NULL,
                $COLUMN_NIVEL INTEGER DEFAULT 0
            )
        """.trimIndent()

        val createTareas = """
            CREATE TABLE $TABLE_TAREAS (
                $COLUMN_TAREA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TAREA_USUARIO_ID INTEGER NOT NULL,
                $COLUMN_TAREA_DESC TEXT NOT NULL,
                $COLUMN_TAREA_PUNTOS INTEGER NOT NULL,
                $COLUMN_TAREA_COMPLETADO INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_TAREA_USUARIO_ID) REFERENCES $TABLE_USUARIOS($COLUMN_ID)
            )
        """.trimIndent()

        db.execSQL(createUsuarios)
        db.execSQL(createTareas)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAREAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }
}