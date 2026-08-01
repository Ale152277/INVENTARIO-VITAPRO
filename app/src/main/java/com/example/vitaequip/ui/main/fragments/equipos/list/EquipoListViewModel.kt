package com.example.vitaequip.ui.main.fragments.equipos.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.model.Marcas
import com.example.vitaequip.data.model.Area
import com.example.vitaequip.data.repository.AreaRepository
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.data.repository.EstadoRepository
import com.example.vitaequip.data.repository.MarcaRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch

class EquipoListViewModel : ViewModel(){

    private val equipoRepository = EquipoRepository()
    private val marcaRepository = MarcaRepository()
    private val estadoRepository = EstadoRepository()
    private val areaRepository = AreaRepository()
    private var listaCompleta: List <Equipo> = emptyList()
    private var marcas: List<Marcas> =emptyList()
    private var areas: List<Area> = emptyList()
    private var estadosMap: Map<Int, String> = emptyMap()
    private val _equipos = MutableLiveData<Resource<List<EquipoUiModel>>>()
    val equipos: LiveData<Resource<List<EquipoUiModel>>> = _equipos

    private val _marcasFiltro = MutableLiveData<List<Marcas>>()
    val marcasFiltro : LiveData<List<Marcas>> = _marcasFiltro

    private val _areasFiltro = MutableLiveData<List<Area>>()
    val areasFiltro : LiveData<List<Area>> = _areasFiltro

    private var textoBusqueda: String = ""
    private var marcaIdFiltro: Int? = null
    private var areaIdFiltro: Int? = null

    fun cargarEquipos(){
        _equipos.value = Resource.Loading

        viewModelScope.launch {
            try {
                listaCompleta = equipoRepository.obtenerTodos()
                marcas = marcaRepository.obtenerTodas()
                areas = areaRepository.obtenerTodas()
                estadosMap = estadoRepository.obtenerTodas().associate { it.id to it.nombre }

                _marcasFiltro.value = marcas
                _areasFiltro.value = areas

                aplicarFiltros()

            }catch (e: Exception){
                _equipos.value = Resource.Error(e.message?: "Error al cargar equipos")
            }
        }
    }

    fun buscar (texto: String){
        textoBusqueda = texto
        aplicarFiltros()
    }

    fun filtrarPorMarca (marcaId: Int?){
        marcaIdFiltro = marcaId
        aplicarFiltros()
    }

    fun filtrarPorArea(areaId: Int?){
        areaIdFiltro = areaId
        aplicarFiltros()
    }

    private fun aplicarFiltros(){
        val filtrados = listaCompleta.filter { equipo->
            val coincideTexto = textoBusqueda.isBlank() ||
                    equipo.descripcion.contains(textoBusqueda, ignoreCase = true) ||
                    equipo.codigo.contains(textoBusqueda, ignoreCase = true)

            val coincideMarca = marcaIdFiltro == null || equipo.marcaId == marcaIdFiltro
            val coincideArea = areaIdFiltro == null || equipo.areaId == areaIdFiltro

            coincideTexto && coincideArea && coincideMarca
        }

        val uiModels = filtrados.map { equipo->
            EquipoUiModel(
                id = equipo.id,
                codigo = equipo.codigo,
                descripcion = equipo.descripcion,
                marcaNombre = marcas.find { it.id == equipo.marcaId }?.nombre ?: "-",
                areaNombre = areas.find { it.id == equipo.areaId }?.nombre ?:"-",
                estadoNombre= estadosMap[equipo.estadoId] ?:"-",
                imagenUrl = equipo.imagenUrl
            )
        }

        _equipos.value = Resource.Success(uiModels)
    }
}