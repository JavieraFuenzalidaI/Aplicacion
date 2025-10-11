package com.example.aplicacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aplicacion.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable {
                    // Al tocar cualquier parte de la pantalla → ir a PantallaMascota
                    navController.navigate("mascota")
                }
        ) {
            // 🔹 Fondo general
            Image(
                painter = painterResource(id = R.drawable.fondo_inicio_aplicacion),
                contentDescription = "Fondo Pantalla Inicio",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ---- Botón Iniciar Sesión ----
                    Image(
                        painter = painterResource(id = R.drawable.iniciar_sesion_btn),
                        contentDescription = "Botón Iniciar Sesión",
                        modifier = Modifier
                            .width(150.dp)
                            .height(70.dp)
                            .clickable {
                                // Ejemplo: ir también a la pantalla mascota si lo deseas
                                //navController.navigate("iniciarSesion")
                            },
                        contentScale = ContentScale.Fit
                    )

                    // ---- Botón Registrarse ----
                    Image(
                        painter = painterResource(id = R.drawable.registrarse_btn),
                        contentDescription = "Botón Registrarse",
                        modifier = Modifier
                            .width(150.dp)
                            .height(70.dp)
                            .clickable {
                                // Otra acción
                            },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

}