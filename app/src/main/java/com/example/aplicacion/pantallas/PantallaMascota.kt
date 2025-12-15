package com.example.aplicacion.pantallas

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.data.remote.RetrofitClient
import com.example.aplicacion.viewmodel.MascotaScreenData
import com.example.aplicacion.viewmodel.MascotaUiState
import com.example.aplicacion.viewmodel.PantallaMascotaViewModel
import com.example.aplicacion.viewmodel.PantallaMascotaViewModelFactory

@Composable
fun PantallaMascota(
    navController: NavHostController,
    usuarioId: Int,
    esAdmin: Boolean = false
) {
    val application = LocalContext.current.applicationContext as Application
    val context = LocalContext.current

    val apiService = RetrofitClient.getInstance(context).instance
    val repository = remember { UsuarioRepository(apiService) }
    val factory = PantallaMascotaViewModelFactory(application, repository)
    val viewModel: PantallaMascotaViewModel = viewModel(factory = factory)

    LaunchedEffect(usuarioId) {
        viewModel.cargarDatosMascota(usuarioId)
    }

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is MascotaUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is MascotaUiState.Success -> {
            PantallaMascotaContent(navController, state.data, esAdmin, viewModel)
        }
        is MascotaUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaMascotaContent(
    navController: NavHostController,
    screenData: MascotaScreenData,
    esAdmin: Boolean,
    viewModel: PantallaMascotaViewModel
) {
    val usuario = screenData.usuario
    val tareasFromApi = screenData.tareas
    var menuExpandido by remember { mutableStateOf(false) }

    val corazonRes = when (usuario.nivel) {
        in 0..20 -> R.drawable.corazon_0_20
        in 21..40 -> R.drawable.corazon_20_40
        in 41..60 -> R.drawable.corazon_40_60
        in 61..80 -> R.drawable.corazon_60_80
        else -> R.drawable.corazon_80_100
    }

    val gatoRes = when (usuario.nivel) {
        in 0..20 -> R.drawable.av_gato_0_20
        in 21..40 -> R.drawable.av_gato_20_40
        in 41..60 -> R.drawable.av_gato_40_60
        in 61..80 -> R.drawable.av_gato_60_80
        else -> R.drawable.av_gato_80_100
    }

    Scaffold(
        floatingActionButton = {
            if (usuario.rol == "superuser") {
                FloatingActionButton(
                    onClick = { },
                    containerColor = Color(0xFFDE9C7C),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = "Generar tareas con IA")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(painter = painterResource(id = R.drawable.fondo_pantalla_mascota), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Image(painter = painterResource(id = corazonRes), contentDescription = "Estado de ánimo", modifier = Modifier.align(Alignment.TopCenter).padding(top = 150.dp).size(150.dp))
            Image(painter = painterResource(id = gatoRes), contentDescription = "Mascota", modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 65.dp, start = 20.dp))

            IconButton(
                onClick = {
                    if (esAdmin) {
                        navController.navigate("sesion_iniciada_admin") { popUpTo(0) }
                    } else {
                        navController.navigate("sesion_iniciada/${usuario.id}") { popUpTo(0) }
                    }
                },
                modifier = Modifier.align(Alignment.TopStart).padding(30.dp).size(85.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.btn_regreso_icon), contentDescription = "Volver")
            }

            Row(
                modifier = Modifier.align(Alignment.TopEnd).padding(30.dp).zIndex(2f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (usuario.rol == "superuser" && (usuario.racha ?: 0) > 0) {
                    Text("${usuario.racha} 🔥", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(onClick = { menuExpandido = !menuExpandido }, modifier = Modifier.size(85.dp)) {
                    Image(painter = painterResource(id = R.drawable.tareas_icon), contentDescription = "Tareas")
                }
            }

            AnimatedVisibility(visible = menuExpandido, modifier = Modifier.align(Alignment.TopEnd).padding(end = 20.dp, top = 130.dp).zIndex(1f)) {
                Surface(color = Color(0xFFFFE0CC), shape = RoundedCornerShape(12.dp), shadowElevation = 6.dp, border = BorderStroke(2.dp, Color.White), modifier = Modifier.width(260.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                        Text("Mis Tareas", fontSize = 20.sp, color = Color(0xFF8B5C42))
                        Divider(color = Color(0xFFDE9C7C), thickness = 1.5.dp)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x99FFFFFF)),
                        ) {
                            Text(
                                text = String.format("Km recorridos hoy: %.2f", screenData.kilometros),
                                fontSize = 16.sp,
                                color = Color(0xFF755C48),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }

                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(tareasFromApi) { tarea ->
                                val isChecked = tarea.completado == 1
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)

                                        .clickable(enabled = !isChecked) {
                                            viewModel.completarTarea(tarea)
                                        }

                                ) {
                                    Checkbox(checked = isChecked, onCheckedChange = null, enabled = !isChecked)
                                    Text(
                                        text = "${tarea.descripcion} (+${tarea.puntos})",
                                        color = if (isChecked) Color(0x99755C48) else Color(0xFF755C48),
                                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (!esAdmin) {
                            Divider(color = Color(0xFFDE9C7C), thickness = 1.dp)

                            var nuevaTareaText by remember { mutableStateOf("") }

                            OutlinedTextField(value = nuevaTareaText, onValueChange = { nuevaTareaText = it }, placeholder = { Text("Añadir nueva tarea...") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

                            Button(
                                onClick = {
                                    viewModel.agregarTarea(usuario.id, nuevaTareaText)
                                    nuevaTareaText = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDE9C7C)),
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Text("Agregar tarea", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
