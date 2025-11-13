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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion.R
import java.util.Calendar
import java.util.regex.Pattern
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion.data.UsuarioRepository
import com.example.aplicacion.viewmodel.RegistroViewModel
import com.example.aplicacion.viewmodel.RegistroViewModelFactory

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

    //ViewModel y Repository
    val repository = remember { UsuarioRepository(context) }
    val viewModel: RegistroViewModel = viewModel(factory = RegistroViewModelFactory(repository))

    // Estados del formulario
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var birthdayError by remember { mutableStateOf("") }

    fun validateFields(): Boolean {
        var valid = true

        if (username.isBlank()) {
            usernameError = "El nombre de usuario no puede estar vacío"
            valid = false
        } else usernameError = ""

        val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@gmail\\.com$")
        if (!emailPattern.matcher(email).matches()) {
            emailError = "Correo electrónico inválido (usa @gmail.com)"
            valid = false
        } else emailError = ""

        val passwordPattern = Pattern.compile("^(?=.*[0-9]).{6,}$")
        if (!passwordPattern.matcher(password).matches()) {
            passwordError = "Mínimo 6 caracteres y al menos un número"
            valid = false
        } else passwordError = ""

        if (birthday.isBlank()) {
            birthdayError = "Selecciona tu fecha de nacimiento"
            valid = false
        } else birthdayError = ""

        return valid
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_iniciar_sesion),
            contentDescription = "Fondo Registro",
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
            Image(
                painter = painterResource(id = R.drawable.img_crear_cuenta),
                contentDescription = "Texto crear cuenta"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                isError = usernameError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) })
            )
            if (usernameError.isNotEmpty())
                Text(usernameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            // Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                isError = emailError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() })
            )
            if (emailError.isNotEmpty())
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            //Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                isError = passwordError.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() })
            )
            if (passwordError.isNotEmpty())
                Text(passwordError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha de nacimiento
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    birthday = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                },
                year, month, day
            )

            OutlinedTextField(
                value = birthday,
                onValueChange = {},
                label = { Text("Fecha de nacimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFF0DCD8),
                    disabledLabelColor = Color(0xFF6D4C41),
                    disabledTextColor = Color.Black
                )
            )
            if (birthdayError.isNotEmpty())
                Text(birthdayError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(24.dp))

            //Botón de registro
            Button(
                onClick = {
                    if (validateFields()) {
                        val exito = viewModel.registrarUsuario(username, email, password, birthday)
                        if (exito) {
                            Toast.makeText(context, "¡Usuario registrado correctamente!", Toast.LENGTH_SHORT).show()
                            // Limpia campos
                            username = ""
                            email = ""
                            password = ""
                            birthday = ""
                            navController.navigate("login")
                        } else {
                            Toast.makeText(context, "Error al registrar usuario (correo ya registrado)", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0DCD8)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrarse", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link a Iniciar sesión
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = Color(0xFF6D4C41),
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    navController.navigate("login")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    RegisterScreen(navController)
}