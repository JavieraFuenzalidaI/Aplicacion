package com.example.aplicacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aplicacion.R

@Composable
fun ModeradorSesionIniciadaScreen(
    navController: NavHostController,
    id: Int // El ID del moderador, aunque no lo usemos en la UI, es bueno tenerlo
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Puedes usar el mismo fondo que el admin o uno diferente
        Image(
            painter = painterResource(id = R.drawable.fondo_sesion_iniciada_admin),
            contentDescription = "Fondo moderador",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido/a Moderador/a",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ver y editar usuarios (única función principal)
            Button(
                onClick = { navController.navigate("ver_usuarios") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41),
                    contentColor = Color.White
                ),
                modifier = Modifier.width(220.dp).height(50.dp)
            ) {
                Text("Ver y editar usuarios")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón para cerrar sesión
            Button(
                onClick = { navController.navigate("login") }, // Asumiendo que quieres que vaya al login
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6D4C41)
                )
            ) {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
