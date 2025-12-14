
package com.example.aplicacion.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacion.data.remote.ApiService
import com.example.aplicacion.data.remote.LoginRequest
import com.example.aplicacion.data.remote.LoginResponse
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockApiService: ApiService

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // MockK: Mock the static RetrofitClient object
        mockkObject(RetrofitClient)
        every { RetrofitClient.instance } returns mockApiService

        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll() // Clean up all MockK mocks
    }

    @Test
    fun `initial state is Idle`() = runTest {
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `Cuando el login es exitoso, estado cambia a Success`() = runTest {
        val loginRequest = LoginRequest("test@example.com", "password")
        val usuario = Usuario(1, "Test", "test@test.com", "pass", "2024-01-01", 1, "usuario")
        val loginResponse = LoginResponse("Success",usuario, "token123")
        val successResponse: Response<LoginResponse> = Response.success(loginResponse)

        coEvery { mockApiService.login(loginRequest) } returns successResponse

        viewModel.iniciarSesion("test@example.com", "password")

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is LoginUiState.Success)
        assertEquals(loginResponse, (state as LoginUiState.Success).loginResponse)
    }

    @Test
    fun `Cuando el login falla por el servidor, estado cambia a Error`() = runTest {
        val loginRequest = LoginRequest("test@example.com", "wrongpassword")
        val errorBodyString = "{\"error\":\"Invalid credentials\"}"
        val errorBody = errorBodyString.toResponseBody(null)
        val errorResponse: Response<LoginResponse> = Response.error(401, errorBody)

        coEvery { mockApiService.login(loginRequest) } returns errorResponse

        viewModel.iniciarSesion("test@example.com", "wrongpassword")

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is LoginUiState.Error)
        assertEquals(errorBodyString, (state as LoginUiState.Error).message)
    }

    @Test
    fun `Cuando el login falla por NETWORK ERROR, estado cambia a Error`() = runTest {
        val loginRequest = LoginRequest("test@example.com", "password")
        val exception = RuntimeException("Network error")
        coEvery { mockApiService.login(loginRequest) } throws exception

        viewModel.iniciarSesion("test@example.com", "password")

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is LoginUiState.Error)
        assertEquals("Error de conexión: ${exception.message}", (state as LoginUiState.Error).message)
    }

    @Test
    fun `Cuando se llama a resetState, estado devuelve al Idle`() = runTest {
        viewModel.resetState()
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }
}
