package com.univalle.dogapp.view.nueva

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentNuevaCitaBinding
import com.univalle.dogapp.di.ServiceLocator
import com.univalle.dogapp.view.form.BaseFormCitaFragment
import com.univalle.dogapp.view.form.FormCitaUi
import com.univalle.dogapp.viewmodel.FormCitaViewModel

/**
 * HU 3.0: crear una cita. Inserta en Room y vuelve al Home, donde la cita
 * aparece por el LiveData del DAO.
 */
class NuevaCitaFragment : BaseFormCitaFragment() {

    private var _binding: FragmentNuevaCitaBinding? = null
    private val binding get() = _binding!!

    override val viewModel: FormCitaViewModel by viewModels {
        FormCitaViewModel.fabrica(ServiceLocator.citaRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaCitaBinding.inflate(inflater, container, false)
        return binding.root
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
        viewModel.guardar(nombre, raza, propietario, telefono, sintoma)
    }

    override fun navegarAlTerminar() {
        findNavController().navigate(R.id.action_nueva_to_home)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
