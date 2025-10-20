package com.example.aplicacion.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.aplicacion.R
import androidx.compose.ui.unit.sp


val PixelFont = FontFamily(Font(R.font.jersey10))

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PixelFont,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PixelFont,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PixelFont,
        fontSize = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PixelFont,
        fontSize = 14.sp
    )
)