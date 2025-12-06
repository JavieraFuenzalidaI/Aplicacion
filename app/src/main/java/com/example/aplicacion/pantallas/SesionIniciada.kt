package com.example.aplicacion.pantallas

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.SesionManager
import com.example.aplicacion.data.remote.Usuario
import com.example.aplicacion.viewmodel.SesionIniciadaViewModel
import com.example.aplicacion.viewmodel.UsuarioUiState

@Composable
fun SesionIniciadaScreen(
    navController: NavHostController,
    correo: String,
    sesionViewModel: SesionIniciadaViewModel = viewModel()
) {
    LaunchedEffect(key1 = correo) {
        sesionViewModel.cargarUsuario(correo)
    }

    val uiState by sesionViewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_sesion_iniciada),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        when (val state = uiState) {
            is UsuarioUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is UsuarioUiState.Success -> {
                ContenidoSesionIniciada(navController, state.usuario)
            }
            is UsuarioUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ContenidoSesionIniciada(
    navController: NavHostController,
    usuario: Usuario
) {
    val context = LocalContext.current
    val sesionManager = remember { SesionManager(context) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .clickable {
                    navController.navigate("mascota/${usuario.id}")
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido/a ${usuario.nombre}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Pulsa para empezar",
                fontSize = 18.sp,
                color = Color(0xFF8D6E63)
            )
        }

        Button(
            onClick = {
                sesionManager.cerrarSesion()
                navController.navigate("login") { popUpTo(0) }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF6D4C41)
            ),
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp).height(32.dp)
        ) {
            Text("Cerrar Sesión", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        if (usuario.correo.lowercase() == "admin") {
            Button(
                onClick = { navController.navigate("ver_usuarios") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBCAAA4),
                    contentColor = Color(0xFF3E2723)
                ),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
            ) {
                Text("Ver usuarios registrados", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
