package com.example.aplicacion.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion.data.remote.Tarea
import com.example.aplicacion.viewmodel.GenerarTareasUiState
import com.example.aplicacion.viewmodel.GenerarTareasViewModel

@Composable
fun GenerarTareasScreen(
    idUsuario: Int,
    onTareasGuardadas: () -> Unit,
    viewModel: GenerarTareasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var prompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("¿Qué tipo de tareas necesitas?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.generarTareas(prompt, idUsuario) },
            enabled = prompt.isNotBlank() && uiState !is GenerarTareasUiState.Loading
        ) {
            Text("Generar Tareas con IA")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is GenerarTareasUiState.Idle -> {
                Text("Escribe una idea para generar tareas, por ejemplo: \"organizar mi viaje a la playa\".")
            }
            is GenerarTareasUiState.Loading -> {
                CircularProgressIndicator()
            }
            is GenerarTareasUiState.Success -> {
                ListaDeTareasGeneradas(state.tareas, onTareasGuardadas)
            }
            is GenerarTareasUiState.Error -> {
                Text(state.message, color = Color.Red)
            }
        }
    }
}

@Composable
private fun ListaDeTareasGeneradas(
    tareas: List<Tarea>,
    onTareasGuardadas: () -> Unit
) {

    Column {
        Text("Tareas sugeridas por la IA:")
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tareas) { tarea ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = tarea.descripcion, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onTareasGuardadas) {
            Text("Añadir estas tareas a mi lista")
        }
    }
}
