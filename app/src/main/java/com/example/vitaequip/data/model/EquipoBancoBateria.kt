package com.example.vitaequip.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquipoBancoBateria(

    val id : Long? = null,
    @SerialName("equipo_id") val equipoId: Long,
    @SerialName("numero_banco") val numeroBanco: Int,
    @SerialName("bateria_voltaje") val bateriaVoltaje: Double? = null,
    @SerialName("bateria_amperaje") val bateriaAmperaje: Double? = null,
    @SerialName("bateria_cantidad") val bateriaCantidad: Int? = null

)
