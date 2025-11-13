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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.LaunchedEffect
import com.example.aplicacion.MainActivity

@Composable
fun PantallaMascota(

    navController: NavHostController,
    usuario: Usuario,
    esAdmin: Boolean = false
) {
    val context = LocalContext.current
    val repo = remember { UsuarioRepository(context) }

    var nivelMascota by remember {
        mutableStateOf(
            PreferenciasDiarias.reiniciarNivelSiEsNuevoDia(context, repo, usuario.id)
        )
    }

    var tareasCompletadas by remember {
        mutableStateOf(
            PreferenciasDiarias.obtenerTareasCompletadas(context, usuario.id).toMutableSet()
        )
    }

    // tareas sugeridas
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

    val metaPasos = 1000
    var pasosActuales by remember { mutableStateOf(PreferenciasDiarias.obtenerPasos(context, usuario.id)) }

    val sensorManager = context.getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
    val stepListener = remember {
        object : SensorEventListener {
            var pasosIniciales: Int? = null
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val contador = event.values[0].toInt()
                    if (pasosIniciales == null) pasosIniciales = contador
                    val pasosHoy = contador - (pasosIniciales ?: contador)
                    PreferenciasDiarias.guardarPasos(context, usuario.id, pasosHoy)
                    pasosActuales = pasosHoy
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }
    // pa el tracker de pasos
    LaunchedEffect(Unit) {
        PreferenciasDiarias.reiniciarPasosSiEsNuevoDia(context, usuario.id)
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensor?.let {
            sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    LaunchedEffect(pasosActuales) {
        val nombreTarea = "Camina ${metaPasos} pasos!"
        if (pasosActuales >= metaPasos && !tareasCompletadas.contains(nombreTarea)) {
            val nuevas = tareasCompletadas.toMutableSet().apply { add(nombreTarea) }
            tareasCompletadas = nuevas

            nivelMascota = (nivelMascota + 20).coerceAtMost(100)
            repo.actualizarNivelUsuario(usuario.id, nivelMascota)

            PreferenciasDiarias.guardarTareaCompletada(context, usuario.id, nombreTarea)

            // la notificacion se manda si tiene <40 (gatito triste)
            if (nivelMascota <40) {
                (context as? MainActivity)?.mostrarNotificacionNivelBajo(context)
            }
        }
    }

    var tareasPersonalizadas by remember { mutableStateOf(repo.obtenerTareasUsuario(usuario.id)) }
    var tareasSugeridas by remember {
        mutableStateOf(
            sugeridas.filterNot { it.first in tareasCompletadas }.shuffled().take(3)
        )
    }

    var menuExpandido by remember { mutableStateOf(false) }

    //imagenes personalizadas para cada nivel de la mascota. Sincronizadas entre gato y corazao
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

    Box(modifier = Modifier.fillMaxSize()) {
        //fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_pantalla_mascota),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        //el corazao
        Image(
            painter = painterResource(id = corazonRes),
            contentDescription = "Estado de ánimo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 150.dp)
                .size(150.dp)
        )
        //mascota
        Image(
            painter = painterResource(id = gatoRes),
            contentDescription = "Mascota",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 65.dp, start = 20.dp)
        )
        //btn de volver a sesion iniciada
        IconButton(
            onClick = {
                if (esAdmin) {
                    navController.navigate("sesion_iniciada_admin") {
                        popUpTo(0)
                    }
                }else
                navController.navigate("sesion_iniciada/${usuario.correo}") { popUpTo(0) }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(30.dp)
                .size(85.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.btn_regreso_icon),
                contentDescription = "Volver"
            )
        }
        //btn de las tareas
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
                contentDescription = "Tareas"
            )
        }

        //el menu de tareas
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
                //Orden + lo que se va a mostrar, caminata y tareas siempre se muestran
                val todasLasTareas = listOf("Camina ${metaPasos} pasos!" to 20) +
                        tareasSugeridas + tareasPersonalizadas

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                    Text("Por hacer / Sugerencias", fontSize = 20.sp, color = Color(0xFF8B5C42))
                    Divider(color = Color(0xFFDE9C7C), thickness = 1.5.dp)

                    todasLasTareas.forEach { tarea ->
                        val desc = tarea.first
                        val checked = tareasCompletadas.contains(desc)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable(enabled = desc != "Camina ${metaPasos} pasos!") {
                                    if (!checked && desc != "Camina ${metaPasos} pasos!") {
                                        val nuevoNivel = (nivelMascota + tarea.second).coerceAtMost(100)
                                        nivelMascota = nuevoNivel
                                        repo.actualizarNivelUsuario(usuario.id, nuevoNivel)
                                        val nuevas = tareasCompletadas.toMutableSet().apply { add(desc) }
                                        tareasCompletadas = nuevas
                                        PreferenciasDiarias.guardarTareaCompletada(context, usuario.id, desc)
                                    }
                                }
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = null,
                                enabled = desc != "Camina ${metaPasos} pasos!",
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF74C69D),
                                    uncheckedColor = Color(0xFF8B5C42)
                                )
                            )
                            Text(
                                text = if (desc == "Camina ${metaPasos} pasos!") "$desc (${pasosActuales}/$metaPasos)"
                                else desc,
                                color = if (checked) Color(0x99755C48) else Color(0xFF755C48),
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Divider(color = Color(0xFFDE9C7C), thickness = 1.dp)

                    //aqui se crean las tareas personalizadas
                    var nuevaTarea by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = nuevaTarea,
                        onValueChange = { nuevaTarea = it },
                        placeholder = { Text("¿Qué más piensas hacer?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    // 3 Tareas randoms q se pueden refrescar
                    Button(
                        onClick = {
                            val puntos = (5..10).random()
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

                    Button(
                        onClick = {
                            tareasSugeridas = sugeridas
                                .filterNot { it.first in tareasCompletadas }
                                .shuffled()
                                .take(3)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD39B7C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dame más tareas >.<", color = Color.White, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    //solo pa el admin B)
                    if (esAdmin) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.White, thickness = 1.8.dp)
                        Text(
                            text = "Controles de administrador",
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        //boton peligroso :0 resetea el estado de la mascota del admin (pa testear cosas)
                        Button(
                            onClick = {
                                PreferenciasDiarias.resetTotal(context, repo, usuario.id)

                                nivelMascota =
                                    repo.obtenerUsuarioPorId(usuario.id)?.nivelMascota ?: 0
                                tareasCompletadas =
                                    PreferenciasDiarias.obtenerTareasCompletadas(context, usuario.id)
                                        .toMutableSet()
                                pasosActuales =
                                    PreferenciasDiarias.obtenerPasos(context, usuario.id)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD39B7C)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("Reset total", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}