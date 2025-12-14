
package com.example.aplicacion.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacion.data.remote.ApiService
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class SesionIniciadaViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockApiService: ApiService

    private lateinit var viewModel: SesionIniciadaViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.instance } returns mockApiService

        viewModel = SesionIniciadaViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is null`() {
        assertNull(viewModel.usuario.value)
    }

    @Test
    fun `cargarUsuarioPorId - success`() = runTest {
        val testUser = Usuario(1, "Test User", "test@test.com", "", "", 5, "user")
        coEvery { mockApiService.getUsuario(1) } returns Response.success(testUser)

        viewModel.cargarUsuarioPorId(1)

        assertEquals(testUser, viewModel.usuario.value)
    }

    @Test
    fun `cargarUsuarioPorId - invalid id`() = runTest {
        viewModel.cargarUsuarioPorId(0)
        coVerify(exactly = 0) { mockApiService.getUsuario(0) }
        assertNull(viewModel.usuario.value)

        viewModel.cargarUsuarioPorId(-1)
        coVerify(exactly = 0) { mockApiService.getUsuario(-1) }
        assertNull(viewModel.usuario.value)
    }

    @Test
    fun `cargarUsuarioPorId - api error`() = runTest {
        val errorResponse = Response.error<Usuario>(404, "Not Found".toResponseBody(null))
        coEvery { mockApiService.getUsuario(1) } returns errorResponse

        viewModel.cargarUsuarioPorId(1)

        assertNull(viewModel.usuario.value)
    }

    @Test
    fun `cargarUsuarioPorId - network exception`() = runTest {
        coEvery { mockApiService.getUsuario(1) } throws RuntimeException("Network error")

        viewModel.cargarUsuarioPorId(1)

        assertNull(viewModel.usuario.value)
    }
}
