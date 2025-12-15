package com.example.aplicacion.pantallas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.remote.AdminUpdateUserData
import com.example.aplicacion.data.remote.ModeratorUpdateUserData
import com.example.aplicacion.viewmodel.EditarUsuarioUiState
import com.example.aplicacion.viewmodel.VerUsuariosViewModel
import com.example.aplicacion.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuarioScreen(
    navController: NavHostController,
    usuarioId: String,
    rolUsuarioLogueado: String,
    // --- INICIO DE LA CORRECCIÓN ---
    // Le pasamos el contexto real a la fábrica para que pueda crear el ViewModel.
    viewModel: VerUsuariosViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
    // --- FIN DE LA CORRECCIÓN ---
) {
    val context = LocalContext.current
    val editState by viewModel.editUiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf(0) }

    var expanded by remember { mutableStateOf(false) }
    val rolesDisponibles = listOf("usuario", "moderador", "admin")

    LaunchedEffect(key1 = usuarioId) {
        viewModel.cargarUsuarioParaEditar(usuarioId)
    }

    LaunchedEffect(key1 = editState) {
        when (val state = editState) {
            is EditarUsuarioUiState.Success -> {
                val usuario = state.usuario
                nombre = usuario.nombre
                correo = usuario.correo
                fecha = usuario.fecha
                rol = usuario.rol
                nivel = usuario.nivel
            }
            is EditarUsuarioUiState.UpdateSuccess -> {
                Toast.makeText(context, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                viewModel.resetEditState()
                navController.popBackStack()
            }
            is EditarUsuarioUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Usuario", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFAA847B)),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_regreso_icon),
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFFFF3E0)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Editando Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41))
                Text("ID de Usuario: $usuarioId", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de usuario") },
                    enabled = (rolUsuarioLogueado == "admin" || rolUsuarioLogueado == "moderador"),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha de Nacimiento") },
                    enabled = (rolUsuarioLogueado == "admin" || rolUsuarioLogueado == "moderador"),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo Electrónico") },
                    enabled = (rolUsuarioLogueado == "admin"),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Nueva Contraseña") },
                    placeholder = { Text("Dejar en blanco para no cambiar") },
                    enabled = (rolUsuarioLogueado == "admin"),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (rolUsuarioLogueado == "admin") {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = rol,
                            onValueChange = {},
                            label = { Text("Rol del usuario") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            rolesDisponibles.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        rol = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (rolUsuarioLogueado == "admin") {
                            val data = AdminUpdateUserData(nombre, correo, fecha, nivel, rol, if (contrasena.isBlank()) null else contrasena)
                            viewModel.guardarCambiosAdmin(usuarioId, data)
                        } else if (rolUsuarioLogueado == "moderador") {
                            val data = ModeratorUpdateUserData(nombre, fecha)
                            viewModel.guardarCambiosModerador(usuarioId, data)
                        }
                    },
                    enabled = editState !is EditarUsuarioUiState.Loading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                ) {
                    Text("Guardar Cambios", color = Color.White)
                }
            }
            if (editState is EditarUsuarioUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
