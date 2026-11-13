package com.univalle.dogapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API publica de dog.ceo (GET, sin llave).
 */
interface DogApiService {

    /** Lista de razas para el autocompletado (HU 3.0 y HU 5.0). */
    @GET("api/breeds/list/all")
    suspend fun listarRazas(): BreedsResponse

    /**
     * Foto de la raza elegida. Esta es la ruta que cumple el requisito
     * "si la raza fue Akita la imagen debe ser de un Akita".
     */
    @GET("api/breed/{raza}/images/random")
    suspend fun fotoPorRaza(@Path("raza") raza: String): ImageResponse

    /**
     * La ruta que menciona el enunciado devuelve una raza aleatoria; se usa
     * solo como respaldo cuando la raza no existe en la API o falla la red.
     */
    @GET("api/breeds/image/random")
    suspend fun fotoAleatoria(): ImageResponse
}
