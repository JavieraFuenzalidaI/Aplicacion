package com.example.aplicacion.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

object GeminiClient {

    private const val API_KEY = "ApiKey"

    // Configuración del modelo
    private val config = generationConfig {
        temperature = 0.7f 
    }

    // Inicialización del modelo de IA (CORREGIDO)
    val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-lite",
            apiKey = API_KEY,
            generationConfig = config
        )
    }
}
