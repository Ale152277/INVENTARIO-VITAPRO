package com.example.vitaequip.data.repository

import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.remote.SupabaseClient
import com.example.vitaequip.data.model.DashboardResumen
import io.github.jan.supabase.postgrest.postgrest
import kotlin.Int

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

    suspend fun crear(equipo: Equipo): Equipo{
      return tabla.insert(equipo){
            select()
        }.decodeSingle<Equipo>()
    }

    suspend fun actualizar(id:Long, equipo: Equipo){
        tabla.update(equipo) {
            filter { eq("id", id) }
        }
    }


    suspend fun obtenerResumen(): DashboardResumen{

        val equipos = obtenerTodos()
        val estadoRepository = EstadoRepository()
        val estados = estadoRepository.obtenerTodas()

        val idEstadoOperativo = estados.find { it.nombre.equals("Operativo", ignoreCase = true) }?.id
        val idEstadoMantenimiento = estados.find{it.nombre.equals("Mantenimiento", ignoreCase = true)}?.id
        val idEstadoFueraServicio = estados.find{it.nombre.equals("Fuera de Servicio", ignoreCase = true)}?.id


        val operativos = equipos.count() {it.estadoId == idEstadoOperativo}
        val mantenimiento = equipos.count() {it.estadoId == idEstadoMantenimiento}
        val fueraDeServicio = equipos.count() {it.estadoId == idEstadoFueraServicio}
        val total = equipos.size
        val porcentaje = if (total > 0) {
            (operativos * 1000) / total
        } else{
            0
        }

        val hoy = java.time.LocalDate.now()

        val proximos = equipos
            .filter { it.fechaProximoMantenimiento != null }
            .filter {
                val fecha = java.time.LocalDate.parse(it.fechaProximoMantenimiento)
                val diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, fecha)
                diasRestantes in 0..14
            }
            .sortedBy { it.fechaProximoMantenimiento }

        return DashboardResumen(
            totalEquipos = total,
            operativos = operativos ,
            porcentajeOperativos = porcentaje,
            mantenimiento = mantenimiento,
            fueraDeServicio = fueraDeServicio,
            proximosMantenimientos = proximos
        )

    }


}