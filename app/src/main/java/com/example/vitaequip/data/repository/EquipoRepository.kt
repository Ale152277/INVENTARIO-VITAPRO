package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class EquipoRepository {
    private val tabla = SupabaseClient.client.postgrest.from("equipos")


    suspend fun obtenerTodos(): List<Equipo>{
        return  tabla.select().decodeList<Equipo>()
    }


    suspend fun obtenerPorId(id:Long): Equipo?{
        return tabla.select {
            filter { eq("id", id) }
        }.decodeSingleOrNull<Equipo>()
    }


    //servira para cuando escaneen el qr que contendrá el codigo del equipo
    //sinlgeornull porque espera una lista o un valor nulo (Asi evita que la app crashee)
    suspend fun obtenerPorCodigo(codigo: String): Equipo?{
        return tabla.select {
            filter { eq("codigo", codigo)}
        }.decodeSingleOrNull<Equipo>()

    }

    suspend fun crear(equipo: Equipo){
        tabla.insert(equipo)
    }

    suspend fun actualizar(id:Long, equipo: Equipo){
        tabla.update {
            filter { eq("id", id) }
        }
    }


}