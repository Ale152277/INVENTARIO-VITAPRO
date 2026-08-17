package com.example.vitaequip.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class EquipoHistorialUbicacionInsert(

    @SerialName("equipo_id")
    val equipoId: Long,
    @SerialName("area_id")
    val areaId: Int? = null,
    @SerialName("modificado_por")
    val modificadoPor: Long? = null


)
