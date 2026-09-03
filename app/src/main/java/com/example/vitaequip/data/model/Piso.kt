package com.example.vitaequip.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Piso (
    val id: Int,
    val nombre: String,
    @SerialName("empresa_id")
    val empresaId: Int,
)
