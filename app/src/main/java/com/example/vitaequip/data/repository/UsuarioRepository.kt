package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Usuario
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest


class UsuarioRepository {
    private val tabla = SupabaseClient.client.postgrest.from("usuarios")

    suspend fun obtenerTodas(): List<Usuario>{
        return tabla.select().decodeList<Usuario>()
    }

    suspend fun obtenerPorAuthId(authId: String): Usuario?{
        return tabla.select {
            filter { eq("auth_id", authId) }
        }.decodeSingleOrNull<Usuario>()
    }

    suspend fun crear (usuario: Usuario){
        tabla.insert(usuario)
    }
}