package com.example.aplicacion.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

object GeminiClient {

    private const val API_KEY = "AIzaSyDS6F6WEoyaTkRYfYxQeaJD_vrFxny5kKg"

    private val config = generationConfig {
        temperature = 0.7f
    }

    val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = API_KEY,
            generationConfig = config
        )
    }
}
