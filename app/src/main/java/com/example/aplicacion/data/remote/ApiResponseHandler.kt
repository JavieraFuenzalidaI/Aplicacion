package com.example.aplicacion.data.remote
import retrofit2.Response

// Esta función de ayuda revisa la respuesta de la API.
// Si todo está bien, devuelve los datos.
// Si hay un error, lanza una excepción para que el ViewModel la capture.
fun <T> handleApiResponse(response: Response<T>): T {
    if (response.isSuccessful) {
        // Si el cuerpo de la respuesta no es nulo, lo devolvemos.
        return response.body() ?: throw Exception("La respuesta del servidor está vacía.")
    } else {
        // Si la respuesta indica un error (ej: 404, 500), lanzamos una excepción con el detalle.
        throw Exception("Error del servidor: ${response.code()} - ${response.message()}")
    }
}
