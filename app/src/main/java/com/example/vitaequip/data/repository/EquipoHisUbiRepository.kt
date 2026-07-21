package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.EquipoHistorialUbicacion
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
class EquipoHisUbiRepository {
    private val tabla = SupabaseClient.client.postgrest.from("equipos_historial_ubicacion")


    suspend fun obtenerPorEquipo(equipoId: Long): List<EquipoHistorialUbicacion>{
        return tabla.select {
            filter { eq("equipo_id", equipoId) }
            order("fecha_desde", order= Order.DESCENDING)
        }.decodeList<EquipoHistorialUbicacion>()


    }

    suspend fun crear (registro: EquipoHistorialUbicacion){
        tabla.insert(registro)
    }

    suspend fun cerrarRegistroVigente(equipoId: Long, fechaHasta: String){
        tabla.update ({set("fecha_hasta", fechaHasta)}){
            filter{
                eq("equipo_id", equipoId)
                exact("fecha_hasta", null)
            }
        }
    }



}