package com.example.vitaequip.data.repository
import com.example.vitaequip.data.model.EquipoBancoBateria
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class EquipoBancoBateriaRepository {

    private val tabla = SupabaseClient.client.postgrest.from("equipos_banco_bateria")

    suspend fun obtenerPorEquipo(equipoId: Long): List<EquipoBancoBateria>{
        return tabla.select{
            filter{eq("equipo_id", equipoId)}
            order("numero_banco", Order.ASCENDING)
        }.decodeList<EquipoBancoBateria>()
    }


    suspend fun reemplazarBancos(equipoId: Long, bancos: List<EquipoBancoBateria>){
        tabla.delete { filter { eq("equipo_id", equipoId ) } }
        if (bancos.isNotEmpty()){
            tabla.insert(bancos)
        }else {
            println("Lista vacia, no se insertó nada")
        }
    }


}