package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Area
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class AreaRepository {
    private val tabla = SupabaseClient.client.postgrest.from("areas")

    suspend fun obtenerTodas():List<Area>{
        return tabla.select().decodeList<Area>()
    }

    suspend fun obtenerPorPiso(pisoId: Int): List<Area>{
        return tabla.select{
            filter { eq("piso_id", pisoId) }
        }.decodeList<Area>()
    }
}