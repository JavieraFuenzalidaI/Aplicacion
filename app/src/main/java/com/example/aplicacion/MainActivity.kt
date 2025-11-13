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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.ui.theme.TaskiPetTheme
import com.example.aplicacion.pantallas.HomeScreen
import com.example.aplicacion.pantallas.LoginScreen
import com.example.aplicacion.pantallas.PantallaMascota
import com.example.aplicacion.pantallas.RegisterScreen
import com.example.aplicacion.pantallas.SesionIniciadaScreen
import com.example.aplicacion.pantallas.VerUsuarios
import com.example.aplicacion.pantallas.AdminSesionIniciadaScreen

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
                val sesionManager = remember { com.example.aplicacion.data.SesionManager(context) }
                val correoGuardado = sesionManager.obtenerSesion()

                // Navegación principal
                NavHost(
                    // Si ya inició sesión se va a la pantalla del usuario
                    navController = navController,
                    startDestination = when {
                        correoGuardado == "admin" -> "sesion_iniciada_admin"
                        correoGuardado != null -> "sesion_iniciada/${correoGuardado}"
                        else -> "home"
                    }
                ) {
                    //rutas simples del home, login y register
                    composable("home") { HomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }

                    //sesion iniciada, son personalizadas para cada user
                    composable(
                        route = "sesion_iniciada/{correo}",
                        arguments = listOf(navArgument("correo") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val correo = backStackEntry.arguments?.getString("correo")!!
                        SesionIniciadaScreen(navController, correo)
                    }
                    //mascota propia de cada usuario
                    composable(
                        route = "mascota/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        val repo = UsuarioRepository(LocalContext.current)
                        val usuario = repo.obtenerUsuarioPorId(id)
                        if (usuario != null) {
                            PantallaMascota(navController, usuario)
                        } else {
                            // si no encuentra al usuario se devuelve al login (fallback)
                            LoginScreen(navController)
                        }
                    }
                    //mascota del admin (pide datos especificos de login)
                    composable("mascota_admin") {
                        val adminUsuario = com.example.aplicacion.model.Usuario(
                            id = -1,
                            nombre = "Administrador",
                            correo = "admin",
                            contrasena = "adminpw",
                            fechaNacimiento = "2024",
                            nivelMascota = 100
                        )
                        PantallaMascota(navController, adminUsuario, esAdmin = true)
                    }

                    composable("sesion_iniciada_admin") { AdminSesionIniciadaScreen(navController) }
                    composable("ver_usuarios") { VerUsuarios(navController) }
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

    // notificaciones
    fun mostrarNotificacionNivelBajo(context: Context) {
        val channelId = "taskipet_alertas"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal (solo una vez para Android 8+) (?)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de mascota",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando la energía de la mascota está baja"
            }
            manager.createNotificationChannel(channel)
        }

        // Datos de la notificaión
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.logo_app)
            .setContentTitle("Tu mascota te extraña demasiado")
            .setContentText("Su nivel de amor es bajo :c , ¡hora de completar tareas!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}