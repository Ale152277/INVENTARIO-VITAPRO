package com.example.vitaequip.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vitaequip.data.model.Usuario
import com.example.vitaequip.util.SessionManager


class MainViewModel : ViewModel (){

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario


    fun cargarUsuarioActual(){
        _usuario.value = SessionManager.usuarioActual
    }

    fun esAdmin(): Boolean{
        return SessionManager.esAdmin()
    }






}