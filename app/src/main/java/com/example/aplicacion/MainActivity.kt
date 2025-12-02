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
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplicacion.data.*
import com.example.aplicacion.ui.theme.*
import com.example.aplicacion.pantallas.*
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient
    private var healthAvailable = false
    private var yaPidioPermisos = false
    // Permisos de Health Connect
    private val healthPermissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )

    // Launcher: petición de permisos de Health Connect
    private val healthPermissionRequest =
        registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { granted ->
            println("➡️ Resultado permisos Health Connect: $granted")
            println("➡️ Permisos que estoy pidiendo: $healthPermissions")

            if (granted.containsAll(healthPermissions)) {
                println("✅ Permisos de Health Connect concedidos")
            } else {
                println("⚠️ Permisos de Health Connect denegados")
            }

            pedirPermisoNotificacionesSiHaceFalta()
        }

    // Launcher: permiso de notificaciones
    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                println("✅ Permiso de notificaciones concedido")
            } else {
                println("⚠️ Permiso de notificaciones denegado")
            }
        }

    /** ====================== PERMISOS ======================= */

// Solo controla el permiso de notificaciones
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

    // Función principal que arranca la cadena de permisos
    private fun pedirPermisos() {
        if (healthAvailable) {
            val granted = runBlocking {
                healthConnectClient.permissionController.getGrantedPermissions()
            }
            if (!granted.containsAll(healthPermissions)) {
                println("📢 Solicitando permisos de Health Connect...")
                // 👉 solo lanzamos ESTE; al terminar llamará a pedirPermisoNotificacionesSiHaceFalta()
                healthPermissionRequest.launch(healthPermissions)
            } else {
                println("✅ Permisos de Health Connect ya concedidos")
                // Si ya están concedidos, vamos directo a notificaciones
                pedirPermisoNotificacionesSiHaceFalta()
            }
        } else {
            // Si no hay Health Connect, solo pedimos notificaciones
            pedirPermisoNotificacionesSiHaceFalta()
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intentar inicializar Health Connect
        try {
            healthConnectClient = HealthConnectClient.getOrCreate(this)
            healthAvailable = true
            println("✅ HealthConnect disponible correctamente.")
        } catch (e: Exception) {
            healthAvailable = false
            println("⚠️ HealthConnect no disponible: ${e.message}")
        }

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
                val correoGuardado = sesionManager.obtenerSesion()

                NavHost(
                    navController = navController,
                    startDestination = when {
                        correoGuardado == "admin" -> "sesion_iniciada_admin"
                        correoGuardado != null -> "sesion_iniciada/${correoGuardado}"
                        else -> "home"
                    }
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }

                    composable(
                        route = "sesion_iniciada/{correo}",
                        arguments = listOf(navArgument("correo") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val correo = backStackEntry.arguments?.getString("correo")!!
                        SesionIniciadaScreen(navController, correo)
                    }

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
                            LoginScreen(navController)
                        }
                    }

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
        }//fin setContent
    }
    override fun onResume() {
        super.onResume()
        if (!yaPidioPermisos) {
            yaPidioPermisos = true
            pedirPermisos()
        }
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