package com.example.vitaequip.ui.main.fragments.inicio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitaequip.data.model.DashboardResumen
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch


class InicioLectorViewModel: ViewModel() {
    private val equipoRepository = EquipoRepository()

    private val _resumen = MutableLiveData<Resource<DashboardResumen>>()

    val resumen : LiveData<Resource<DashboardResumen>> = _resumen

    fun cargarResumen(){
        _resumen.value = Resource.Loading

        viewModelScope.launch {
         try {
             _resumen.value = Resource.Success(equipoRepository.obtenerResumen())
         }catch (e: Exception){
            _resumen.value = Resource.Error(e.message ?: "Error al cargar los datos ")
         }
        }
    }
}