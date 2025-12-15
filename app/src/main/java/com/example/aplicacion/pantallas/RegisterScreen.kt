package com.example.aplicacion.pantallas

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacion.R
import com.example.aplicacion.viewmodel.RegistroUiState
import com.example.aplicacion.viewmodel.RegistroViewModel
import java.util.Calendar
import java.util.regex.Pattern

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: RegistroViewModel = viewModel()
    val registerState by viewModel.uiState.collectAsState()

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegistroUiState.Success -> {
                Toast.makeText(context, "¡Usuario registrado correctamente!", Toast.LENGTH_SHORT).show()
                navController.navigate("login") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                viewModel.resetState()
            }
            is RegistroUiState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.fondo_iniciar_sesion),
            contentDescription = "Fondo Registro",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (registerState is RegistroUiState.Loading) {
            CircularProgressIndicator()
        } else {
            RegisterForm(navController, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(navController: NavHostController, viewModel: RegistroViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var birthdayError by remember { mutableStateOf("") }

    fun validateFields(): Boolean {
        usernameError = if (username.isBlank()) "El nombre no puede estar vacío" else ""
        emailError = if (!Pattern.matches("^[A-Za-z0-9+_.-]+@.+$", email)) "Correo inválido" else ""
        passwordError = if (password.length < 6) "Mínimo 6 caracteres" else ""
        birthdayError = if (birthday.isBlank()) "Selecciona tu fecha" else ""
        return usernameError.isEmpty() && emailError.isEmpty() && passwordError.isEmpty() && birthdayError.isEmpty()
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.img_crear_cuenta), contentDescription = "Texto crear cuenta")
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nombre de usuario") }, isError = usernameError.isNotEmpty(), modifier = Modifier.fillMaxWidth())
        if (usernameError.isNotEmpty()) Text(usernameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, isError = emailError.isNotEmpty(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }), modifier = Modifier.fillMaxWidth())
        if (emailError.isNotEmpty()) Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, isError = passwordError.isNotEmpty(), visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequester))
        if (passwordError.isNotEmpty()) Text(passwordError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))

        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(context, { _, y, m, d -> birthday = "$d/${m + 1}/$y" }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        OutlinedTextField(
            value = birthday, onValueChange = {}, label = { Text("Fecha de nacimiento") }, modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
            enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color(0xFFF0DCD8), disabledLabelColor = Color(0xFF6D4C41), disabledTextColor = Color.Black)
        )
        if (birthdayError.isNotEmpty()) Text(birthdayError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (validateFields()) {
                    viewModel.registrarUsuario(username, email, password, birthday, context)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0DCD8)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Registrarse", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Ya tienes cuenta? Inicia sesión",
            color = Color(0xFF6D4C41),
            modifier = Modifier.clickable { navController.navigate("login") }
        )
    }
}
