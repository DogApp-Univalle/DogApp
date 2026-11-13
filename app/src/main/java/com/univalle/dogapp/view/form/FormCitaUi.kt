package com.univalle.dogapp.view.form

import android.view.View
import android.widget.ProgressBar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

/**
 * Contrato de vistas del formulario de cita, para que HU 3.0 y HU 5.0 compartan
 * exactamente la misma logica de validacion y de autocompletado.
 */
class FormCitaUi(
    val root: View,
    val toolbar: MaterialToolbar,
    val etNombre: TextInputEditText,
    val etRaza: MaterialAutoCompleteTextView,
    val etPropietario: TextInputEditText,
    val etTelefono: TextInputEditText,
    val etSintoma: MaterialAutoCompleteTextView,
    val btnGuardar: MaterialButton,
    val progress: ProgressBar
)
