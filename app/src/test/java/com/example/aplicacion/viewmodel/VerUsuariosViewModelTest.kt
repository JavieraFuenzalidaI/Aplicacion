
package com.example.aplicacion.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacion.data.remote.ApiService
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Usuario
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class VerUsuariosViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockApiService: ApiService

    private lateinit var viewModel: VerUsuariosViewModel

    private val adminUser = Usuario(0, "Admin", "admin", "", "", 100, "admin")
    private val regularUser = Usuario(1, "Test User", "test@test.com", "", "", 5, "user")
    private val userList = listOf(adminUser, regularUser)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.instance } returns mockApiService

        viewModel = VerUsuariosViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `cargarUsuarios - success and filters admin`() = runTest {
        coEvery { mockApiService.getUsuarios() } returns Response.success(userList)

        viewModel.cargarUsuarios()

        val state = viewModel.uiState.value
        assertTrue(state is VerUsuariosUiState.Success)
        val successState = state as VerUsuariosUiState.Success
        assertEquals(1, successState.usuarios.size)
        assertEquals(regularUser, successState.usuarios.first())
    }

    @Test
    fun `cargarUsuarios - api error`() = runTest {
        val errorResponse = Response.error<List<Usuario>>(500, "Server Error".toResponseBody(null))
        coEvery { mockApiService.getUsuarios() } returns errorResponse

        viewModel.cargarUsuarios()

        assertTrue(viewModel.uiState.value is VerUsuariosUiState.Error)
    }

    @Test
    fun `eliminarUsuario - success and reloads list`() = runTest {
        // Mock successful deletion
        coEvery { mockApiService.deleteUsuario(regularUser.id) } returns Response.success(Unit)
        // Mock the user list reload, but this time without the deleted user
        coEvery { mockApiService.getUsuarios() } returns Response.success(listOf(adminUser))

        viewModel.eliminarUsuario(regularUser.id)

        val state = viewModel.uiState.value
        assertTrue(state is VerUsuariosUiState.Success)
        // After reload and filtering, the list should be empty
        assertTrue((state as VerUsuariosUiState.Success).usuarios.isEmpty())
    }

    @Test
    fun `eliminarUsuario - api error`() = runTest {
        val errorResponse = Response.error<Unit>(500, "Server Error".toResponseBody(null))
        coEvery { mockApiService.deleteUsuario(regularUser.id) } returns errorResponse

        viewModel.eliminarUsuario(regularUser.id)

        assertTrue(viewModel.uiState.value is VerUsuariosUiState.Error)
    }

    @Test
    fun `eliminarUsuario - network exception`() = runTest {
        coEvery { mockApiService.deleteUsuario(regularUser.id) } throws RuntimeException("Network error")

        viewModel.eliminarUsuario(regularUser.id)

        val state = viewModel.uiState.value
        assertTrue(state is VerUsuariosUiState.Error)
        assertEquals("Error de conexión: Network error", (state as VerUsuariosUiState.Error).message)
    }
}
