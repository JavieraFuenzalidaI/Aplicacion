
package com.example.aplicacion

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.model.Usuario
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UsuarioRepositoryTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = UsuarioRepository(context)
    }

    @After
    fun tearDown() {
        val usuarios = repository.obtenerTodosLosUsuarios()
        for (usuario in usuarios) {
            repository.eliminarUsuario(usuario.id)
        }
    }

    @Test
    fun `insertar y obtener un usuario`() {
        val nuevoUsuario = Usuario(0, "Test User", "test@example.com", "password123", 0, "usuario")
        val id = repository.insertarUsuario(nuevoUsuario)
        assertTrue(id > 0)

        val usuarioObtenido = repository.obtenerUsuarioPorCorreo("test@example.com")
        assertNotNull(usuarioObtenido)
        assertEquals("Test User", usuarioObtenido?.nombre)
    }

    @Test
    fun `validar usuario con credenciales correctas`() {
        val nuevoUsuario = Usuario(0, "Test User", "test@example.com", "password123", 0, "usuario")
        repository.insertarUsuario(nuevoUsuario)

        val usuarioValidado = repository.validarUsuario("test@example.com", "password123")
        assertNotNull(usuarioValidado)
    }

    @Test
    fun `validar usuario con credenciales incorrectas`() {
        val nuevoUsuario = Usuario(0, "Test User", "test@example.com", "password123", 0, "usuario")
        repository.insertarUsuario(nuevoUsuario)

        val usuarioValidado = repository.validarUsuario("test@example.com", "wrongpassword")
        assertNull(usuarioValidado)
    }

    @Test
    fun `actualizar nivel de usuario`() {
        val nuevoUsuario = Usuario(0, "Test User", "test@example.com", "password123", 0, "usuario")
        repository.insertarUsuario(nuevoUsuario)
        val usuarioInsertado = repository.obtenerUsuarioPorCorreo("test@example.com")!!

        repository.actualizarNivelUsuario(usuarioInsertado.id, 5)

        val usuarioActualizado = repository.obtenerUsuarioPorId(usuarioInsertado.id)
        assertNotNull(usuarioActualizado)
        assertEquals(5, usuarioActualizado?.nivelMascota)
    }

    @Test
    fun `insertar y obtener tareas de usuario`() {
        val nuevoUsuario = Usuario(0, "Test User", "test@example.com", "password123", 0, "usuario")
        repository.insertarUsuario(nuevoUsuario)
        val usuarioInsertado = repository.obtenerUsuarioPorCorreo("test@example.com")!!

        repository.insertarTarea(usuarioInsertado.id, "Tarea 1", 10)
        repository.insertarTarea(usuarioInsertado.id, "Tarea 2", 20)

        val tareas = repository.obtenerTareasUsuario(usuarioInsertado.id)
        assertEquals(2, tareas.size)
        assertTrue(tareas.contains("Tarea 1" to 10))
        assertTrue(tareas.contains("Tarea 2" to 20))
    }

    @Test
    fun `borrar tareas de usuario`() {
        val nuevoUsuario = Usuario(0, "Test User", "test@example.com", "password123", 0, "usuario")
        repository.insertarUsuario(nuevoUsuario)
        val usuarioInsertado = repository.obtenerUsuarioPorCorreo("test@example.com")!!

        repository.insertarTarea(usuarioInsertado.id, "Tarea 1", 10)
        repository.borrarTareasUsuario(usuarioInsertado.id)

        val tareas = repository.obtenerTareasUsuario(usuarioInsertado.id)
        assertTrue(tareas.isEmpty())
    }

    @Test
    fun `eliminar usuario`() {
        val nuevoUsuario = Usuario(0, "Test User", "test@example.com", "password123", 0, "usuario")
        repository.insertarUsuario(nuevoUsuario)
        val usuarioInsertado = repository.obtenerUsuarioPorCorreo("test@example.com")!!

        repository.eliminarUsuario(usuarioInsertado.id)

        val usuarioEliminado = repository.obtenerUsuarioPorId(usuarioInsertado.id)
        assertNull(usuarioEliminado)
    }
}
