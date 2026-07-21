package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Piso
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class PisoRepository {

    private val tabla = SupabaseClient.client.postgrest.from("pisos")

    suspend fun obtenerTodos(): List<Piso>{
        return tabla.select().decodeList<Piso>()
    }
}