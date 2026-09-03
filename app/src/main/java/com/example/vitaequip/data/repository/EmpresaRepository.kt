package com.example.vitaequip.data.repository
import com.example.vitaequip.data.model.Empresa
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest


class EmpresaRepository {

    private val tabla = SupabaseClient.client.postgrest.from("empresas")


    suspend fun obtenerTodas(): List<Empresa>{
        return tabla.select().decodeList<Empresa>()
    }
}