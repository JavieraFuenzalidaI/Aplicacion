package com.example.aplicacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun AdminSesionIniciadaScreen(navController: NavHostController) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_sesion_iniciada_admin),
            contentDescription = "Fondo admin",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {},
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido/a Administrador/a",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ir a ver usuarios
            Button(
                onClick = { navController.navigate("ver_usuarios") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41),
                    contentColor = Color.White
                ),
                modifier = Modifier.width(220.dp).height(50.dp)
            ) {
                Text("Ver usuarios registrados")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // mascota del admin
            Button(
                onClick = { navController.navigate("mascota_admin") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41),
                    contentColor = Color.White
                ),
                modifier = Modifier.width(220.dp).height(50.dp)
            ) {
                Text("Ir a mascota")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // btn cerrar sesion
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6D4C41)
                )
            ) {
                Text(
                    text = "Cerrar Sesión", fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}