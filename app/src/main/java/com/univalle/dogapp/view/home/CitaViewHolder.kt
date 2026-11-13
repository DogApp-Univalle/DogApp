package com.univalle.dogapp.view.home

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.univalle.dogapp.R
import com.univalle.dogapp.databinding.ItemCitaBinding
import com.univalle.dogapp.model.Cita

/**
 * Item del Home: avatar circular (Glide), nombre, sintoma y turno "#n".
 */
class CitaViewHolder(
    private val binding: ItemCitaBinding,
    private val alHacerClic: (Cita) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun pintar(cita: Cita) {
        binding.tvNombre.text = cita.nombreMascota
        binding.tvSintoma.text = cita.sintoma
        binding.tvTurno.text = binding.root.context.getString(R.string.detalle_turno, cita.id)

        Glide.with(binding.root)
            .load(cita.imagenUrl)
            .placeholder(R.drawable.ic_dog_placeholder)
            .error(R.drawable.ic_dog_placeholder)
            .centerCrop()
            .into(binding.ivMascota)

        binding.cardCita.setOnClickListener { alHacerClic(cita) }
    }
}
