package com.example.vitaequip.data.model
import kotlinx.serialization.Serializable

@Serializable
data class Empresa(
    val id: Int,
    val nombre: String
)
