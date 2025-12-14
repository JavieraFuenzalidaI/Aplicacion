
package com.example.aplicacion.viewmodel

import com.example.aplicacion.data.remote.RegistroRepository
import com.example.aplicacion.data.remote.Usuario
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RegistroViewModelTest {

    private lateinit var mockRepository: RegistroRepository
    private lateinit var viewModel: RegistroViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        mockRepository = mockk()
        viewModel = RegistroViewModel(repository = mockRepository, dispatcher = testDispatcher)
    }

    @Test
    fun `cuando se crea el viewModel, el estado inicial es Idle`() {
        assertTrue("El estado inicial debe ser Idle", viewModel.uiState.value is RegistroUiState.Idle)
    }

    @Test
    fun `cuando el registro es exitoso, el estado es Success`() {
        // Given
        val usuario = Usuario(1, "Test", "test@test.com", "pass", "2024-01-01", 1, "usuario")
        coEvery { mockRepository.registrarUsuario(any()) } returns RegistroUiState.Success(usuario)

        // When
        viewModel.registrarUsuario("Test", "test@test.com", "pass", "2024-01-01")

        // Then
        assertTrue("El estado debería ser Success", viewModel.uiState.value is RegistroUiState.Success)
    }

    @Test
    fun `cuando el registro falla, el estado es Error`() {
        // Given
        val errorMessage = "Error de prueba"
        coEvery { mockRepository.registrarUsuario(any()) } returns RegistroUiState.Error(errorMessage)

        // When
        viewModel.registrarUsuario("Test", "test@test.com", "pass", "2024-01-01")

        // Then
        val state = viewModel.uiState.value
        assertTrue("El estado debería ser Error", state is RegistroUiState.Error)
        assertTrue("El mensaje de error no coincide", (state as RegistroUiState.Error).message == errorMessage)
    }

    @Test
    fun `cuando se llama a resetState, el estado vuelve a Idle`() {
        // When
        viewModel.resetState()

        // Then
        assertTrue("El estado después de resetState debe ser Idle", viewModel.uiState.value is RegistroUiState.Idle)
    }
}
