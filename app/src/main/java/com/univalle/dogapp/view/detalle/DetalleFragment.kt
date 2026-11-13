package com.univalle.dogapp.view.detalle

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.FragmentDetalleBinding
import com.univalle.dogapp.di.ServiceLocator
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.utils.Constants
import com.univalle.dogapp.viewmodel.DetalleViewModel

/**
 * HU 4.0: ficha del paciente, dos tarjetas superpuestas y dos FAB
 * (eliminar rosado a la izquierda, editar azul oscuro a la derecha).
 */
class DetalleFragment : Fragment(R.layout.fragment_detalle) {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    private val citaId: Int by lazy {
        arguments?.getInt(Constants.ARG_CITA_ID) ?: Constants.ID_INVALIDO
    }

    private val viewModel: DetalleViewModel by viewModels {
        DetalleViewModel.fabrica(ServiceLocator.citaRepository(requireContext()), citaId)
    }

    private var citaActual: Cita? = null
    private var saliendo = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetalleBinding.bind(view)

        if (citaId == Constants.ID_INVALIDO) {
            findNavController().navigateUp()
            return
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.fabEliminar.setOnClickListener {
            val cita = citaActual ?: return@setOnClickListener
            saliendo = true
            viewModel.eliminar(cita)
        }

        binding.fabEditar.setOnClickListener {
            val cita = citaActual ?: return@setOnClickListener
            findNavController().navigate(
                R.id.action_detalle_to_editar,
                Bundle().apply { putInt(Constants.ARG_CITA_ID, cita.id) }
            )
        }

        viewModel.cita.observe(viewLifecycleOwner) { cita ->
            if (cita == null) {
                if (!saliendo) findNavController().navigateUp()
                return@observe
            }
            citaActual = cita
            pintar(cita)
        }

        viewModel.eliminada.observe(viewLifecycleOwner) { eliminada ->
            if (eliminada) {
                findNavController().navigate(R.id.action_detalle_to_home)
            }
        }
    }

    private fun pintar(cita: Cita) {
        binding.toolbar.title = cita.nombreMascota
        binding.tvRaza.text = cita.raza
        binding.tvSintoma.text = cita.sintoma
        binding.tvTurno.text = getString(R.string.detalle_turno, cita.id)
        binding.tvPropietario.text = getString(R.string.detalle_propietario, cita.propietario)
        binding.tvTelefono.text = getString(R.string.detalle_telefono, cita.telefono)

        Glide.with(this)
            .load(cita.imagenUrl)
            .placeholder(R.drawable.ic_dog_placeholder)
            .error(R.drawable.ic_dog_placeholder)
            .centerCrop()
            .into(binding.ivMascota)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
