package com.example.aplicacion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TaskiPetColorScheme = lightColorScheme(
    primary = Color(0xFFFFAB91),        // Naranja durazno
    onPrimary = Color.White,
    secondary = Color(0xFFFFE0B2),      // Fondo más suave
    onSecondary = Color(0xFF6D4C41),    // Café
    background = Color(0xFFFFF3E0),     // Fondo general
    onBackground = Color(0xFF4E342E),   // Texto principal
    surface = Color(0xFFFFE0B2),
    onSurface = Color(0xFF5D4037)
)

@Composable
fun TaskiPetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TaskiPetColorScheme,
        typography = Typography,
        content = content
    )
}