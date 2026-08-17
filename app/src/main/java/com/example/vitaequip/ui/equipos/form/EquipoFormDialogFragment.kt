package com.example.vitaequip.ui.equipos.form

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.vitaequip.R
import com.example.vitaequip.data.model.*
import com.example.vitaequip.databinding.FragmentEquipoFormDialogBinding
import com.example.vitaequip.util.Constants
import com.example.vitaequip.util.Resource
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class EquipoFormDialogFragment : DialogFragment() {

    private var _binding : FragmentEquipoFormDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EquipoFormViewModel by viewModels()

    private var listaMarcas: List<Marcas> = emptyList()
    private var listaPisos: List<Piso> = emptyList()
    private var listaAreas: List<Area> = emptyList()
    private var listaEstados: List<Estados> = emptyList()

    private var equipoId : Long? = null
    private var esEdicion = false

    private var equipoCargado: Equipo? = null

    private val formatoDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val formatoSupabase = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var imagenUriSeleccionada: android.net.Uri? = null
    private var imagenUrlSubida: String? = null

    private val pickMedia = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imagenUriSeleccionada = uri
            binding.ivImagenEquipo.setImageURI(uri) // vista previa inmediata, local
            subirImagen(uri)
        }
    }
    companion object{

        private const val ARG_EQUIPO_ID = "arg_equipo_id"

        fun newInstance(equipoId: Long?): EquipoFormDialogFragment{
            val fragment = EquipoFormDialogFragment()
            val args = Bundle()
            equipoId?.let{args.putLong(ARG_EQUIPO_ID, it)}
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        equipoId = arguments?.let {
            if (it.containsKey(ARG_EQUIPO_ID)) it.getLong(ARG_EQUIPO_ID) else null

        }
        esEdicion = equipoId != null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipoFormDialogBinding.inflate(inflater, container,false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        //se ajusta el ancho del dialogo para que se vea como tarjeta
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTituloFormulario.text = getString(
            if(esEdicion) R.string.editar_equipo else R.string.registrar_equipo_titulo
        )
        binding.btnCerrar.setOnClickListener { dismiss() }

        binding.btnSeleccionarImagen.setOnClickListener {
            pickMedia.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        configurarSpinnerPotenciaUnidad()
        configurarDatePickers()
        configurarObservers()
        configurarBotonGuardar()

        viewModel.cargarCatalogos()
        equipoId?.let { viewModel.cargarEquipo(it) }

    }

    private fun configurarSpinnerPotenciaUnidad(){
        val unidades = resources.getStringArray(R.array.unidades_potencia)
        binding.spinnerPotenciaUnidad.adapter= ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, unidades
        )


    }

    private fun configurarDatePickers(){
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.etFechaUltimoMantenimiento.setOnClickListener {
            mostrarDatePicker(binding.etFechaUltimoMantenimiento.text.toString(), formato ){
                    fecha ->
                binding.etFechaUltimoMantenimiento.setText(fecha)

            }
        }

        binding.etFechaProximoMantenimiento.setOnClickListener {
            mostrarDatePicker(binding.etFechaProximoMantenimiento.text.toString(), formato){
                    fecha ->
                binding.etFechaProximoMantenimiento.setText(fecha)
            }
        }
    }


    private fun aFormatoSupabase(fechaDisplay: String): String? {
        if (fechaDisplay.isBlank()) return null
        return runCatching { formatoDisplay.parse(fechaDisplay)?.let { formatoSupabase.format(it) } }.getOrNull()
    }

    private fun aFormatoDisplay(fechaSupabase: String?): String {
        if (fechaSupabase.isNullOrBlank()) return ""
        return runCatching { formatoSupabase.parse(fechaSupabase)?.let { formatoDisplay.format(it) } }.getOrNull() ?: fechaSupabase
    }

    private fun mostrarDatePicker(
        valorActual: String,
        formato: SimpleDateFormat,
        onFechaElegida: (String) -> Unit
    ){
        val calendar = Calendar.getInstance()
        //si ya hay fecha, el picker se posiciona en esa misma fecha
        runCatching { formato.parse(valorActual)?.let{calendar.time = it} }

        DatePickerDialog(
            requireContext(),
            {_, year, month, day ->
                calendar.set(year, month, day)
                onFechaElegida(formato.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()

    }


    private fun configurarObservers(){

        viewModel.marcas.observe(viewLifecycleOwner){marcas ->
            listaMarcas = marcas
            binding.spinnerMarca.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, marcas.map{it.nombre}
            )
            seleccionarEnSpinner(binding.spinnerMarca, marcas, equipoCargado?.marcaId)
        }

        viewModel.pisos.observe(viewLifecycleOwner){pisos->
            listaPisos = pisos


            binding.spinnerPiso.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, pisos.map { it.nombre }

            )

            binding.spinnerPiso.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    pisos.getOrNull(pos)?.let{viewModel.cargarAreasPorPiso(it.id)}
                }

                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }

        viewModel.areas.observe(viewLifecycleOwner) { areas ->
            listaAreas = areas

            binding.spinnerArea.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, areas.map { it.nombre }
            )
            seleccionarEnSpinner(binding.spinnerArea, areas, equipoCargado?.areaId)
        }

        viewModel.estados.observe(viewLifecycleOwner){estados->

            listaEstados = estados
            binding.spinnerEstado.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, estados.map{it.nombre}
            )

            seleccionarEnSpinner(binding.spinnerEstado, estados, equipoCargado?.estadoId)

        }

        viewModel.equipoActual.observe(viewLifecycleOwner){equipo ->
            equipoCargado = equipo
            equipo?.let{prellenarFormulario(it)}
        }

        viewModel.guardado.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Success -> {
                    setFragmentResult(Constants.RESULT_EQUIPO_GUARDADO, Bundle())
                    dismiss()


                }
                is Resource.Error -> Toast.makeText(
                    requireContext(), resource.mensaje, Toast.LENGTH_SHORT
                ).show()
                else -> Unit
            }
        }

    }

    private fun <T> seleccionarEnSpinner(spinner: Spinner, lista: List<T>, idGuardado: Int?){
        if(idGuardado == null) return
        val posicion = lista.indexOfFirst { item ->
            when(item){
                is Marcas -> item.id == idGuardado
                is Piso -> item.id == idGuardado
                is Area -> item.id == idGuardado
                is Estados -> item.id == idGuardado
                else -> false
            }
        }
        if (posicion >= 0) spinner.setSelection(posicion)
    }

    private fun prellenarFormulario(equipo: Equipo) {
        binding.etCodigo.setText(equipo.codigo)
        binding.etDescripcion.setText(equipo.descripcion)
        binding.etModelo.setText(equipo.modelo)
        binding.etPotenciaValor.setText(equipo.potenciaValor?.toString() ?: "")
        binding.etVoltajeBateria.setText(equipo.bateriaVoltaje?.toString() ?: "")
        binding.etAmperaje.setText(equipo.bateriaAmperaje?.toString() ?: "")
        binding.etNumeroBaterias.setText(equipo.bateriaCantidad?.toString() ?: "")
        binding.etUbicacionEspecifica.setText(equipo.ubicacionEspecifica)
        binding.etFechaUltimoMantenimiento.setText(aFormatoDisplay(equipo.fechaUltimoMantenimiento))
        binding.etFechaProximoMantenimiento.setText(aFormatoDisplay(equipo.fechaProximoMantenimiento))
        binding.etSerie.setText(equipo.serie ?: "")
        binding.etInventario.setText(equipo.inventario ?: "")
        if (!equipo.imagenUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(equipo.imagenUrl)
                .centerCrop()
                .into(binding.ivImagenEquipo)
        }

        val unidades = resources.getStringArray(R.array.unidades_potencia)
        val posUnidad = unidades.indexOf(equipo.potenciaUnidad)
        if (posUnidad >= 0) binding.spinnerPotenciaUnidad.setSelection(posUnidad)

        // Marca/piso/área/estado se seleccionan en sus propios observers de arriba:
        // si equipoCargado llega ANTES que el catálogo, seleccionarEnSpinner ya se disparó
        // desde acá con la lista todavía vacía; por eso también se vuelve a intentar
        // dentro de cada observer de catálogo, usando equipoCargado ya asignado.
        seleccionarEnSpinner(binding.spinnerMarca, listaMarcas, equipo.marcaId)
        seleccionarEnSpinner(binding.spinnerArea, listaAreas, equipo.areaId)
        seleccionarEnSpinner(binding.spinnerEstado, listaEstados, equipo.estadoId)
    }

    private fun configurarBotonGuardar() {
        binding.btnGuardar.setOnClickListener {
            val marcaSeleccionada = listaMarcas.getOrNull(binding.spinnerMarca.selectedItemPosition)
            val areaSeleccionada = listaAreas.getOrNull(binding.spinnerArea.selectedItemPosition)
            val estadoSeleccionado = listaEstados.getOrNull(binding.spinnerEstado.selectedItemPosition)

            android.util.Log.d(
                "DEBUG_ESTADO",
                "posicion=${binding.spinnerEstado.selectedItemPosition} " +
                        "tamañoLista=${listaEstados.size} " +
                        "estadoSeleccionado=$estadoSeleccionado"
            )

            val unidadSeleccionada = resources.getStringArray(R.array.unidades_potencia)
                .getOrNull(binding.spinnerPotenciaUnidad.selectedItemPosition)

            // Partimos del equipo ya cargado (edición) para no perder campos que el
            // formulario no muestra (serie, observaciones, imagenUrl, qrUrl, etc.)
            val base = equipoCargado ?: Equipo(
                id = null, descripcion = "", codigo = "",
                creadoEn = null, actualizadoEn = null
            )

            val equipo = base.copy(
                codigo = binding.etCodigo.text.toString().trim(),
                descripcion = binding.etDescripcion.text.toString().trim(),
                modelo = binding.etModelo.text.toString().trim().ifBlank { null },
                marcaId = marcaSeleccionada?.id,
                potenciaValor = binding.etPotenciaValor.text.toString().toDoubleOrNull(),
                potenciaUnidad = unidadSeleccionada,
                bateriaVoltaje = binding.etVoltajeBateria.text.toString().toDoubleOrNull(),
                bateriaAmperaje = binding.etAmperaje.text.toString().toDoubleOrNull(),
                bateriaCantidad = binding.etNumeroBaterias.text.toString().toIntOrNull(),
                areaId = areaSeleccionada?.id,
                ubicacionEspecifica = binding.etUbicacionEspecifica.text.toString().trim().ifBlank { null },
                estadoId = estadoSeleccionado?.id,
                fechaUltimoMantenimiento = aFormatoSupabase(binding.etFechaUltimoMantenimiento.text.toString()),
                fechaProximoMantenimiento = aFormatoSupabase(binding.etFechaProximoMantenimiento.text.toString()),
                serie = binding.etSerie.text.toString().trim().ifBlank { null },
                inventario = binding.etInventario.text.toString().ifBlank { null } ,
                imagenUrl = imagenUrlSubida ?: equipoCargado?.imagenUrl,
            )

            viewModel.guardarEquipo(equipo, esEdicion)
        }
    }

    private fun subirImagen(uri: android.net.Uri){
        lifecycleScope.launch {
            try {
                val bytes = requireContext().contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: return@launch

                val nombreArchivo = "equipo_${System.currentTimeMillis()}.jpg"

                val bucket = com.example.vitaequip.data.remote.SupabaseClient.client.storage.from("imagenes-equipos")
                bucket.upload(nombreArchivo, bytes) { upsert = false }

                imagenUrlSubida = bucket.publicUrl(nombreArchivo)
            }catch(e: Exception){
                Toast.makeText(
                    requireContext(),
                    "No se pudo subir la imagen ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // evita memory leak, osea evita que las vistas sigan activas cuando no estemos interactuando
    }
}



