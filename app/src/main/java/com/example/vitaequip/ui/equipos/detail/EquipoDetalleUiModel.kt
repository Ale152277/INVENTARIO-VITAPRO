package com.example.vitaequip.ui.equipos.detail

import com.example.vitaequip.data.model.Equipo

data class EquipoDetalleUiModel(
    val equipo: Equipo,
    val marcaNombre: String,
    val areaNombre: String,
    val pisoNombre: String
)