
package com.example.aplicacion.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aplicacion.data.remote.ApiService
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.data.remote.Tarea
import com.example.aplicacion.data.remote.Usuario
import com.example.aplicacion.util.StepCounter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class PantallaMascotaViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockApiService: ApiService

    @MockK
    private lateinit var mockApplication: Application

    @MockK
    private lateinit var mockSensorManager: SensorManager

    @MockK
    private lateinit var mockStepSensor: Sensor

    private lateinit var viewModel: PantallaMascotaViewModel

    private val testUser = Usuario(1, "Test User", "test@test.com", "", "", 5, "user")
    private val testTasks = listOf(Tarea(1, 1, "Test Task 1", 10, 0))

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.instance } returns mockApiService

        every { mockApplication.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockStepSensor

        viewModel = PantallaMascotaViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `cargarDatosMascota - success`() = runTest {
        coEvery { mockApiService.getUsuario(1) } returns Response.success(testUser)
        coEvery { mockApiService.getTareasPorUsuario(1) } returns Response.success(testTasks)

        viewModel.cargarDatosMascota(1)

        val state = viewModel.uiState.value
        assertTrue(state is MascotaUiState.Success)
        assertEquals(testUser, (state as MascotaUiState.Success).data.usuario)
        assertEquals(testTasks, state.data.tareas)
    }

    @Test
    fun `cargarDatosMascota - admin user`() = runTest {
        viewModel.cargarDatosMascota(-1)

        val state = viewModel.uiState.value
        assertTrue(state is MascotaUiState.Success)
        assertEquals(-1, (state as MascotaUiState.Success).data.usuario.id)
        assertTrue(state.data.tareas.isEmpty())
        coVerify(exactly = 0) { mockApiService.getUsuario(any()) } // Verify function was NOT called
    }
    
    @Test
    fun `completarTarea - success`() = runTest {
        val initialData = MascotaScreenData(testUser, testTasks)
        (viewModel.uiState as MutableStateFlow).value = MascotaUiState.Success(initialData)

        val tareaACompletar = testTasks.first()
        val nuevoNivel = testUser.nivel + 1

        val tareaActualizada = tareaACompletar.copy(completado = 1)
        coEvery { mockApiService.updateTarea(any(), any()) } returns Response.success(tareaActualizada)
        coEvery { mockApiService.updateUsuario(any(), any()) } returns Response.success(testUser)
        coEvery { mockApiService.getUsuario(testUser.id) } returns Response.success(testUser)
        coEvery { mockApiService.getTareasPorUsuario(testUser.id) } returns Response.success(testTasks)

        viewModel.completarTarea(tareaACompletar, nuevoNivel)

        coVerify { mockApiService.updateTarea(any(), any()) }
        coVerify { mockApiService.updateUsuario(any(), any()) }
        coVerify { mockApiService.getUsuario(testUser.id) } // From reload
    }

    @Test
    fun `stepCounter - actualizar kilometros`() = runTest(testDispatcher) {
        val stepsFlow = MutableStateFlow(10000) // Initial steps from sensor
        mockkConstructor(StepCounter::class)
        every { anyConstructed<StepCounter>().getSteps() } returns stepsFlow

        coEvery { mockApiService.getUsuario(1) } returns Response.success(testUser)
        coEvery { mockApiService.getTareasPorUsuario(1) } returns Response.success(testTasks)

        viewModel.cargarDatosMascota(1)
        advanceUntilIdle()

        viewModel.initializeStepCounter(mockApplication)
        advanceUntilIdle()

        val initialState = viewModel.uiState.value as MascotaUiState.Success
        assertEquals(0.0f, initialState.data.kilometros, 0.01f)

        stepsFlow.value = 11312
        advanceUntilIdle()

        val state1 = viewModel.uiState.value as MascotaUiState.Success
        assertEquals(1.0f, state1.data.kilometros, 0.01f)

        stepsFlow.value = 12624
        advanceUntilIdle()

        val state2 = viewModel.uiState.value as MascotaUiState.Success
        assertEquals(2.0f, state2.data.kilometros, 0.01f)
    }
}
