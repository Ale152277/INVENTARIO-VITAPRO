package com.example.vitaequip.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquipoHistorialEstado(
    val id: Long? = null,
    @SerialName("equipo_id")
    val equipoId: Long,
    @SerialName("estado_anterior_id")
    val estadoAnteriorId: Int? = null,
    @SerialName("estado_nuevo_id")
    val estadoNuevoId: Int,
    @SerialName("modificado_por")
    val modificadoPor: Long? = null,
    val fecha: String? = null
)