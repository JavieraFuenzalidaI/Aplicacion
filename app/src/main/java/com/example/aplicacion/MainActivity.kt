package com.example.aplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.pantallas.HomeScreen
import com.example.aplicacion.pantallas.LoginScreen
import com.example.aplicacion.pantallas.PantallaMascota
import com.example.aplicacion.pantallas.SesionIniciadaScreen
import com.example.aplicacion.pantallas.RegisterScreen
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
                    composable("home") {
                        HomeScreen(navController)
                    }

                    composable("login") {
                        LoginScreen(navController)
                    }

                    composable("register") {
                        RegisterScreen(navController)
                    }

                    composable("sesion_iniciada/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        SesionIniciadaScreen(navController, username)
                    }

                    //Ruta con correo
                    composable("mascota/{correo}") { backStackEntry ->
                        val correo = backStackEntry.arguments?.getString("correo") ?: ""
                        val context = LocalContext.current
                        val usuarioRepo = UsuarioRepository(context)
                        val usuario = usuarioRepo.obtenerUsuarioPorCorreo(correo)

                        if (usuario != null) {
                            PantallaMascota(navController, usuario)
                        } else {
                            // Si no lo encuentra, volver al login
                            LoginScreen(navController)
                        }
                    }
                }
            }
        }
    }
}