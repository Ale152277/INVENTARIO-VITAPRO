package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Marcas
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest



class MarcaRepository {

    private val tabla = SupabaseClient.client.postgrest.from("marcas")

    suspend fun obtenerTodas(): List<Marcas>{
        return tabla.select().decodeList<Marcas>()
    }
}