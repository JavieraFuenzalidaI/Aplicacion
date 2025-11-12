package com.example.aplicacion.pantallas

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.model.Usuario

@Composable
fun PantallaMascota(
    navController: NavHostController,
    usuario: Usuario // 👈 pasamos el usuario logueado
) {
    val context = LocalContext.current
    val repo = remember { UsuarioRepository(context) }

    // cargar nivel y tareas de la BD del usuario actual
    var nivelMascota by remember { mutableStateOf(usuario.nivelMascota) }
    var tareasPersonalizadas by remember { mutableStateOf(repo.obtenerTareasUsuario(usuario.id)) }
    var menuExpandido by remember { mutableStateOf(false) }

    // 🖼 imágenes según nivel
    val corazonRes = when (nivelMascota) {
        in 0..20 -> R.drawable.corazon_0_20
        in 21..40 -> R.drawable.corazon_20_40
        in 41..60 -> R.drawable.corazon_40_60
        in 61..80 -> R.drawable.corazon_60_80
        else -> R.drawable.corazon_80_100
    }

    val gatoRes = when (nivelMascota) {
        in 0..20 -> R.drawable.av_gato_0_20
        in 21..40 -> R.drawable.av_gato_20_40
        in 41..60 -> R.drawable.av_gato_40_60
        in 61..80 -> R.drawable.av_gato_60_80
        else -> R.drawable.av_gato_80_100
    }

    // lista de sugerencias fijas
    val sugeridas = listOf(
        "Alimenta al gatito" to 10,
        "Hacer la cama" to 5,
        "Cepillarse los dientes" to 5,
        "Estudio" to 20,
        "Ordenar tu escritorio" to 10,
        "Tomar agua" to 5
    )
    // 3 random
    var tareasSugeridas by remember { mutableStateOf(sugeridas.shuffled().take(3)) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_pantalla_mascota),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Corazón
        Image(
            painter = painterResource(id = corazonRes),
            contentDescription = "Estado de ánimo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp)
                .size(150.dp)
        )

        // Mascota
        Image(
            painter = painterResource(id = gatoRes),
            contentDescription = "Mascota",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 65.dp)
        )

        // Botón regreso
        IconButton(
            onClick = {
                navController.navigate("sesion_iniciada") {
                    popUpTo(0)
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(30.dp)
                .size(85.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.btn_regreso_icon),
                contentDescription = "Volver al inicio"
            )
        }

        /** 📋 Icono de tareas **/
        IconButton(
            onClick = {
                tareasSugeridas = sugeridas.shuffled().take(3)
                menuExpandido = !menuExpandido
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(30.dp)
                .size(85.dp)
                .zIndex(2f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.tareas_icon),
                contentDescription = "Icono de Tareas"
            )
        }

        /** 📋 Panel de tareas **/
        var nuevaTarea by remember { mutableStateOf("") }

        AnimatedVisibility(
            visible = menuExpandido,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 20.dp, top = 130.dp)
                .zIndex(1f)
        ) {
            Surface(
                color = Color(0xFFFFE0CC),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp,
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier.width(260.dp)
            ) {
                val todasLasTareas = tareasSugeridas + tareasPersonalizadas

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Por hacer / Sugerencias",
                        fontSize = 20.sp,
                        color = Color(0xFF8B5C42),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Divider(color = Color(0xFFDE9C7C), thickness = 1.5.dp)

                    /** Lista de tareas **/
                    todasLasTareas.forEach { tarea ->
                        var checked by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    if (!checked) {
                                        nivelMascota =
                                            (nivelMascota + tarea.second).coerceAtMost(100)
                                        repo.actualizarNivelUsuario(usuario.id, nivelMascota)
                                        checked = true
                                    }
                                }
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (!checked) {
                                        nivelMascota =
                                            (nivelMascota + tarea.second).coerceAtMost(100)
                                        repo.actualizarNivelUsuario(usuario.id, nivelMascota)
                                    }
                                    checked = it
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF74C69D),
                                    uncheckedColor = Color(0xFF8B5C42)
                                )
                            )
                            Text(
                                text = tarea.first,
                                color = if (checked) Color(0x99755C48) else Color(0xFF755C48),
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Divider(color = Color(0xFFDE9C7C), thickness = 1.dp)

                    /** Campo para nueva tarea **/
                    OutlinedTextField(
                        value = nuevaTarea,
                        onValueChange = { nuevaTarea = it },
                        placeholder = { Text("¿Qué más piensas hacer?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF8B5C42),
                            unfocusedIndicatorColor = Color(0xFF8B5C42),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color(0xFF8B5C42)
                        )
                    )

                    /** Botón agregar **/
                    Button(
                        onClick = {
                            val puntos = (20..30).random()
                            if (nuevaTarea.isNotBlank()) {
                                repo.insertarTarea(usuario.id, nuevaTarea, puntos)
                                tareasPersonalizadas =
                                    repo.obtenerTareasUsuario(usuario.id)
                                nuevaTarea = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDE9C7C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Agregar tarea", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}