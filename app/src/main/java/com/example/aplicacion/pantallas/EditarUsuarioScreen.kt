package com.example.aplicacion.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun EditarUsuarioScreen(
    navController: NavHostController,
    usuarioId: String, // ID del usuario a editar
    rolUsuarioLogueado: String // Rol del usuario actual ("admin" o "moderador")
) {
    // 1. Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    // ... otros campos como 'rol' y 'nivel' si el admin puede cambiarlos

    // 2. Lógica para cargar los datos del usuario a editar (usando un ViewModel y Retrofit)
    // LaunchedEffect(usuarioId) { /* Cargar datos del usuario con GET /usuarios/{usuarioId} */ }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Editando Usuario ID: $usuarioId")

        // Campo Nombre (editable por ambos)
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de usuario") },
            enabled = true // Siempre habilitado
        )

        // Campo contrasenia
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            enabled = (rolUsuarioLogueado == "admin") // Habilitado solo si es admin
        )

        // Campo Correo (editable solo por admin)
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electrónico") },
            enabled = (rolUsuarioLogueado == "admin") // Habilitado solo si es admin
        )

        // Campo Fecha (editable por ambos)
        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha de Nacimiento") },
            enabled = true // Siempre habilitado
        )

        // ... otros campos que solo el admin puede editar ...

        Button(onClick = {
            // 3. Lógica para guardar los cambios
            if (rolUsuarioLogueado == "admin") {
                // Llamar al endpoint PATCH /usuarios/admin/{id}
                // viewModel.actualizarUsuarioAdmin(usuarioId, nombre, correo, fecha, ...)
            } else if (rolUsuarioLogueado == "moderador") {
                // Llamar al endpoint PATCH /usuarios/moderador/{id}
                // viewModel.actualizarUsuarioModerador(usuarioId, nombre, fecha)
            }
        }) {
            Text("Guardar Cambios")
        }
    }
}
