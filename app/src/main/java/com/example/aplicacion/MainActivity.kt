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
import com.example.aplicacion.pantallas.SesionIniciadaScreen
import com.example.aplicacion.ui.theme.TaskiPetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskiPetTheme {
                val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home")
                    { HomeScreen(navController) }

                composable("mascota")
                    { PantallaMascota(navController) }

                composable("login")
                    {LoginScreen(navController) }

                composable("sesion_iniciada/{username}") { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: "Usuario"
                    SesionIniciadaScreen(navController, username)
                }
            }
            }
        }
    }
}
