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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion.R
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.viewmodel.LoginViewModel
import com.example.aplicacion.viewmodel.LoginViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val passwordFocusRequest = remember { FocusRequester() }
    val sesionManager = remember { com.example.aplicacion.data.SesionManager(context) }

    // --- ViewModel y Repository ---
    val repository = remember { UsuarioRepository(context) }
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(repository))

    // --- Estados de UI ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo_iniciar_sesion),
            contentDescription = "Fondo Login",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Campo de correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFAB91),
                    unfocusedBorderColor = Color(0xFFFFCCBC),
                    cursorColor = Color(0xFF6D4C41),
                    focusedLabelColor = Color(0xFF6D4C41)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequest.requestFocus() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequest),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFAB91),
                    unfocusedBorderColor = Color(0xFFFFCCBC),
                    cursorColor = Color(0xFF6D4C41),
                    focusedLabelColor = Color(0xFF6D4C41)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val usuario = viewModel.iniciarSesion(email, password)
                        if (usuario != null) {
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            navController.navigate("sesion_iniciada/${usuario.correo}")
                        } else {
                            errorMessage = "Correo o contraseña incorrectos"
                        }
                        focusManager.clearFocus()
                    }
                ),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // error
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botón de inicio de sesión ---

            Image(
                painter = painterResource(id = R.drawable.iniciar_sesion_btn),
                contentDescription = "Botón Iniciar sesión",
                modifier = Modifier
                    .width(180.dp)
                    .height(80.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            val usuario = viewModel.iniciarSesion(email, password)
                            if (usuario != null) {
                                //aki se guarda la sesión
                                sesionManager.guardarSesion(usuario.correo)
                                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("sesion_iniciada/${usuario.correo}")
                            } else {
                                errorMessage = "Correo o contraseña incorrectos"
                            }
                        } else {
                            errorMessage = "Completa todos los campos"
                        }
                    },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = Color(0xFF6D4C41),
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    navController.navigate("register")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController)
}
