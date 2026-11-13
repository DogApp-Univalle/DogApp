package com.univalle.dogapp.view.home

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentHomeBinding
import com.univalle.dogapp.di.ServiceLocator
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.utils.Constants
import com.univalle.dogapp.viewmodel.HomeViewModel

/**
 * HU 2.0: lista de citas de Room. El boton atras del telefono cierra la app
 * (finishAffinity) en lugar de volver al login.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.fabrica(ServiceLocator.citaRepository(requireContext()))
    }

    private val adapter = CitaAdapter { cita -> abrirDetalle(cita) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.rvCitas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitas.adapter = adapter

        binding.fabNueva.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_nueva)
        }

        viewModel.citas.observe(viewLifecycleOwner) { citas ->
            adapter.submitList(citas)
            binding.tvVacio.isVisible = citas.isEmpty()
        }

        // Criterio 1: atras en el Home -> escritorio, nunca al login.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }
    }

    private fun abrirDetalle(cita: Cita) {
        findNavController().navigate(
            R.id.action_home_to_detalle,
            Bundle().apply { putInt(Constants.ARG_CITA_ID, cita.id) }
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
