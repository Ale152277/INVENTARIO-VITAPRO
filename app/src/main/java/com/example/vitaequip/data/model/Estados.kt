package com.example.vitaequip.data.model
import kotlinx.serialization.Serializable

@Serializable
data class Estados(
    val id: Int,
    val nombre: String
) {
}