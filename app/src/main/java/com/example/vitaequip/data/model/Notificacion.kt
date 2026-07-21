package com.example.vitaequip.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notificacion(
    val id: Long,
    val tipo: String,
    @SerialName("equipo_id")
    val equipoId: Long? = null,
    @SerialName("dirigido_a")
    val dirigidoA: RolUsuario? = null,
    val mensaje: String,
    val leida: Boolean = false,
    @SerialName("creado_en")
    val creadoEn: String
)