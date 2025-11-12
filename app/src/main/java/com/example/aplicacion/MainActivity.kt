package com.example.aplicacion

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.pantallas.HomeScreen
import com.example.aplicacion.pantallas.LoginScreen
import com.example.aplicacion.pantallas.PantallaMascota
import com.example.aplicacion.pantallas.RegisterScreen
import com.example.aplicacion.pantallas.SesionIniciadaScreen
import com.example.aplicacion.ui.theme.TaskiPetTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // fuera hud >:C
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(
            android.view.WindowInsets.Type.statusBars() or
                    android.view.WindowInsets.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        setContent {
            TaskiPetTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                // Sesión iniciada
                val sesionManager = remember { com.example.aplicacion.data.SesionManager(context) }
                val correoGuardado = sesionManager.obtenerSesion()

                // Navegación principal
                NavHost(
                    navController = navController,
                    startDestination = if (correoGuardado != null)
                        "sesion_iniciada/${correoGuardado}"
                    else
                        "home"
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
                        val username =
                            backStackEntry.arguments?.getString("username") ?: ""
                        SesionIniciadaScreen(navController, username)
                    }

                    // ruta con correo
                    composable("mascota/{correo}") { backStackEntry ->
                        val correo =
                            backStackEntry.arguments?.getString("correo") ?: ""
                        val usuarioRepo = UsuarioRepository(context)
                        val usuario = usuarioRepo.obtenerUsuarioPorCorreo(correo)

                        if (usuario != null) {
                            PantallaMascota(navController, usuario)
                        } else {
                            LoginScreen(navController)
                        }
                    }
                }
            }
        }
    }
}