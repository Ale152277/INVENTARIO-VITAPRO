package com.example.vitaequip.data.repository
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class ReporteIncidenciaRepository {

    private val tabla = SupabaseClient.client.postgrest.from("reportes_incidencia")

    suspend fun obtenerPorEquipo(equipoId: Long): List<ReporteIncidencia>{
        return tabla.select {
            filter { eq("equipo_id", equipoId) }
        }.decodeList<ReporteIncidencia>()
    }

    suspend fun crear (reporte: ReporteIncidencia){
        tabla.insert(reporte)
    }

    suspend fun marcarAtentido (id: Long){
        tabla.update ({set("atendido", true)}){
            filter { eq("id", id) }
        }
    }
}