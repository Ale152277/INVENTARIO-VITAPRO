package com.example.vitaequip.ui.main.fragments.reportes.form

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.data.repository.ReporteIncidenciaRepository
import kotlinx.coroutines.launch
import com.example.vitaequip.util.Resource


class ReporteFormViewModel: ViewModel(){

    private val repository = ReporteIncidenciaRepository()

    private val _guardado = MutableLiveData<Resource<ReporteIncidencia>>()
    val guardado : LiveData<Resource<ReporteIncidencia>> = _guardado

    fun crearReporte (reporte : ReporteIncidencia){

        if(reporte.descripcion.isBlank()){
            _guardado.value = Resource.Error("Debe llenar el campo de descripción")
            return
        }

        _guardado.value = Resource.Loading

        viewModelScope.launch {
            try {
                repository.crear(reporte)
                _guardado.value = Resource.Success(reporte)

            }catch (e: Exception){
                _guardado.value = Resource.Error(e.message ?: "Error al crear el reporte")
            }
        }
    }
}