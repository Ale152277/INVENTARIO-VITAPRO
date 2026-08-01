package com.example.vitaequip.data.repository
import com.example.vitaequip.data.model.EquipoHistorialEstado
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order


class EquipoHistorialEstadoRepository {
    private val tabla = SupabaseClient.client.postgrest.from("equipos_historial_estado")

    suspend fun registrar(historial: EquipoHistorialEstado){
        tabla.insert(historial)
    }

    suspend fun obtenerPorEquipo(equipoId: Long): List<EquipoHistorialEstado>{
        return tabla.select{
            filter{eq("equipo_id", equipoId)}
            order("fecha", Order.DESCENDING)
        }.decodeList()
    }
}