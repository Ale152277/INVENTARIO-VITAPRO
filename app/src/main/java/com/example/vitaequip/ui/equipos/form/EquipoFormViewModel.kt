package com.example.vitaequip.ui.equipos.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.example.vitaequip.data.model.*
import com.example.vitaequip.data.repository.*
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch



class EquipoFormViewModel : ViewModel() {
    private val equipoRepository = EquipoRepository()
    private val pisoRepository = PisoRepository()
    private val areaRepository = AreaRepository()
    private val marcaRepository = MarcaRepository()
    private val estadoRepository = EstadoRepository()

    private val _pisos = MutableLiveData<List<Piso>>()
    val pisos: LiveData<List<Piso>> = _pisos

    private val _areas = MutableLiveData<List<Area>>()
    val areas: LiveData<List<Area>> = _areas

    private val _estados = MutableLiveData<List<Estados>>()
    val estados: LiveData<List<Estados>> = _estados

    private val _marcas = MutableLiveData<List<Marcas>>()
    val marcas: LiveData<List<Marcas>> = _marcas

    private val _guardado = MutableLiveData<Resource<Equipo>>()
    val guardado: LiveData<Resource<Equipo>> = _guardado

    private val _equipoActual = MutableLiveData<Equipo?>()
    val equipoActual: LiveData<Equipo?> = _equipoActual

    fun cargarCatalogos() {
        viewModelScope.launch {
            _pisos.value = pisoRepository.obtenerTodos()
            _estados.value = estadoRepository.obtenerTodas()
            _marcas.value = marcaRepository.obtenerTodas()
        }

    }

    fun cargarAreasPorPiso(pisoId: Int) {
        viewModelScope.launch {
            _areas.value = areaRepository.obtenerPorPiso(pisoId)
        }

    }

    fun guardarEquipo(equipo: Equipo, esEdicion: Boolean) {

        val errorCodigo = EquipoFormValidator.validarCodigo(equipo.codigo)
        val errorDescripcion = EquipoFormValidator.validarDescripcion(equipo.descripcion)
        val errorEstados = EquipoFormValidator.validarEstado(equipo.estadoId)

        val primeroError = errorCodigo ?: errorDescripcion ?: errorEstados
        if (primeroError != null) {
            _guardado.value = Resource.Error(primeroError)
            return
        }
        _guardado.value = Resource.Loading

        viewModelScope.launch {
            try {
                if (esEdicion) {

                    val id = equipo.id ?: throw Exception("El equipo no tiene ID para actualizar")
                    equipoRepository.actualizar(id, equipo)

                } else {
                    equipoRepository.crear(equipo)
                }

                _guardado.value = Resource.Success(equipo)

            } catch (e: Exception) {
                _guardado.value = Resource.Error(
                    e.message ?: "Error al guardar el producto"
                )
            }
        }

    }
    fun cargarEquipo(equipoId: Long) {
        viewModelScope.launch {
            _equipoActual.value = equipoRepository.obtenerPorId(equipoId)
        }

    }
}