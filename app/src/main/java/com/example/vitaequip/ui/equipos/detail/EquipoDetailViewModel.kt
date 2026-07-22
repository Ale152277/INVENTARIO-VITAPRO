package com.example.vitaequip.ui.equipos.detail
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.model.EquipoHistorialUbicacion
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.data.repository.ReporteIncidenciaRepository
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.data.repository.EquipoHisUbiRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch


class EquipoDetailViewModel : ViewModel(){
    private val equipoRepository = EquipoRepository()
    private val historialRepository = EquipoHisUbiRepository()

    private val reporteRepository = ReporteIncidenciaRepository()


    private val _equipo = MutableLiveData<Resource<Equipo>>()
    private val equipo : LiveData<Resource<Equipo>> = _equipo


    private val _historial = MutableLiveData<Resource<List<EquipoHistorialUbicacion>>>()
    private val historial : LiveData<Resource<List<EquipoHistorialUbicacion>>> = _historial

    private val _reportes = MutableLiveData<Resource<List<ReporteIncidencia>>>()
    val reportes : LiveData<Resource<List<ReporteIncidencia>>> = _reportes


    fun cargarEquipo (equipoId :Long){
        _equipo.value = Resource.Loading
        viewModelScope.launch {
            try {
                val resultado = equipoRepository.obtenerPorId(equipoId)
                if(resultado!= null){
                    _equipo.value = Resource.Success(resultado)
                }else{
                    _equipo.value = Resource.Error("Equipo no encontrado")
                }

            }catch (e: Exception){
                _equipo.value = Resource.Error (e.message ?: "Error al cargar el equipo")
            }
        }
    }

    fun cargarHistorial (equipoId: Long){
        _equipo.value = Resource.Loading

        viewModelScope.launch {
            try {
                val resultado = historialRepository.obtenerPorEquipo(equipoId)

                _historial.value = Resource.Success(resultado)

            }catch (e: Exception){
                _historial.value = Resource.Error(e.message ?: "Error al cargar el historial")
            }
        }
    }

    fun cargarReportes (equipoId : Long){
        _reportes.value = Resource.Loading

        viewModelScope.launch {
            try {
                val resultado = reporteRepository.obtenerPorEquipo(equipoId)
                _reportes.value = Resource.Success(resultado)
            }catch (e: Exception){
                _reportes.value = Resource.Error(e.message ?: "Error al cargar los reportes")
            }
        }

    }
}