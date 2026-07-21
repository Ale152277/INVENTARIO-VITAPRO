package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Estados
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class EstadoRepository {

    private val tabla = SupabaseClient.client.postgrest.from("estados")

    suspend fun obtenerTodas(): List<Estados>{
        return tabla.select().decodeList<Estados>()
    }
}