package com.example.vitaequip.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RolUsuario{
    @SerialName("ADMIN")
    ADMIN,
    @SerialName("Lector")
    LECTOR
}


@Serializable
data class Usuario (
    val id: Long,
    val nombre: String,
    val correo: String,
    @SerialName("auth_id")
    val authId: String? = null,
    val rol: RolUsuario,
    val activo: Boolean = true,
    @SerialName("creado_en")
    val creadoEn: String
){
}