package com.example.aplicacion.pantallas

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
import com.example.aplicacion.data.PreferenciasDiarias
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.model.Usuario

@Composable
fun PantallaMascota(
    navController: NavHostController,
    usuario: Usuario
) {
    val context = LocalContext.current
    val repo = remember { UsuarioRepository(context) }

    // tareas sugeridas
    // lista de sugerencias fijas
    val sugeridas = listOf(
        "Alimenta al gatito" to 10,
        "Hacer la cama" to 5,
        "Cepillarse los dientes" to 5,
        "Estudio" to 20,
        "Ordenar tu escritorio" to 10,
        "Tomar agua" to 5,
        "Regar las plantas" to 10,
        "Sacar la basura" to 10,
        "Preparar tu mochila" to 5,
        "Leer un capítulo de un libro" to 15,
        "Lavar los platos" to 10,
        "Acariciar al gato" to 5,
        "Hacer ejercicio" to 20,
        "Meditar 5 minutos" to 10,
        "Guardar la ropa limpia" to 10,
        "Ayudar en casa" to 15,
        "Evitar el celular por 30 minutos" to 10,
        "Comer una fruta" to 5,
        "Limpiar tu habitación" to 15,
        "Practicar un hobby" to 15,
        "Revisar tus metas del día" to 5,
        "Planificar el día" to 10,
        "Revisar tus pendientes" to 10,
        "Responder correos importantes" to 10,
        "Asistir a una clase o reunión" to 15,
        "Tomar apuntes organizados" to 10,
        "Avanzar en un proyecto" to 15,
        "Estudiar durante 30 minutos concentrado" to 15,
        "Revisar y corregir tu trabajo" to 10,
        "Actualizar tu agenda o calendario" to 10,
        "Ordenar tu escritorio o espacio de trabajo" to 10,
        "Guardar archivos o respaldar tu trabajo" to 10,
        "Leer un artículo o documento de tu área" to 10,
        "Practicar una habilidad profesional" to 15,
        "Evitar distracciones durante 1 hora" to 10,
        "Participar en clase o en una reunión" to 10,
        "Resolver un problema o ejercicio difícil" to 15,
        "Enviar una tarea o informe a tiempo" to 10,
        "Configurar tus metas del día" to 5,
        "Hacer una pausa activa breve" to 5,
        "Evaluar tus logros del día" to 5
    )

    var nivelMascota by remember {
        mutableStateOf(PreferenciasDiarias.reiniciarNivelSiEsNuevoDia(context, repo, usuario.id))
    }

    var tareasCompletadas by remember { mutableStateOf(mutableSetOf<String>()) }

    // Tareas sugeridas actuales
    var tareasSugeridas by remember {
        mutableStateOf(
            PreferenciasDiarias.obtenerTareasDelDia(context, sugeridas)
                .filter { it.first !in tareasCompletadas }
        )
    }

    var tareasPersonalizadas by remember { mutableStateOf(repo.obtenerTareasUsuario(usuario.id)) }
    var menuExpandido by remember { mutableStateOf(false) }

    // imágenes según nivel
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

    // UI general
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
                .padding(top = 150.dp)
                .size(150.dp)
        )

        // Mascota
        Image(
            painter = painterResource(id = gatoRes),
            contentDescription = "Mascota",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 65.dp, start = 20.dp)
        )

        // Botón regreso
        IconButton(
            onClick = {
                navController.navigate("sesion_iniciada/${usuario.correo}") {
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
                contentDescription = "Volver a sesion iniciada"
            )
        }

        // Icono de tareas
        IconButton(
            onClick = {
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

        // Campo nueva tarea
        var nuevaTarea by remember { mutableStateOf("") }

        // Panel de tareas
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

                    //Lista de tareas
                    var estadosChecked by remember { mutableStateOf(mutableStateMapOf<String, Boolean>()) }

                    todasLasTareas.forEach { tarea ->
                        val desc = tarea.first
                        val checked = estadosChecked[desc] ?: false

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    if (!checked) {
                                        nivelMascota = (nivelMascota + tarea.second).coerceAtMost(100)
                                        repo.actualizarNivelUsuario(usuario.id, nivelMascota)
                                        estadosChecked[desc] = true
                                        if (desc in sugeridas.map { it.first }) {
                                            tareasCompletadas.add(desc)
                                        }
                                    }
                                }
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { nuevo ->
                                    if (!checked && nuevo) {
                                        nivelMascota = (nivelMascota + tarea.second).coerceAtMost(100)
                                        repo.actualizarNivelUsuario(usuario.id, nivelMascota)
                                        estadosChecked[desc] = true
                                        if (desc in sugeridas.map { it.first }) {
                                            tareasCompletadas.add(desc)
                                        }
                                    } else if (!nuevo) {
                                        estadosChecked[desc] = false
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF74C69D),
                                    uncheckedColor = Color(0xFF8B5C42)
                                )
                            )
                            Text(
                                text = desc,
                                color = if (checked) Color(0x99755C48) else Color(0xFF755C48),
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Divider(color = Color(0xFFDE9C7C), thickness = 1.dp)

                    // Añadir nueva tarea
                    var nuevaTarea by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = nuevaTarea,
                        onValueChange = { nuevaTarea = it },
                        placeholder = { Text("¿Qué más piensas hacer?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    Button(
                        onClick = {
                            val puntos = (5..15).random()
                            if (nuevaTarea.isNotBlank()) {
                                repo.insertarTarea(usuario.id, nuevaTarea, puntos)
                                tareasPersonalizadas = repo.obtenerTareasUsuario(usuario.id)
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // refrescar sugeridas
                    Button(
                        onClick = {
                            val restantes = sugeridas.filter { it.first !in tareasCompletadas }
                            tareasSugeridas = restantes.shuffled().take(3)
                            estadosChecked.clear()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD39B7C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dame más tareas >.<", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}