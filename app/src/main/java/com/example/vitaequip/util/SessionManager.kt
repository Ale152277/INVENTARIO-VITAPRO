package com.example.vitaequip.util

import com.example.vitaequip.data.model.RolUsuario
import com.example.vitaequip.data.model.Usuario
import com.example.vitaequip.data.model.RolUsuario.ADMIN


object SessionManager {
    var usuarioActual: Usuario? = null
        private set

    fun iniciarSesion(usuario: Usuario){
        usuarioActual = usuario
    }

    fun cerrarSesion(){
        usuarioActual = null
    }

    fun esAdmin(): Boolean{
        return usuarioActual?.rol == ADMIN
    }
}