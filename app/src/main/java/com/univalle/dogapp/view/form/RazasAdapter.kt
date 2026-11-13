package com.univalle.dogapp.view.form

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

/**
 * Autocompletado de razas con filtro por PREFIJO (startsWith) y umbral 2 letras.
 *
 * El ArrayAdapter por defecto filtra con "contains" y en el orden de la lista,
 * asi que escribir "pu" podria devolver "bullpug" antes que "pug". El enunciado
 * (HU 3.0 criterio 3) exige "razas que comiencen por esas letras".
 *
 * performFiltering corre en un hilo de fondo: no se debe llamar a la API aqui,
 * por eso la lista completa ya viene cargada una sola vez desde el ViewModel.
 */
class RazasAdapter(
    context: Context,
    private val todas: List<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, todas.toMutableList()) {

    override fun getFilter(): Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val prefijo = constraint?.toString()?.trim()?.lowercase().orEmpty()
            val resultado = if (prefijo.length < 2) todas
            else todas.filter { it.lowercase().startsWith(prefijo) }

            return FilterResults().apply {
                values = resultado
                count = resultado.size
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            addAll(results?.values as? List<String> ?: emptyList())
            notifyDataSetChanged()
        }
    }
}
