package com.example.aplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.example.aplicacion.pantallas.HomeScreen
import com.example.aplicacion.pantallas.LoginScreen
import com.example.aplicacion.pantallas.PantallaMascota

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen(navController) }
                composable("mascota") { PantallaMascota(navController) }
                composable("login") {LoginScreen(navController) }
            }
        }
    }
}
