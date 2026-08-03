package com.example.vitaequip.ui.equipos.detail
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vitaequip.R
import com.example.vitaequip.databinding.ItemEventoHistorialBinding


class EventoHistorialAdapter: RecyclerView.Adapter<EventoHistorialAdapter.EventoViewHolder>() {
    private var lista: List<EventoHistorialUiModel> = emptyList()

    fun actualizarLista(nuevaLista: List<EventoHistorialUiModel>){

        lista = nuevaLista
        notifyDataSetChanged()

    }

    override fun getItemCount() : Int = lista.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventoHistorialAdapter.EventoViewHolder {
        val binding = ItemEventoHistorialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        holder.bind(lista[position])
    }


    class EventoViewHolder (private val binding: ItemEventoHistorialBinding ):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(evento: EventoHistorialUiModel) {
            val contexto = binding.root.context

            when(evento){
                is EventoHistorialUiModel.CambioEstado->{
                    binding.tvDescripcionEvento.text = contexto.getString(
                        R.string.evento_cambio_estado,
                        evento.estadoAnteriorNombre,
                        evento.estadoNuevoNombre
                    )
                }
                is EventoHistorialUiModel.CambioUbicacion -> {
                    binding.tvDescripcionEvento.text = contexto.getString(
                        R.string.evento_cambio_ubicacion,
                        evento.areaNombre
                    )
                }
            }

            binding.tvFechaEvento.text = evento.fecha
            binding.tvUsuarioEvento.text = contexto.getString(
                R.string.autorizado_por,
                when(evento){
                    is EventoHistorialUiModel.CambioEstado -> evento.usuarioNombre
                    is EventoHistorialUiModel.CambioUbicacion -> evento.usuarioNombre
                }
            )

        }



    }
}