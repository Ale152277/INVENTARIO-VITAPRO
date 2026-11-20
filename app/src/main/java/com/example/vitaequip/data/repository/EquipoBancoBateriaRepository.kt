package com.example.vitaequip.data.repository
import com.example.vitaequip.data.model.EquipoBancoBateria
import com.example.vitaequip.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
class EquipoBancoBateriaRepository {

    private val tabla = SupabaseClient.client.postgrest.from("equipos_banco_bateria")

    suspend fun obtenerPorEquipo(equipoId: Long): List<EquipoBancoBateria>{
        return tabla.select{
            filter{eq("equipo_id", equipoId)}
            order("numero_banco", Order.ASCENDING)
        }.decodeList<EquipoBancoBateria>()
    }


    suspend fun reemplazarBancos(
        equipoId: Long,
        bancos: List<EquipoBancoBateria>
    ){
        val bancosJson = buildJsonArray {
            bancos.forEach { banco ->
                add(buildJsonObject{
                    put("numero_banco", banco.numeroBanco )

                    banco.bateriaVoltaje?.let {
                    put("bateria_voltaje", it)
                    }

                    banco.bateriaAmperaje?.let {
                    put("bateria_amperaje", it)
                    }

                    banco.bateriaCantidad?.let{
                    put("bateria_cantidad", it)
                    }
                })
            }
        }

        val parametros = buildJsonObject {
            put("p_equipo_id", equipoId)
            put("p_bancos",bancosJson )
        }

        SupabaseClient.client.postgrest.rpc(
            "reemplazar_bancos_equipo", parametros
        )
    }
}