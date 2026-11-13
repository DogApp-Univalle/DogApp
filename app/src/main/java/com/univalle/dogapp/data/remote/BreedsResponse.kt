package com.univalle.dogapp.data.remote

/**
 * Respuesta de https://dog.ceo/api/breeds/list/all
 * {"message": {"pug": [], "bulldog": ["boston", "english"]}, "status": "success"}
 */
data class BreedsResponse(
    val message: Map<String, List<String>> = emptyMap(),
    val status: String = ""
)
