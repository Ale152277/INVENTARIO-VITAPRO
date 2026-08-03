package com.example.vitaequip.ui.equipos.detail
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.model.EquipoHistorialEstado
import com.example.vitaequip.data.model.EquipoHistorialUbicacion
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.data.repository.AreaRepository
import com.example.vitaequip.data.repository.ReporteIncidenciaRepository
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.data.repository.EquipoHisUbiRepository
import com.example.vitaequip.data.repository.EquipoHistorialEstadoRepository
import com.example.vitaequip.data.repository.EstadoRepository
import com.example.vitaequip.data.repository.UsuarioRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch


class EquipoDetailViewModel : ViewModel(){
    private val equipoRepository = EquipoRepository()

    private val historialUbicacionRepository = EquipoHisUbiRepository()
    private val historialEstadoRepository = EquipoHistorialEstadoRepository()
    private val reporteRepository = ReporteIncidenciaRepository()
    private val estadoRepository = EstadoRepository()
    private val areaRepository = AreaRepository()
    private val usuarioRepository = UsuarioRepository()


    private val _equipo = MutableLiveData<Resource<Equipo>>()
    val equipo : LiveData<Resource<Equipo>> = _equipo


    private val _historialCombinado = MutableLiveData<Resource<List<EventoHistorialUiModel>>>()
    val historialCombinado : LiveData<Resource<List<EventoHistorialUiModel>>> = _historialCombinado

    private val _reportes = MutableLiveData<Resource<List<ReporteIncidencia>>>()
    val reportes : LiveData<Resource<List<ReporteIncidencia>>> = _reportes


    fun cargarEquipo (equipoId :Long){
        _equipo.value = Resource.Loading
        viewModelScope.launch {
            try {
                val resultado = equipoRepository.obtenerPorId(equipoId)
                if(resultado != null){
                    _equipo.value = Resource.Success(resultado)
                }else{
                    _equipo.value = Resource.Error("Equipo no encontrado")
                }

            }catch (e: Exception){
                _equipo.value = Resource.Error (e.message ?: "Error al cargar el equipo")
            }
        }
    }

    fun cargarHistorialCombinado(equipoId: Long){
        _historialCombinado.value = Resource.Loading

        viewModelScope.launch {
            try {
                val estados = historialEstadoRepository.obtenerPorEquipo(equipoId)
                val ubicaciones = historialUbicacionRepository.obtenerPorEquipo(equipoId)
                val estadosCatalogo = estadoRepository.obtenerTodas().associateBy { it.id }
                val areasCatalogo = areaRepository.obtenerTodas().associateBy { it.id }
                val usuariosCatalogo = usuarioRepository.obtenerTodas().associateBy { it.id }

                val eventosEstado = estados.map { e ->
                    EventoHistorialUiModel.CambioEstado(
                        fecha = e.fecha ?: "",
                        estadoAnteriorNombre = estadosCatalogo[e.estadoAnteriorId]?.nombre ?: "—",
                        estadoNuevoNombre = estadosCatalogo[e.estadoNuevoId]?.nombre ?: "—",
                        usuarioNombre = usuariosCatalogo[e.modificadoPor]?.nombre ?: "Sistema"
                    )
                }
                val eventosUbicacion = ubicaciones.map { u ->
                    EventoHistorialUiModel.CambioUbicacion(
                        fecha = u.fechaDesde,
                        areaNombre = areasCatalogo[u.areaId]?.nombre ?: "—",
                        usuarioNombre = usuariosCatalogo[u.modificadoPor]?.nombre ?: "Sistema"
                    )
                }
                val combinado = (eventosEstado + eventosUbicacion).sortedByDescending { it.fecha }
                _historialCombinado.value = Resource.Success(combinado)

            }catch (e: Exception){

                _historialCombinado.value = Resource.Error(e.message ?: "Error al cargar historial")

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