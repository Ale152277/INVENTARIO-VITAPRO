package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Notificacion
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class NotificacionRepository {
    private val tabla = SupabaseClient.client.postgrest.from("notificaciones")

    suspend fun obtenerRecientes(): List<Notificacion> {
        return tabla.select {
            order("creado_en", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            limit(20)
        }.decodeList<Notificacion>()
    }

    suspend fun marcarLeida(id: Long) {
        tabla.update({ set("leida", true) }) {
            filter { eq("id", id) }
        }
    }
}