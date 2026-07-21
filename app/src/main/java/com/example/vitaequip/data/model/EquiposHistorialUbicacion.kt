package com.example.vitaequip.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquipoHistorialUbicacion(
    val id: Long,
    @SerialName("equipo_id")
    val equipoId: Long,
    @SerialName("area_id")
    val areaId: Int,
    @SerialName("modificado_por")
    val modificadoPor: Long? = null,
    @SerialName("fecha_desde")
    val fechaDesde: String,
    @SerialName("fecha_hasta")
    val fechaHasta: String? = null
)