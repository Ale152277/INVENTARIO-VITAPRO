package com.example.vitaequip.util

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    fun formatearFechaHora(fechaIso: String): String {
        return try {
            val limpio = fechaIso.substringBefore(".")
            LocalDateTime.parse(limpio).format(formatoSalida)

        }catch (e: Exception){
            Log.e("DateUtils", "No se pudo formatear la fecha: ${fechaIso}", e)

            fechaIso
        }
    }
}