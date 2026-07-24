package com.example.vitaequip.ui.main.fragments.dashboard
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.databinding.ItemProximoMantenimientoBinding

class ProximoMantenimientoAdapter:
    RecyclerView.Adapter<ProximoMantenimientoAdapter.ProximoViewHolder>(){

    private var lista: List<Equipo> = emptyList()

    fun actualizarLista(nuevaLista: List<Equipo>){
        lista = nuevaLista
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = lista.size


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProximoViewHolder {
        val binding = ItemProximoMantenimientoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ProximoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProximoViewHolder, position: Int) {
        val equipo = lista[position]
        holder.bind(equipo)
    }


    class ProximoViewHolder(
        private val binding: ItemProximoMantenimientoBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(equipo: Equipo){
            binding.tvDescripcionEquipo.text = equipo.descripcion
            binding.tvCodigoEquipo.text = equipo.codigo

            val dias = calcularDiasRestantes(equipo.fechaProximoMantenimiento)
            binding.tvDiasRestantes.text = "${dias}"
        }


        private fun calcularDiasRestantes(fecha : String?): Long{
            if(fecha == null){
                return 0
            }

            val hoy = java.time.LocalDate.now()
            val fechaMantenimiento = java.time.LocalDate.parse(fecha)
            return java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaMantenimiento)

        }


    }

}

