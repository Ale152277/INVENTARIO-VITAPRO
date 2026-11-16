package com.example.vitaequip.ui.equipos.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.example.vitaequip.data.model.*
import com.example.vitaequip.data.remote.SupabaseClient
import com.example.vitaequip.data.repository.*
import com.example.vitaequip.util.QrGenerator
import com.example.vitaequip.util.Resource
import com.example.vitaequip.util.SessionManager
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
class EquipoFormViewModel : ViewModel() {
    private val equipoRepository = EquipoRepository()
    private val pisoRepository = PisoRepository()
    private val areaRepository = AreaRepository()
    private val marcaRepository = MarcaRepository()
    private val estadoRepository = EstadoRepository()
    private val empresaRepository = EmpresaRepository()
    private val historialEquipoRepository = EquipoHistorialEstadoRepository()
    private val historailUbicacionRepository = EquipoHisUbiRepository()
    private val bancoBateriaRepository = EquipoBancoBateriaRepository()

    private val _pisos = MutableLiveData<List<Piso>>()
    val pisos: LiveData<List<Piso>> = _pisos

    private val _areas = MutableLiveData<List<Area>>()
    val areas: LiveData<List<Area>> = _areas

    private val _estados = MutableLiveData<List<Estados>>()
    val estados: LiveData<List<Estados>> = _estados

    private val _empresas = MutableLiveData<List<Empresa>>()
    val empresas: LiveData<List<Empresa>> = _empresas

    private val _marcas = MutableLiveData<List<Marcas>>()
    val marcas: LiveData<List<Marcas>> = _marcas

    private val _guardado = MutableLiveData<Resource<Equipo>>()
    val guardado: LiveData<Resource<Equipo>> = _guardado

    private val _equipoActual = MutableLiveData<Equipo?>()
    val equipoActual: LiveData<Equipo?> = _equipoActual

    private val _cadenaUbicacionEdicion = MutableLiveData<CadenaUbicacion?>()
    val cadenaUbicacionEdicion: LiveData<CadenaUbicacion?> = _cadenaUbicacionEdicion

    private val _bancoBateria = MutableLiveData<List<EquipoBancoBateria>>()
    val bancoBateria : LiveData<List<EquipoBancoBateria>> = _bancoBateria


    private suspend fun registrarHistorialSiCambio(anterior: Equipo?, actual : Equipo){
        val equipoId = actual.id ?: return

        if (anterior == null) return

        val usuarioId = SessionManager.usuarioActual?.id

        if(anterior.estadoId != actual.estadoId && actual.estadoId != null){
            historialEquipoRepository.registrar(
                EquipoHistorialEstado(
                    equipoId = equipoId,
                    estadoAnteriorId = anterior.estadoId,
                    estadoNuevoId = actual.estadoId,
                    modificadoPor = usuarioId
                )
            )
        }

        if (anterior.areaId != actual.areaId && actual.areaId != null){
            val ahora = java.time.LocalDateTime.now().toString()
            historailUbicacionRepository.cerrarRegistroVigente(equipoId, ahora)
            historailUbicacionRepository.crear(
                EquipoHistorialUbicacionInsert(equipoId, actual.areaId, usuarioId)
            )
        }




    }
    fun cargarCatalogos() {
        viewModelScope.launch {
            _estados.value = estadoRepository.obtenerTodas()
            _marcas.value = marcaRepository.obtenerTodas()
            _empresas.value = empresaRepository.obtenerTodas()
        }

    }

    fun cargarPisosPorEmpresa(empresaId: Int) {
        viewModelScope.launch {
            _pisos.value = pisoRepository.obtenerPorEmpresa(empresaId)
        }
    }

    fun cargarAreasPorPiso(pisoId: Int) {
        viewModelScope.launch {
            _areas.value = areaRepository.obtenerPorPiso(pisoId)
        }

    }

    fun guardarEquipo(equipo: Equipo, esEdicion: Boolean, bancos: List<EquipoBancoBateria>) {

        val errorCodigo = EquipoFormValidator.validarCodigo(equipo.codigo)
        val errorDescripcion = EquipoFormValidator.validarDescripcion(equipo.descripcion)
        val errorEstados = EquipoFormValidator.validarEstado(equipo.estadoId)

        val primeroError = errorCodigo ?: errorDescripcion ?: errorEstados
        if (primeroError != null) {
            _guardado.value = Resource.Error(primeroError)
            return
        }

        val equipoAnterior = _equipoActual.value
        _guardado.value = Resource.Loading

        viewModelScope.launch {
            try {

                val equipoFinal: Equipo

                if (esEdicion) {

                    val id = equipo.id ?: throw Exception("El equipo no tiene ID para actualizar")
                    equipoRepository.actualizar(id, equipo)
                    equipoFinal = equipo

                } else {

                    val creado = equipoRepository.crear(equipo)
                    val id = creado.id ?: throw Exception("Supabase no devolvió el ID del equipo creado")

                    val bytesQr = QrGenerator.generarBytesPng(id.toString())


                    val bucketQr = SupabaseClient.client.storage.from("qr-equipos")
                    val nombreArchivoQr = "equipo_$id.png"
                    bucketQr.upload(nombreArchivoQr, bytesQr) { upsert = false }
                    val urlQr = bucketQr.publicUrl(nombreArchivoQr)


                    val equipoConQr = creado.copy(qrUrl = urlQr)
                    equipoRepository.actualizar(id, equipoConQr)
                    equipoFinal = equipoConQr
                }

                val idEquipo = equipoFinal.id
                if (idEquipo != null) {
                    val bancosConId = bancos.map { it.copy(equipoId = idEquipo) }
                    bancoBateriaRepository.reemplazarBancos(idEquipo, bancosConId)
                }
                registrarHistorialSiCambio(equipoAnterior, equipoFinal)

                _guardado.value = Resource.Success(equipoFinal)

            } catch (e: Exception) {
                android.util.Log.e("GUARDAR_EQUIPO", "Error al guardar", e)
                _guardado.value = Resource.Error(e.message ?: "Error al guardar el producto")

            }
        }

    }
    fun cargarEquipo(equipoId: Long) {
        viewModelScope.launch {
            val equipo = equipoRepository.obtenerPorId(equipoId)
            _equipoActual.value = equipo

            val areaId = equipo?.areaId
            if(areaId != null){
                val areaDelEquipo = areaRepository.obtenerTodas().find{it.id == areaId}
                val pisoDelEquipo = areaDelEquipo?.let {area ->
                    pisoRepository.obtenerTodos().find { it.id == area.pisoId }
                }
                if(areaDelEquipo != null && pisoDelEquipo != null){
                    _cadenaUbicacionEdicion.value = CadenaUbicacion(
                        empresaId = pisoDelEquipo.empresaId,
                        pisoId = pisoDelEquipo.id,
                        areaId = areaDelEquipo.id

                    )
                }
            }

        }

    }

    fun cargarBancosBateria(equipoId: Long){
        viewModelScope.launch {
            _bancoBateria.value = bancoBateriaRepository.obtenerPorEquipo(equipoId)
        }
    }
}