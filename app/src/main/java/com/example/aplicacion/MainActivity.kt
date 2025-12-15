package com.example.aplicacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplicacion.data.*
import com.example.aplicacion.ui.theme.*
import com.example.aplicacion.pantallas.*

// Se elimina la importación incorrecta de "VerUsuarios" del final de la lista
// import com.example.aplicacion.pantallas.VerUsuarios

class MainActivity : ComponentActivity() {

    // Launcher: permiso de notificaciones
    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                println("✅ Permiso de notificaciones concedido")
            } else {
                println("⚠️ Permiso de notificaciones denegado")
            }
        }

    // Función para pedir el permiso de notificaciones
    private fun pedirPermisoNotificacionesSiHaceFalta() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("📢 Solicitando permiso de notificaciones...")
                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                println("✅ Permiso de notificaciones ya concedido")
            }
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar barras del sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(
            android.view.WindowInsets.Type.statusBars() or
                    android.view.WindowInsets.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Cargar contenido Compose
        setContent {
            TaskiPetTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val sesionManager = remember { SesionManager(context) }
                val sesionGuardada = sesionManager.obtenerSesion()

                val startDestination = when {
                    sesionGuardada == "admin" -> "sesion_iniciada_admin"
                    sesionGuardada?.startsWith("superusuario/") == true -> {
                        val id = sesionGuardada.split("/").getOrNull(1)?.toIntOrNull() ?: 0
                        "sesion_iniciada_superusuario/$id"
                    }
                    sesionGuardada?.startsWith("moderador/") == true -> {
                        val id = sesionGuardada.split("/").getOrNull(1)?.toIntOrNull() ?: 0
                        "sesion_iniciada_moderador/$id"
                    }
                    sesionGuardada != null -> {
                        // Lógica unificada para "usuario/ID" o solo "ID"
                        val id = sesionGuardada.substringAfter("/").toIntOrNull() ?: sesionGuardada.toIntOrNull() ?: 0
                        "sesion_iniciada/$id"
                    }
                    else -> "home"
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable("login") {
                        LoginScreen(navController)
                    }
                    composable("home"){
                        HomeScreen(navController)
                    }

                    composable("register") {
                        RegisterScreen(navController)
                    }

                    composable(
                        route = "sesion_iniciada/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        SesionIniciadaScreen(navController, id)
                    }

                    composable(
                        route = "sesion_iniciada_superusuario/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        SuperUsuarioSesionIniciadaScreen(navController, id)
                    }

                    composable(
                        route = "sesion_iniciada_moderador/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        ModeradorSesionIniciadaScreen(navController, id)
                    }

                    composable("sesion_iniciada_admin") {
                        AdminSesionIniciadaScreen(navController)
                    }

                    // --- INICIO DE LAS RUTAS AÑADIDAS ---

                    // 1. Ruta para la pantalla de ver todos los usuarios
                    composable("ver_usuarios") {
                        VerUsuarios(navController = navController)
                    }

                    // 2. Ruta para la pantalla de editar un usuario específico
                    composable(
                        route = "editar_usuario/{usuarioId}/{rol}",
                        arguments = listOf(
                            navArgument("usuarioId") { type = NavType.StringType },
                            navArgument("rol") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
                        val rol = backStackEntry.arguments?.getString("rol") ?: ""
                        EditarUsuarioScreen(
                            navController = navController,
                            usuarioId = usuarioId,
                            rolUsuarioLogueado = rol
                        )
                    }

                    // --- FIN DE LAS RUTAS AÑADIDAS ---

                    composable(
                        route = "mascota/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        PantallaMascota(
                            navController = navController,
                            usuarioId = id,
                            esAdmin = false
                        )
                    }

                    composable("mascota_admin") {
                        PantallaMascota(
                            navController = navController,
                            usuarioId = 0, // El admin no tiene un ID de usuario específico aquí
                            esAdmin = true
                        )
                    }

                    composable(
                        route = "generar_tareas/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        GenerarTareasScreen(
                            idUsuario = id,
                            onTareasGuardadas = { navController.popBackStack() }
                        )
                    }
                }
            }
        }//fin setContent
    }
    override fun onResume() {
        super.onResume()
        pedirPermisoNotificacionesSiHaceFalta()
    }

    /** ====================== NOTIFICACIONES ======================= */
    fun mostrarNotificacionNivelBajo(context: Context) {
        val channelId = "taskipet_alertas"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de mascota",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.logo_app)
            .setContentTitle("Tu mascota te extraña demasiado")
            .setContentText("Su nivel de amor está bajo 🥺 ¡completa tareas!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}

