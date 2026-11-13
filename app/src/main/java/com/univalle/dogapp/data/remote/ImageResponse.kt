package com.univalle.dogapp.data.remote

/**
 * Respuesta de las dos rutas de imagen:
 * https://dog.ceo/api/breed/{raza}/images/random
 * https://dog.ceo/api/breeds/image/random
 * {"message": "https://images.dog.ceo/breeds/pug/n02110958_1.jpg", "status": "success"}
 */
data class ImageResponse(
    val message: String = "",
    val status: String = ""
)
