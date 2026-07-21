package com.example.vitaequip.data.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Area(
    val id: Int,
    @SerialName("piso_id")
    val pisoId: Int,
    val nombre: String,
    val tipo: String? = null
) {
}