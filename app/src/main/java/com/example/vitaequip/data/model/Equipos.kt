package com.example.vitaequip.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Equipo(
    val id: Long,
    val descripcion: String,
    val codigo: String,
    val inventario: String? = null,
    @SerialName("marca_id")
    val marcaId: Int? = null,
    val modelo: String? = null,
    val serie: String? = null,
    val potencia: String? = null,
    @SerialName("bateria_voltaje")
    val bateriaVoltaje: Double? = null,
    @SerialName("bateria_amperaje")
    val bateriaAmperaje: Double? = null,
    @SerialName("bateria_cantidad")
    val bateriaCantidad: Int? = null,
    @SerialName("area_id")
    val areaId: Int? = null,
    @SerialName("ubicacion_especifica")
    val ubicacionEspecifica: String? = null,
    @SerialName("estado_id")
    val estadoId: Int? = null,
    @SerialName("fecha_ultimo_mantenimiento")
    val fechaUltimoMantenimiento: String? = null,
    @SerialName("fecha_proximo_mantenimiento")
    val fechaProximoMantenimiento: String? = null,
    val observaciones: String? = null,
    val recomendaciones: String? = null,
    @SerialName("imagen_url")
    val imagenUrl: String? = null,
    @SerialName("qr_url")
    val qrUrl: String? = null,
    @SerialName("creado_en")
    val creadoEn: String,
    @SerialName("actualizado_en")
    val actualizadoEn: String
)