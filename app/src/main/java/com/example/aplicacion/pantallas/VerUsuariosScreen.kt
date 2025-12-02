package com.example.aplicacion.pantallas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.aplicacion.data.remote.Usuario
import com.example.aplicacion.viewmodel.VerUsuariosUiState
import com.example.aplicacion.viewmodel.VerUsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerUsuarios(
    navController: NavHostController,
    viewModel: VerUsuariosViewModel = viewModel()
) {
    // Cargamos los usuarios en cuanto la pantalla es visible.
    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios registrados", color = Color.White, modifier = Modifier.fillMaxWidth()) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFAA847B)),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.btn_regreso_icon), contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFFFFF3E0)
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is VerUsuariosUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is VerUsuariosUiState.Success -> {
                    if (state.usuarios.isEmpty()) {
                        Text("No hay usuarios registrados", color = Color.Gray)
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            items(state.usuarios) { user ->
                                UsuarioItem(
                                    usuario = user,
                                    onEliminar = {
                                        viewModel.eliminarUsuario(user.id)
                                        Toast.makeText(context, "Usuario ${user.nombre} eliminado", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
                is VerUsuariosUiState.Error -> {
                    Text(state.message, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun UsuarioItem(usuario: Usuario, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAE6D9)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, color = Color(0xFFAA847B), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(usuario.correo, color = Color(0xFFA4A3A8), fontSize = 13.sp)
            }
            IconButton(onClick = onEliminar) {
                Image(
                    painter = painterResource(id = R.drawable.btn_eliminar_user),
                    contentDescription = "Eliminar usuario",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
