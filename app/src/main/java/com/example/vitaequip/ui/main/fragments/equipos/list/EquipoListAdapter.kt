package com.example.vitaequip.ui.main.fragments.equipos.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vitaequip.R
import com.example.vitaequip.databinding.ItemEquipoBinding

class EquipoListAdapter (
    private val onEditarClick: (EquipoUiModel) -> Unit,
    private val onHistorialClick: (EquipoUiModel) -> Unit,

): RecyclerView.Adapter<EquipoListAdapter.EquipoViewHolder>(){
    private var lista: List<EquipoUiModel> = emptyList()
    fun actualizarLista(nuevaLista: List<EquipoUiModel>){
        val diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(
            EquipoDiffCallback(lista, nuevaLista)
        )
        lista = nuevaLista
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = lista.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {
        val binding = ItemEquipoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EquipoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipoViewHolder, position: Int) {
        holder.bind(lista[position])
    }
    inner class EquipoViewHolder(private val binding: ItemEquipoBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(equipo: EquipoUiModel){
            binding.tvCodigo.text = equipo.codigo
            binding.tvDescripcion.text = equipo.descripcion
            binding.tvMarcaArea.text = "${equipo.marcaNombre} . ${equipo.areaNombre}"
            binding.tvEstadoBadge.text = equipo.estadoNombre

            val (fondoBadge, colorTexto) = when (equipo.estadoNombre){
                "Operativo" -> R.drawable.bg_badge_operativo to R.color.estado_operativo
                "Mantenimiento" -> R.drawable.bg_badge_mantenimiento to R.color.estado_mantenimiento
                "Fuera de Servicio" -> R.drawable.bg_fuera_servicio to R.color.estado_fuera_servicio

                else-> R.drawable.bg_badge_operativo to R.color.gris_texto
            }
            binding.tvEstadoBadge.setBackgroundColor(fondoBadge)
            binding.tvEstadoBadge.setTextColor(
                androidx.core.content.ContextCompat.getColor(binding.root.context, colorTexto)
            )
            binding.btnEditar.setOnClickListener { onEditarClick(equipo) }
            binding.btnHistorial.setOnClickListener { onHistorialClick(equipo) }
        }
    }
}