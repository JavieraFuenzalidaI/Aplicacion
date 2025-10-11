package com.example.aplicacion.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.aplicacion.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //Fondo general
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

                // 🔸 Fila de botones personalizados lado a lado
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
                                // Acción para iniciar sesión
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
                                // Acción para registrarse
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
fun HomeScreenPreview(){
    HomeScreen()
}