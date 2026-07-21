package com.example.vitaequip.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReporteIncidencia(
    val id: Long,
    @SerialName("equipo_id")
    val equipoId: Long,
    @SerialName("usuario_id")
    val usuarioId: Long,
    val descripcion: String,
    val atendido: Boolean = false,
    @SerialName("creado_en")
    val creadoEn: String
)