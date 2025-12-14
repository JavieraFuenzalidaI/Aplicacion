
package com.example.aplicacion.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacion.data.remote.GeminiClient
import com.example.aplicacion.data.remote.Tarea
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GenerarTareasViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockGenerativeModel: GenerativeModel

    @MockK
    private lateinit var mockResponse: GenerateContentResponse

    private lateinit var viewModel: GenerarTareasViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // MockK: Mock a static object
        mockkObject(GeminiClient)
        every { GeminiClient.generativeModel } returns mockGenerativeModel

        viewModel = GenerarTareasViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // MockK: Limpiar el mock
        unmockkObject(GeminiClient)
    }

    @Test
    fun `initial state is Idle`() = runTest {
        assertEquals(GenerarTareasUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `Cuando se genera tareas, el estado cambia a Success`() = runTest {
        val idUsuario = 1
        val prompt = "Organizar mi día"
        val jsonResponse = """[
            {"descripcion": "Revisar correos", "puntos": 10, "usuario_id": 1},
            {"descripcion": "Planificar reuniones", "puntos": 20, "usuario_id": 1}
        ]"""
        val expectedTareas = listOf(
            Tarea(id = 0, usuarioId = idUsuario, descripcion = "Revisar correos", puntos = 10, completado = 0),
            Tarea(id = 0, usuarioId = idUsuario, descripcion = "Planificar reuniones", puntos = 20, completado = 0)
        )

        every { mockResponse.text } returns jsonResponse
        coEvery { mockGenerativeModel.generateContent(any<String>()) } returns mockResponse

        viewModel.generarTareas(prompt, idUsuario)

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is GenerarTareasUiState.Success)
        val successState = state as GenerarTareasUiState.Success
        assertEquals(expectedTareas.size, successState.tareas.size)
        assertEquals(expectedTareas[0].descripcion, successState.tareas[0].descripcion)
    }

    @Test
    fun `Cuando la IA no devuelve ninguna respuesta, el estado cambia a Error `() = runTest {
        every { mockResponse.text } returns null
        coEvery { mockGenerativeModel.generateContent(any<String>()) } returns mockResponse

        viewModel.generarTareas("un prompt", 1)

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is GenerarTareasUiState.Error)
        assertEquals("La IA no devolvió una respuesta.", (state as GenerarTareasUiState.Error).message)
    }

    @Test
    fun `Cuando hay problemas al contactar la IA, el estado cambia a Error`() = runTest {
        val errorMessage = "Network error"
        coEvery { mockGenerativeModel.generateContent(any<String>()) } throws RuntimeException(errorMessage)

        viewModel.generarTareas("un prompt", 1)

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is GenerarTareasUiState.Error)
        assertEquals("Error al contactar con la IA: $errorMessage", (state as GenerarTareasUiState.Error).message)
    }
    
    @Test
    fun `Cuando la respuesta Json esta mal formulada, estado es exitoso con lista vacia`() = runTest {
        val idUsuario = 1
        val prompt = "Organizar mi día"
        val jsonResponse = "Esto no es un JSON"

        every { mockResponse.text } returns jsonResponse
        coEvery { mockGenerativeModel.generateContent(any<String>()) } returns mockResponse

        viewModel.generarTareas(prompt, idUsuario)

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is GenerarTareasUiState.Success)
        assertTrue("Task list should be empty", (state as GenerarTareasUiState.Success).tareas.isEmpty())
    }

    @Test
    fun `Cuando se llama al resetState, estado retorna al Idle`() = runTest {
        coEvery { mockGenerativeModel.generateContent(any<String>()) } throws RuntimeException("error")
        viewModel.generarTareas("prompt", 1)
        assertTrue(viewModel.uiState.value is GenerarTareasUiState.Error)

        viewModel.resetState()

        assertEquals(GenerarTareasUiState.Idle, viewModel.uiState.value)
    }
}
