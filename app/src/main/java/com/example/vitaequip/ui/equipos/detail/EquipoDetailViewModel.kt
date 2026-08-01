package com.example.vitaequip.ui.equipos.detail
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.model.EquipoHistorialEstado
import com.example.vitaequip.data.model.EquipoHistorialUbicacion
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.data.repository.ReporteIncidenciaRepository
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.data.repository.EquipoHisUbiRepository
import com.example.vitaequip.data.repository.EquipoHistorialEstadoRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch


class EquipoDetailViewModel : ViewModel(){
    private val equipoRepository = EquipoRepository()
    private val historialRepository = EquipoHisUbiRepository()
    private val reporteRepository = ReporteIncidenciaRepository()
    private val historialUbicacionRepository = EquipoHisUbiRepository()
    private val historialEstadoRepository = EquipoHistorialEstadoRepository()



    private val _equipo = MutableLiveData<Resource<Equipo>>()
    val equipo : LiveData<Resource<Equipo>> = _equipo


    private val _historialUbicacion = MutableLiveData<Resource<List<EquipoHistorialUbicacion>>>()
    val historialUbicacion : LiveData<Resource<List<EquipoHistorialUbicacion>>> = _historialUbicacion

    private val _historialEstado = MutableLiveData<Resource<List<EquipoHistorialEstado>>>()
    val historialEstado : LiveData<Resource<List<EquipoHistorialEstado>>> = _historialEstado

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

    fun cargarHistorialUbicacion(equipoId: Long){
        _historialUbicacion.value = Resource.Loading

        viewModelScope.launch {
            try {
                _historialUbicacion.value = Resource.Success(historialUbicacionRepository.obtenerPorEquipo(equipoId))

            }catch (e: Exception){

                _historialUbicacion.value = Resource.Error(e.message ?: "Error al cargar historial")

            }
        }
    }

    fun cargarHistorialEstado(equipoId: Long){
        _historialEstado.value = Resource.Loading

        viewModelScope.launch {
            try {
                _historialEstado.value = Resource.Success(historialEstadoRepository.obtenerPorEquipo(equipoId))

            }catch (e: Exception){
                _historialEstado.value = Resource.Error(e.message?:"Error al cargar el historial dado")

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