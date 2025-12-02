package com.example.aplicacion.pantallas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.data.SesionManager
import com.example.aplicacion.viewmodel.LoginUiState
import com.example.aplicacion.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sesionManager = remember { SesionManager(context) }

    val viewModel: LoginViewModel = viewModel()

    val loginState by viewModel.uiState.collectAsState()

    // --- Efectos secundarios basados en el estado ---
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginUiState.Success -> {
                // Éxito: Guardamos sesión, mostramos Toast y navegamos.
                val correo = state.loginResponse.usuario.correo
                sesionManager.guardarSesion(correo)
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                navController.navigate("sesion_iniciada/$correo") {
                    // Limpiamos la pila para que el usuario no vuelva al login.
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
                viewModel.resetState() // Reseteamos el estado para futuras operaciones.
            }
            is LoginUiState.Error -> {
                // Error: Mostramos el mensaje que viene de la API/ViewModel.
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState() // Permitimos que el usuario reintente.
            }
            else -> Unit // No hacemos nada en los estados Idle o Loading.
        }
    }

    // --- UI del Login ---
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_iniciar_sesion),
            contentDescription = "Fondo Login",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Si el estado es Loading, mostramos una barra de progreso y oscurecemos el fondo.
        if (loginState is LoginUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        } else {
            // Si no está cargando, mostramos el formulario.
            LoginForm(navController, viewModel, sesionManager)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginForm(
    navController: NavHostController,
    viewModel: LoginViewModel,
    sesionManager: SesionManager
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val passwordFocusRequest = remember { FocusRequester() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequest.requestFocus() })
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequest),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                handleLoginClick(email, password, context, navController, viewModel)
            }),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.iniciar_sesion_btn),
            contentDescription = "Botón Iniciar sesión",
            modifier = Modifier
                .width(180.dp)
                .height(80.dp)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    handleLoginClick(email, password, context, navController, viewModel)
                },
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿No tienes cuenta? Regístrate",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                navController.navigate("register")
            }
        )
    }
}

private fun handleLoginClick(
    email: String,
    password: String,
    context: android.content.Context,
    navController: NavHostController,
    viewModel: LoginViewModel
) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        return
    }
    // Mantenemos el login de admin como caso especial.
    if (email == "admin" && password == "adminpw") {
        Toast.makeText(context, "Bienvenido/a Administrador/a", Toast.LENGTH_SHORT).show()
        navController.navigate("sesion_iniciada_admin")
        return
    }
    
    // Para cualquier otro usuario, llamamos al ViewModel para que inicie la llamada a la API.
    viewModel.iniciarSesion(email, password)
}
