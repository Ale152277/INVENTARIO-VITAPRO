package com.example.vitaequip.ui.main.fragments.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.data.repository.EstadoRepository
import com.example.vitaequip.util.Resource
import kotlinx.coroutines.launch
import java.time.LocalDate


class DashboardViewModel: ViewModel(){
    private val equipoRepository = EquipoRepository()
    private val estadoRepository = EstadoRepository()

    private val _resumen = MutableLiveData<Resource<DashboardResumen>>()
    val resumen: LiveData<Resource<DashboardResumen>> = _resumen

    fun cargarResumen(){
        _resumen.value = Resource.Loading

        viewModelScope.launch {
            try {
                val equipos = equipoRepository.obtenerTodos()
                val estados = estadoRepository.obtenerTodas()

                val idEstadoOperativo = estados.find {
                    it.nombre.equals("Operativo", ignoreCase = true)
                }?.id

                val idEstadoMantenimiento = estados.find{
                    it.nombre.equals("Mantenimiento", ignoreCase = true)
                }?.id

                val idEstadoFueraServicio = estados.find {
                    it.nombre.equals("Fuera de servicio", ignoreCase = true)
                }?.id

                val operativos = equipos.count{
                    it.estadoId == idEstadoOperativo
                }

                val mantenimiento = equipos.count{
                    it.estadoId == idEstadoMantenimiento
                }

                val fueraDeServicio = equipos.count{
                    it.estadoId == idEstadoFueraServicio
                }

                val total = equipos.size

                val porcentaje = if (total >0) {
                    (operativos * 100) / total
                }  else 0


                val hoy = LocalDate.now()

                val proximos = equipos
                    .filter { it.fechaProximoMantenimiento != null }
                    .filter {
                        val fecha = LocalDate.parse(it.fechaProximoMantenimiento)
                        val diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, fecha)
                        diasRestantes in 0..14
                    }
                    .sortedBy { it.fechaProximoMantenimiento }

                _resumen.value = Resource.Success(
                    DashboardResumen(
                        totalEquipos = total,
                        operativos = operativos,
                        porcentajeOperativos = porcentaje,
                        mantenimiento= mantenimiento,
                        fueraDeServicio = fueraDeServicio,
                        proximosMantenimientos = proximos
                    )
                )
            }catch (e: Exception){
                _resumen.value = Resource.Error(e.message ?: "Error al cargar el dashboard")
            }
        }

    }
}