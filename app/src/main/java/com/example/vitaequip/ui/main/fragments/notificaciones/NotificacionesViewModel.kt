package com.example.vitaequip.ui.main.fragments.notificaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.vitaequip.data.model.Notificacion
import com.example.vitaequip.data.repository.NotificacionRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch


class NotificacionesViewModel : ViewModel() {

    private val repository = NotificacionRepository()
    private val _notificaciones = MutableLiveData<Resource<List<Notificacion>>>()
    val notificaciones : LiveData<Resource<List<Notificacion>>> = _notificaciones

    fun cargarNotificaciones(){

        _notificaciones.value = Resource.Loading

        viewModelScope.launch {
            try {
                val resultado = repository.obtenerRecientes()
                _notificaciones.value = Resource.Success(resultado)

            }catch (e: Exception){
                _notificaciones.value = Resource.Error(e.message ?: "Error al cargar notificaciones")
            }
        }
    }

    fun marcarComoLeida(notificacionId: Long){
        viewModelScope.launch {
            try {
                repository.marcarLeida(notificacionId)
                cargarNotificaciones()

            }catch (e: Exception){

            }
        }


    }

}