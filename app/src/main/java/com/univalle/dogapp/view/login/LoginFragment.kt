package com.univalle.dogapp.view.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentLoginBinding
import com.univalle.dogapp.utils.BiometricHelper
import com.univalle.dogapp.viewmodel.LoginViewModel

/**
 * HU 1.0: fondo oscuro sin toolbar, marca con fuente externa, huella Lottie y
 * BiometricPrompt. Solo navega al Home si la huella es reconocida.
 */
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var biometrico: BiometricHelper
    private var navegado = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        biometrico = BiometricHelper(
            activity = requireActivity(),
            onExito = { viewModel.autenticacionExitosa() },
            onError = { mensaje -> mostrarError(mensaje) }
        )

        // Criterio 5: el toque sobre la huella abre la ventana emergente del sistema.
        binding.lottieHuella.setOnClickListener { pedirHuella() }

        viewModel.accesoConcedido.observe(viewLifecycleOwner) { concedido ->
            if (concedido) irAlHome()
        }
    }

    private fun pedirHuella() {
        if (!biometrico.disponible()) {
            mostrarError(getString(R.string.login_sin_huella))
            return
        }
        biometrico.autenticar(
            titulo = getString(R.string.login_titulo),
            subtitulo = getString(R.string.login_subtitulo),
            textoCancelar = getString(R.string.login_cancelar)
        )
    }

    private fun mostrarError(mensaje: String) {
        if (_binding == null) return
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).show()
    }

    private fun irAlHome() {
        if (navegado) return
        navegado = true
        // popUpTo login: el boton atras del Home no debe devolver al login.
        findNavController().navigate(R.id.action_login_to_home)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
