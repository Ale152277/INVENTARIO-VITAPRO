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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.vitaequip.R
import com.example.vitaequip.data.model.*
import com.example.vitaequip.databinding.FragmentEquipoFormDialogBinding
import com.example.vitaequip.util.Constants
import com.example.vitaequip.util.Resource
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
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
    private var listaEmpresas: List<Empresa> = emptyList()
    private var empresaIdAPreseleccionar: Int? = null
    private var pisoIdAPreseleccionar: Int? = null
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
        configurarSeccionBancoBateria()
        equipoId?.let {
            viewModel.cargarEquipo(it)
            viewModel.cargarBancosBateria(it)
        }
        viewModel.cargarCatalogos()
    }
    private fun configurarSpinnerPotenciaUnidad(){
        val unidades = resources.getStringArray(R.array.unidades_potencia)
        binding.spinnerPotenciaUnidad.adapter= ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, unidades
        )
    }
    private fun configurarSeccionBancoBateria(){
        binding.banco1.tvTituloBanco.text = "Banco 1"
        binding.banco2.tvTituloBanco.text = "Banco 2"
        binding.banco3.tvTituloBanco.text = "Banco 3"

        binding.spinnerCantidadBancos.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, listOf("1","2", "3")
        )

        binding.rgIncluyeBanco.setOnCheckedChangeListener { _, checkedId ->
            val incluye = checkedId == binding.rbBancoSi.id
            binding.contenedorCantidadBanco.visibility = if(incluye) View.VISIBLE else View.GONE
            if(incluye){
                actualizarBancosVisibles(binding.spinnerCantidadBancos.selectedItemPosition + 1)

            }else{
                actualizarBancosVisibles(0)
            }

            binding.spinnerCantidadBancos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    actualizarBancosVisibles (pos + 1)
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }


    }

    private fun actualizarBancosVisibles(cantidad: Int) {
        binding.banco1.root.visibility = if (cantidad >= 1) View.VISIBLE else View.GONE
        binding.banco2.root.visibility = if (cantidad >= 2) View.VISIBLE else View.GONE
        binding.banco3.root.visibility = if (cantidad >= 3) View.VISIBLE else View.GONE
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
    private fun configurarObservers() {

        // 1. Ubicación previa en edición
        viewModel.cadenaUbicacionEdicion.observe(viewLifecycleOwner) { cadena ->
            cadena?.let {
                empresaIdAPreseleccionar = it.empresaId
                pisoIdAPreseleccionar = it.pisoId
                seleccionarEnSpinner(binding.spinnerEmpresa, listaEmpresas, it.empresaId)
            }
        }
        // 2. Marcas
        viewModel.marcas.observe(viewLifecycleOwner) { marcas ->
            listaMarcas = marcas
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, marcas.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMarca.adapter = adapter
            seleccionarEnSpinner(binding.spinnerMarca, marcas, equipoCargado?.marcaId)
        }
        // 3. Empresas
        viewModel.empresas.observe(viewLifecycleOwner) { empresas ->
            listaEmpresas = empresas
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, empresas.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerEmpresa.adapter = adapter

            // Limpiar listener para evitar disparos accidentales
            binding.spinnerEmpresa.onItemSelectedListener = null

            if (empresaIdAPreseleccionar != null) {
                seleccionarEnSpinner(binding.spinnerEmpresa, empresas, empresaIdAPreseleccionar)
            } else if (empresas.isNotEmpty()) {
                binding.spinnerEmpresa.setSelection(0, false)
            }

            // asignar listener despues de establecer la selección inicial
            binding.spinnerEmpresa.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    empresas.getOrNull(pos)?.let { viewModel.cargarPisosPorEmpresa(it.id) }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

            // disparar carga de pisos manualmente para la posición actual
            val posicion = binding.spinnerEmpresa.selectedItemPosition
            if (posicion in empresas.indices) {
                viewModel.cargarPisosPorEmpresa(empresas[posicion].id)
            }
        }
        // 4. Pisos
        viewModel.pisos.observe(viewLifecycleOwner) { pisos ->
            listaPisos = pisos
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pisos.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPiso.adapter = adapter

            binding.spinnerPiso.onItemSelectedListener = null
            if (pisoIdAPreseleccionar != null) {
                seleccionarEnSpinner(binding.spinnerPiso, pisos, pisoIdAPreseleccionar)
                pisoIdAPreseleccionar = null // Resetear para no bloquear selecciones manuales futuras
            } else if (pisos.isNotEmpty()) {
                binding.spinnerPiso.setSelection(0, false)
            }

            binding.spinnerPiso.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    pisos.getOrNull(pos)?.let { viewModel.cargarAreasPorPiso(it.id) }
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }

            val posicion = binding.spinnerPiso.selectedItemPosition
            if (posicion in pisos.indices) {
                viewModel.cargarAreasPorPiso(pisos[posicion].id)
            } else {
                listaAreas = emptyList()
                binding.spinnerArea.adapter = null
            }
        }

        // 5. Áreas
        viewModel.areas.observe(viewLifecycleOwner) { areas ->
            listaAreas = areas
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, areas.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerArea.adapter = adapter
            seleccionarEnSpinner(binding.spinnerArea, areas, equipoCargado?.areaId)
        }

        // 6. Estados
        viewModel.estados.observe(viewLifecycleOwner) { estados ->
            listaEstados = estados
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerEstado.adapter = adapter
            seleccionarEnSpinner(binding.spinnerEstado, estados, equipoCargado?.estadoId)
        }

        // 7. Equipo Actual
        viewModel.equipoActual.observe(viewLifecycleOwner) { equipo ->
            equipoCargado = equipo
            equipo?.let { prellenarFormulario(it) }
        }

        // 8. Resultado Guardado
        viewModel.guardado.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    setFragmentResult(Constants.RESULT_EQUIPO_GUARDADO, Bundle())
                    dismiss()
                }
                is Resource.Error -> Toast.makeText(requireContext(), resource.mensaje, Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }

        viewModel.bancoBateria.observe(viewLifecycleOwner){bancos ->
            prellenarBancos(bancos)}
    }

    private fun <T> seleccionarEnSpinner(spinner: Spinner, lista: List<T>, idGuardado: Int?){
        if(idGuardado == null) return
        val posicion = lista.indexOfFirst { item ->
            when(item){
                is Empresa -> item.id == idGuardado
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


        seleccionarEnSpinner(binding.spinnerMarca, listaMarcas, equipo.marcaId)
        seleccionarEnSpinner(binding.spinnerArea, listaAreas, equipo.areaId)
        seleccionarEnSpinner(binding.spinnerEstado, listaEstados, equipo.estadoId)
    }

    private fun prellenarBancos(bancos: List<EquipoBancoBateria>){
        if(bancos.isEmpty()){
            binding.rgIncluyeBanco.check(binding.rbBancoNo.id)
            return
        }

        binding.rgIncluyeBanco.check(binding.rbBancoSi.id)
        binding.spinnerCantidadBancos.setSelection(bancos.size - 1)
        actualizarBancosVisibles(bancos.size)

        val bloques = listOf(
            binding.banco1,
            binding.banco2,
            binding.banco3
        )
        bancos.sortedBy { it.numeroBanco }.forEachIndexed { index, banco ->
            bloques[index].etVoltajeBanco.setText(banco.bateriaVoltaje?.toString() ?: "")
            bloques[index].etAmperajeBanco.setText(banco.bateriaAmperaje?.toString() ?: "")
            bloques[index].etCantidadBanco.setText(banco.bateriaCantidad?.toString() ?: "")

        }
    }

    private fun configurarBotonGuardar() {
        binding.btnGuardar.setOnClickListener {
            val marcaSeleccionada = listaMarcas.getOrNull(binding.spinnerMarca.selectedItemPosition)
            val areaSeleccionada = listaAreas.getOrNull(binding.spinnerArea.selectedItemPosition)
            val estadoSeleccionado = listaEstados.getOrNull(binding.spinnerEstado.selectedItemPosition)
            val incluyeBanco = binding.rgIncluyeBanco.checkedRadioButtonId == binding.rbBancoSi.id
            val bancos = leerBancosDesdeFormulario()


            val unidadSeleccionada = resources.getStringArray(R.array.unidades_potencia)
                .getOrNull(binding.spinnerPotenciaUnidad.selectedItemPosition)


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
                incluyeBancoBaterias = incluyeBanco,

            )

            viewModel.guardarEquipo(equipo, esEdicion, bancos)
        }
    }

    private fun leerBancosDesdeFormulario(): List<EquipoBancoBateria>{
        if(binding.rgIncluyeBanco.checkedRadioButtonId != binding.rbBancoSi.id){
            return emptyList()
        }
        val cantidad = binding.spinnerCantidadBancos.selectedItemPosition + 1
        val bloques = listOf(binding.banco1, binding.banco2, binding.banco3).take(cantidad)

        return bloques.mapIndexed { index, bloque ->
            EquipoBancoBateria(
                equipoId = 0L, // se reemplaza en el viewmodel una vez que se conoce el id real del equipo
                numeroBanco = index + 1,
                bateriaVoltaje = bloque.etVoltajeBanco.text.toString().toDoubleOrNull(),
                bateriaAmperaje = bloque.etAmperajeBanco.text.toString().toDoubleOrNull(),
                bateriaCantidad = bloque.etCantidadBanco.text.toString().toIntOrNull()
            )
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



