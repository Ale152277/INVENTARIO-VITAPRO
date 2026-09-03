package com.example.vitaequip.ui.main.fragments.equipos.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.databinding.FragmentEquipoDetailDialogBinding
import com.example.vitaequip.ui.equipos.detail.EquipoDetailViewModel
import com.example.vitaequip.ui.equipos.detail.EquipoDetalleUiModel
import com.example.vitaequip.ui.equipos.detail.EventoHistorialAdapter
import com.example.vitaequip.ui.equipos.detail.EventoHistorialUiModel
import com.example.vitaequip.util.PdfGenerator
import com.example.vitaequip.util.Resource


class EquipoDetailDialogFragment : DialogFragment() {

    private var _binding : FragmentEquipoDetailDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel : EquipoDetailViewModel by viewModels() //permite que las instancias sobrevivan a las rotaciones

    private var equipoId : Long = -1

    private var detalleCache: EquipoDetalleUiModel? = null
    private var eventosCache: List<EventoHistorialUiModel>? = null
    private var reportesCache: List<ReporteIncidencia>? = null


    companion object{
        private const val ARG_EQUIPO_ID = "arg_equipo_id"

        fun newInstance(equipoId: Long?): EquipoDetailDialogFragment{
            val fragment = EquipoDetailDialogFragment() //objeto del fragmento vacio
            val args = Bundle() //permite pasar datos entre componentes "clave , valor"
            args.putLong(ARG_EQUIPO_ID, equipoId ?: 1)
            fragment.arguments = args //el bundle se asinga aca, cuando el fragmento se destruya estos datos sobreviven
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipoId = arguments?.getLong(ARG_EQUIPO_ID)?: -1

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        _binding = FragmentEquipoDetailDialogBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1).toInt(),
            (resources.displayMetrics.heightPixels * 0.85).toInt()   // altura fija
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(equipoId == -1L){
            dismiss()
            return
        }

        binding.webViewDetalle.settings.javaScriptEnabled = false
        binding.webViewDetalle.settings.builtInZoomControls = true
        binding.webViewDetalle.settings.displayZoomControls = false
        binding.webViewDetalle.settings.setSupportZoom(true)

        binding.btnCerrar.setOnClickListener { dismiss() }

        configurarObservers()

        viewModel.cargarEquipo(equipoId)
        viewModel.cargarHistorialCombinado(equipoId)
        viewModel.cargarReportes(equipoId)


        binding.btnDescargarPdf.setOnClickListener {
            val detalle = (viewModel.equipoDetalle.value as? Resource.Success)?.data
            val eventos = (viewModel.historialCombinado.value as? Resource.Success)?.data

            if (detalle == null || eventos == null){
                Toast.makeText(
                    requireContext(),
                    "Espere a que termine de cargar el historial",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            PdfGenerator.generarPdfHistorial(requireContext(), detalle, eventos, reportesCache ?: emptyList())

        }
    }

    private fun configurarObservers(){
        viewModel.equipoDetalle.observe(viewLifecycleOwner){resource ->
            if (resource is Resource.Success){
                detalleCache = resource.data
                intentarMostrarHtml()
            }else if (resource is Resource.Error){
                Toast.makeText(requireContext(), resource.mensaje, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.historialCombinado.observe(viewLifecycleOwner){ resource ->
            when(resource){
                is Resource.Loading -> binding.progressHistorial.visibility = View.VISIBLE
                is Resource.Success ->{
                    eventosCache = resource.data
                    intentarMostrarHtml()

                }
                is Resource.Error -> {
                    binding.progressHistorial.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.mensaje, Toast.LENGTH_SHORT).show()

                }
            }

        }

        viewModel.reportes.observe(viewLifecycleOwner){resource ->
            when (resource) {
                is Resource.Success -> {
                    reportesCache = resource.data
                    intentarMostrarHtml()
                }
                is Resource.Error->{
                    reportesCache = emptyList()
                    intentarMostrarHtml()
                }

                else-> Unit


            }


        }
    }

    private fun intentarMostrarHtml(){
        val detalle = detalleCache?: return
        val eventos = eventosCache?: return
        val reportes = reportesCache?: return

        val html = PdfGenerator.construirHtml(detalle, eventos, reportes)
        binding.webViewDetalle.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        binding.progressHistorial.visibility = View.GONE
        binding.webViewDetalle.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}