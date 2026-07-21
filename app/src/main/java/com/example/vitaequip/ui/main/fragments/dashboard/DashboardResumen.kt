package com.example.vitaequip.ui.main.fragments.dashboard


import com.example.vitaequip.data.model.Equipo

data class DashboardResumen(
    val totalEquipos: Int,
    val operativos: Int,
    val porcentajeOperativos: Int,
    val mantenimiento: Int,
    val fueraDeServicio: Int,
    val proximosMantenimientos: List<Equipo>
)