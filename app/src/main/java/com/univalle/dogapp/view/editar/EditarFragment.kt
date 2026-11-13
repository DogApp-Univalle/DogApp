package com.univalle.dogapp.view.editar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentEditarBinding
import com.univalle.dogapp.di.ServiceLocator
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.utils.Constants
import com.univalle.dogapp.view.form.BaseFormCitaFragment
import com.univalle.dogapp.view.form.FormCitaUi
import com.univalle.dogapp.viewmodel.FormCitaViewModel

/**
 * HU 5.0: mismo formulario de la HU 3.0 precargado con los datos del detalle.
 * Al guardar actualiza Room y vuelve al Home. Si cambio la raza, el ViewModel
 * pide una foto nueva para esa raza.
 */
class EditarFragment : BaseFormCitaFragment() {

    private var _binding: FragmentEditarBinding? = null
    private val binding get() = _binding!!

    private val citaId: Int by lazy {
        arguments?.getInt(Constants.ARG_CITA_ID) ?: Constants.ID_INVALIDO
    }

    private var precargada = false

    override val viewModel: FormCitaViewModel by viewModels {
        FormCitaViewModel.fabrica(ServiceLocator.citaRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (citaId == Constants.ID_INVALIDO) {
            findNavController().navigateUp()
            return
        }

        viewModel.cargar(citaId)
        viewModel.cita.observe(viewLifecycleOwner) { cita ->
            if (cita != null && !precargada) {
                precargada = true
                volcar(cita)
            }
        }
    }

    private fun volcar(cita: Cita) {
        ui.etNombre.setText(cita.nombreMascota)
        ui.etRaza.setText(cita.raza, false)
        ui.etPropietario.setText(cita.propietario)
        ui.etTelefono.setText(cita.telefono)
        ui.etSintoma.setText(cita.sintoma, false)
    }

    override fun crearUi(): FormCitaUi = FormCitaUi(
        root = binding.root,
        toolbar = binding.toolbar,
        etNombre = binding.etNombre,
        etRaza = binding.etRaza,
        etPropietario = binding.etPropietario,
        etTelefono = binding.etTelefono,
        etSintoma = binding.etSintoma,
        btnGuardar = binding.btnGuardar,
        progress = binding.progress
    )

    override fun guardarEnViewModel(
        nombre: String,
        raza: String,
        propietario: String,
        telefono: String,
        sintoma: String
    ) {
        viewModel.actualizar(nombre, raza, propietario, telefono, sintoma)
    }

    override fun navegarAlTerminar() {
        findNavController().navigate(R.id.action_editar_to_home)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
