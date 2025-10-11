package com.example.aplicacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.aplicacion.R

@Composable
fun PantallaMascota() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "aqui va la mascota waos")
    }
    Image(
        painter = painterResource(id = R.drawable.fondo_pantalla_mascota),
        contentDescription = "Fondo Pantalla Mascota",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

}//Fin fun