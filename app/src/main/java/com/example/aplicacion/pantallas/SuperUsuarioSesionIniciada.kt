package com.example.aplicacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // <-- ¡ESTE ES EL IMPORT QUE FALTABA!
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.SesionManager

@Composable
fun SuperUsuarioSesionIniciadaScreen(
    navController: NavHostController,
    id: Int
) {
    val context = LocalContext.current
    val sesionManager = remember { SesionManager(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_sesion_iniciada), // Fondo genérico
            contentDescription = "Fondo súper usuario",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido, Súper Usuario!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para ir a la mascota
            Button(
                onClick = { navController.navigate("mascota/$id") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41)),
                modifier = Modifier.width(250.dp).height(50.dp)
            ) {
                Text("Ir a mi mascota", color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón EXTRA para generar tareas
            Button(
                onClick = { navController.navigate("generar_tareas/$id") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)), // Color verde
                modifier = Modifier.width(250.dp).height(50.dp)
            ) {
                Text("Generar Tareas con IA", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón para cerrar sesión
            Button(
                onClick = {
                    sesionManager.cerrarSesion()
                    navController.navigate("login") { popUpTo(0) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D4C41)
                )
            }
        }
    }
}
