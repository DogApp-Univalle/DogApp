package com.univalle.dogapp.utils

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Envoltorio de BiometricPrompt (androidx.biometric:biometric).
 * HU 1.0 criterio 5: titulo, subtitulo y boton "Cancelar".
 * HU 1.0 criterio 6: huella correcta -> Home; incorrecta -> no entra.
 */
class BiometricHelper(
    private val activity: FragmentActivity,
    private val onExito: () -> Unit,
    private val onError: (String) -> Unit
) {

    /** Hay hardware biometrico y al menos una huella enrolada. */
    fun disponible(): Boolean =
        BiometricManager.from(activity)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS

    fun autenticar(titulo: String, subtitulo: String, textoCancelar: String) {
        val ejecutor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onExito()
            }

            override fun onAuthenticationError(codigoError: Int, mensaje: CharSequence) {
                // Cancelar o cerrar el dialogo no es un error que haya que mostrar.
                val silencioso = codigoError == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    codigoError == BiometricPrompt.ERROR_USER_CANCELED ||
                    codigoError == BiometricPrompt.ERROR_CANCELED
                if (!silencioso) onError(mensaje.toString())
            }

            // Huella no reconocida: el dialogo del sistema ya muestra el error
            // y se queda abierto para reintentar; la app no navega.
            override fun onAuthenticationFailed() = Unit
        }

        val prompt = BiometricPrompt(activity, ejecutor, callback)

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(titulo)
            .setSubtitle(subtitulo)
            .setNegativeButtonText(textoCancelar)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        prompt.authenticate(info)
    }
}
