package com.example.aplicacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.activity.compose.setContent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.ui.theme.TaskiPetTheme
import com.example.aplicacion.pantallas.HomeScreen
import com.example.aplicacion.pantallas.LoginScreen
import com.example.aplicacion.pantallas.PantallaMascota
import com.example.aplicacion.pantallas.RegisterScreen
import com.example.aplicacion.pantallas.SesionIniciadaScreen


class MainActivity : ComponentActivity() {

    // pedir permisos
    private val permissionRequest =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            val notif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            else
                true

            if (fine || coarse) {
                println("✅ Permiso de ubicación concedido")
            } else {
                println("⚠️ Permiso de ubicación denegado")
            }

            if (notif) {
                println("✅ Permiso de notificaciones concedido")
            } else {
                println("⚠️ Permiso de notificaciones denegado")
            }
        }

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

        pedirPermisos()

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
                    composable("home") { HomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }

                    composable("sesion_iniciada/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        SesionIniciadaScreen(navController, username)
                    }

                    // ruta con correo
                    composable("mascota/{correo}") { backStackEntry ->
                        val correo = backStackEntry.arguments?.getString("correo") ?: ""
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

    // pedir permisos logica
    private fun pedirPermisos() {
        val permisos = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionRequest.launch(permisos.toTypedArray())
    }

    // noti: nivel de corazoncito bajo
    private fun mostrarNotificacionNivelBajo(context: Context) {
        val channelId = "taskipet_alertas"
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de mascota",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Tu mascota necesita ayuda")
            .setContentText("Su nivel de energía está bajo, ¡Hora de hacer tareas!")
            .setSmallIcon(R.mipmap.logo_app)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(1, notification)
    }
}
