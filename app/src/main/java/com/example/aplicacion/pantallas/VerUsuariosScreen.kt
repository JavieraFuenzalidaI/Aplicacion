package com.example.aplicacion.pantallas

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
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.model.Usuario
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerUsuarios(navController: NavHostController) {
    val context = LocalContext.current
    val repo = remember { UsuarioRepository(context) }

    var usuarios by remember { mutableStateOf(obtenerUsuarios(repo)) }

    //fondo y topbar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Usuarios registrados",
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFAA847B)
                ),
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // si no hay nadie registrado
            if (usuarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios registrados", color = Color.Gray)
                }
            } else {
                //muestra a los users encontrados y opcion de eliminar la cuenta
                LazyColumn(modifier = Modifier.fillMaxWidth() ) {
                    items(usuarios) { user ->
                        UsuarioItem(
                            usuario = user,
                            onEliminar = {
                                repo.eliminarUsuario(user.id)
                                usuarios = obtenerUsuarios(repo)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsuarioItem(usuario: Usuario, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor =Color(0xFFFAE6D9)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, color = Color(0xFFAA847B),fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)

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

fun obtenerUsuarios(repo: UsuarioRepository): List<Usuario> {
    return repo.obtenerTodosLosUsuarios()
}