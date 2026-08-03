package com.example.vitaequip.ui.equipos.detail

sealed class EventoHistorialUiModel {
    abstract val fecha: String

    data class CambioEstado(
        override val fecha: String,
        val estadoAnteriorNombre: String,
        val estadoNuevoNombre: String,
        val usuarioNombre: String,
    ): EventoHistorialUiModel()

    data class CambioUbicacion(
        override val fecha: String,
        val areaNombre: String,
        val usuarioNombre: String
    ): EventoHistorialUiModel()
}