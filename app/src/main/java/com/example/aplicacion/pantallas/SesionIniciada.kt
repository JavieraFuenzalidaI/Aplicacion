package com.example.aplicacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import androidx.compose.ui.graphics.Color

@Composable
fun SesionIniciadaScreen(
    navController: NavHostController,
    username: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate("mascota")
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo_sesion_iniciada),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Bienvenido/a $username",
                //fontFamily = ,
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
    }
}