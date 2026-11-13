package com.univalle.dogapp.view.form

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.univalle.dogapp.R
import com.univalle.dogapp.viewmodel.FormCitaViewModel

/**
 * Logica comun de HU 3.0 (Nueva Cita) y HU 5.0 (Editar Cita):
 *  - toolbar curvada con flecha rosada que vuelve atras,
 *  - dropdown de sintomas con "Sintomas" por defecto,
 *  - autocompletado de razas por prefijo (Retrofit, una sola carga),
 *  - boton inactivo hasta completar los 4 campos y blanco bold al habilitarse,
 *  - mensaje "Selecciona un sintoma".
 *
 * Las subclases solo cambian textos, icono y a donde navegan al terminar.
 */
abstract class BaseFormCitaFragment : Fragment() {

    protected abstract val viewModel: FormCitaViewModel

    protected lateinit var ui: FormCitaUi
        private set

    protected abstract fun crearUi(): FormCitaUi

    protected abstract fun guardarEnViewModel(
        nombre: String,
        raza: String,
        propietario: String,
        telefono: String,
        sintoma: String
    )

    protected abstract fun navegarAlTerminar()

    private var guardadoAtendido = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui = crearUi()

        ui.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        configurarSintomas()
        configurarRazas()
        vigilarCampos()

        ui.btnGuardar.setOnClickListener { intentarGuardar() }

        viewModel.cargando.observe(viewLifecycleOwner) { cargando ->
            ui.progress.isVisible = cargando
            ui.btnGuardar.isEnabled = !cargando && camposCompletos()
        }

        viewModel.guardado.observe(viewLifecycleOwner) { hecho ->
            if (hecho && !guardadoAtendido) {
                guardadoAtendido = true
                viewModel.consumirGuardado()
                navegarAlTerminar()
            }
        }
    }

    private fun configurarSintomas() {
        val opciones = resources.getStringArray(R.array.sintomas)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, opciones)
        ui.etSintoma.setAdapter(adapter)
        // Criterio 6: la lista trae el item "Sintomas" por defecto (indice 0 = sin seleccion).
        ui.etSintoma.setText(opciones[0], false)
        ui.etSintoma.setOnClickListener { ui.etSintoma.showDropDown() }
    }

    private fun configurarRazas() {
        viewModel.razas.observe(viewLifecycleOwner) { razas ->
            ui.etRaza.setAdapter(RazasAdapter(requireContext(), razas))
        }
    }

    private fun camposCompletos(): Boolean =
        !ui.etNombre.text.isNullOrBlank() &&
            !ui.etRaza.text.isNullOrBlank() &&
            !ui.etPropietario.text.isNullOrBlank() &&
            !ui.etTelefono.text.isNullOrBlank()

    /**
     * Criterio 7 y 9: el boton solo se habilita con los cuatro campos llenos y
     * cuando se habilita el texto pasa a blanco bold.
     */
    private fun vigilarCampos() {
        val revisar = {
            val listo = camposCompletos()
            val color = ContextCompat.getColor(
                requireContext(),
                if (listo) R.color.white else R.color.dogapp_pink_disabled
            )
            ui.btnGuardar.isEnabled = listo
            ui.btnGuardar.setTextColor(color)
            ui.btnGuardar.typeface = if (listo) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            ui.btnGuardar.alpha = if (listo) 1f else 0.6f
        }

        listOf(ui.etNombre, ui.etRaza, ui.etPropietario, ui.etTelefono).forEach { campo ->
            campo.doAfterTextChanged { revisar() }
        }
        revisar()
    }

    private fun intentarGuardar() {
        val sintoma = ui.etSintoma.text?.toString()?.trim().orEmpty()
        // Criterio 8: campos llenos pero sin sintoma -> mensaje y no guarda.
        if (sintoma.isBlank() || sintoma == getString(R.string.hint_sintoma)) {
            Snackbar.make(ui.root, R.string.msg_sin_sintoma, Snackbar.LENGTH_LONG).show()
            return
        }

        guardarEnViewModel(
            nombre = texto(ui.etNombre.text?.toString()),
            raza = texto(ui.etRaza.text?.toString()),
            propietario = texto(ui.etPropietario.text?.toString()),
            telefono = texto(ui.etTelefono.text?.toString()),
            sintoma = sintoma
        )
    }

    protected fun texto(valor: String?): String = valor?.trim().orEmpty()
}
