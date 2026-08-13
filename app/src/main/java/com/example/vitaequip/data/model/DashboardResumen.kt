package com.example.vitaequip.data.model

data class DashboardResumen(
    val totalEquipos: Int,
    val operativos: Int,
    val porcentajeOperativos: Int,
    val mantenimiento: Int,
    val fueraDeServicio: Int,
    val proximosMantenimientos: List<Equipo>
)