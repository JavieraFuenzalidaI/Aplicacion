package com.example.aplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion.pantallas.HomeScreen
import com.example.aplicacion.pantallas.LoginScreen
import com.example.aplicacion.pantallas.PantallaMascota

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController) }
                composable("mascota") { PantallaMascota() }
                composable("login") {LoginScreen(navController) }
            }
        }
    }
}
