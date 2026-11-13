package com.univalle.dogapp.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.univalle.dogapp.databinding.ItemCitaBinding
import com.univalle.dogapp.model.Cita

/**
 * ListAdapter con DiffUtil: la lista se actualiza sola cuando Room emite cambios.
 */
class CitaAdapter(
    private val alHacerClic: (Cita) -> Unit
) : ListAdapter<Cita, CitaViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemCitaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CitaViewHolder(binding, alHacerClic)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.pintar(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Cita>() {
            override fun areItemsTheSame(oldItem: Cita, newItem: Cita): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Cita, newItem: Cita): Boolean =
                oldItem == newItem
        }
    }
}
