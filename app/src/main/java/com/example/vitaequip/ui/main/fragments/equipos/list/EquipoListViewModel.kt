package com.example.vitaequip.ui.main.fragments.equipos.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch

class EquipoListViewModel : ViewModel(){

    private val repository = EquipoRepository()

    private var listaCompleta: List <Equipo> = emptyList()

    private val _equipos = MutableLiveData<Resource<List<Equipo>>>()
    val equipos: LiveData<Resource<List<Equipo>>> = _equipos

    private var textoBusqueda: String = ""
    private var marcaIdFiltro: Int? = null
    private var areaIdFiltro: Int? = null

    fun cargarEquipos(){
        _equipos.value = Resource.Loading

        viewModelScope.launch {
            try {
                listaCompleta = repository.obtenerTodos()
                aplicarFiltros()

            }catch (e: Exception){
                _equipos.value = Resource.Error(e.message?: "Error al cargar equipos")
            }
        }
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
        _equipos.value = Resource.Success(filtrados)
    }
}