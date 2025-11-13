package com.example.aplicacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.data.SesionManager
import android.widget.Toast

@Composable
fun SesionIniciadaScreen(
    navController: NavHostController,
    correo: String
) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepository(context) }
    val usuario = remember { usuarioRepo.obtenerUsuarioPorCorreo(correo) }
    val sesionManager = remember { SesionManager(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_sesion_iniciada),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .clickable {
                    if (usuario != null) {
                        navController.navigate("mascota/${usuario.id}")
                    } else {
                        Toast.makeText(context, "Error: usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (usuario != null)
                    "Bienvenido/a ${usuario.nombre}"
                else "Usuario no encontrado",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pulsa para empezar",
                fontSize = 18.sp,
                color = Color(0xFF8D6E63)
            )
        }

        // Cerrar sesión
        Button(
            onClick = {
                sesionManager.cerrarSesion()
                navController.navigate("login") {
                    popUpTo(0)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFFFFF),
                contentColor = Color(0xFF6D4C41)
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .height(32.dp)
        ) {
            Text(
                text = "Cerrar Sesión",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Solo el admin puede ver usuarios registrados
        if (correo.lowercase() == "admin") {
            Button(
                onClick = { navController.navigate("verUsuarios") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBCAAA4),
                    contentColor = Color(0xFF3E2723)
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            ) {
                Text("Ver usuarios registrados", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}