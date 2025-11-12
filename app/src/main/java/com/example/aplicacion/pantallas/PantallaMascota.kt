package com.example.aplicacion.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacion.R
import androidx.navigation.NavHostController
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.ui.zIndex

@Composable
fun PantallaMascota(navController: NavHostController) {
    var nivelMascota by remember { mutableStateOf(20) }
    var menuExpandido by remember { mutableStateOf(false) }

    // Selecciona imágenes según nivel
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

    val todasLasTareas = listOf(
        "Alimenta al gatito" to 10,
        "Hacer la cama " to 5,
        "Cepillarse los dientes" to 5,
        "Estudio" to 20,
        "Hacer la cama" to 5,
        "Ordenar tu escritorio" to 10,
        "Lavar los platos" to 10,
        "Limpiar tu habitación" to 15,
        "Sacar la basura" to 10,
        "Regar las plantas" to 10,
        "Levantarte a tiempo" to 5,
        "Acomodar tu espacio" to 5,
        "Prepararte un desayuno saludable" to 10,
        "Tomar agua" to 5,
        "Salir a tomar aire o sol unos minutos" to 10,
        "Hacer estiramientos o yoga" to 10,
        "Darte un descanso sin pantallas" to 5,
        "Estudiar 30 minutos" to 15,
        "Organizar tus apuntes" to 10,
        "Anotar tus pendientes del día" to 5,
        "Completar una tarea o trabajo" to 20,
        "Leer un capítulo de un libro" to 5,
        "Aprender algo nuevo hoy" to 15,
        "Enviar un mensaje lindo a alguien" to 5,
        "Pasar tiempo con familia o amigos" to 10,
        "Ayudar a alguien con algo" to 20,
        "Sonreírle a alguien (o a tu mascota :0)" to 5,
        "Compartir algo positivo en redes" to 5,
        )

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_pantalla_mascota),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // misiones randoms :3
        var tareasMostradas by remember { mutableStateOf(todasLasTareas.shuffled().take(3)) }
        // tareas personalizadas
        var nuevaTarea by remember { mutableStateOf("") }
        var tareasPersonalizadas by remember { mutableStateOf(listOf<Pair<String, Int>>()) }


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

        //Botón regreso
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

        // tareas
        IconButton(
            onClick = { menuExpandido = !menuExpandido },
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


        // tareas waa
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

                val todasLasTareasMostradas = remember(tareasPersonalizadas) {
                    tareasMostradas + tareasPersonalizadas
                }

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

                    todasLasTareasMostradas.forEach { tarea ->
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

                    Button(
                        onClick = {
                            val puntos = (20..30).random()
                            if (nuevaTarea.isNotBlank()) {
                                tareasPersonalizadas = tareasPersonalizadas + (nuevaTarea to puntos)
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

    }//fin?
}//fin
