package com.example.vitaequip.ui.equipos.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.vitaequip.R
import com.example.vitaequip.databinding.ActivityEquipoDetailBinding
import com.example.vitaequip.util.Constants
import com.example.vitaequip.util.PdfGenerator
import com.example.vitaequip.util.Resource



class EquipoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEquipoDetailBinding
    private val viewModel: EquipoDetailViewModel by viewModels()
    private lateinit var adapter : EventoHistorialAdapter


    private var equipoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquipoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        equipoId = intent.getLongExtra(Constants.EXTRA_EQUIPO_ID, -1)
        if(equipoId == -1L){
            finish()
            return
        }


        adapter = EventoHistorialAdapter()
        binding.rvHistorial.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.rvHistorial.adapter = adapter

        binding.btnAtras.setOnClickListener { finish() }


        configurarObservers()

        viewModel.cargarEquipo(equipoId)
        viewModel.cargarHistorialCombinado(equipoId)
        viewModel.cargarReportes(equipoId)

        binding.btnDescargarPdf.setOnClickListener {
            val detalle = (viewModel.equipoDetalle.value as? Resource.Success)?.data
            val eventos = (viewModel.historialCombinado.value as? Resource.Success)?.data
            val reportes = (viewModel.reportes.value as? Resource.Success)?.data

            if (detalle == null || eventos == null) {
                android.widget.Toast.makeText(this, "Espera a que termine de cargar el historial", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PdfGenerator.generarPdfHistorial(this, detalle, eventos, reportes ?: emptyList())
        }

    }
    private fun configurarObservers(){
        viewModel.equipo.observe(this){resource ->
            if(resource is Resource.Success){
                val equipo = resource.data
                binding.tvCodigoDetalle.text = equipo.codigo
                binding.tvDescripcionDetalle.text = equipo.descripcion
                binding.tvMarcaAreaDetalle.text = equipo.modelo ?: ""

                if (!equipo.imagenUrl.isNullOrBlank()) {
                    com.bumptech.glide.Glide.with(this)
                        .load(equipo.imagenUrl)
                        .centerCrop()
                        .into(binding.ivImagenEquipoDetalle)
                }
            }

        }
        viewModel.historialCombinado.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressHistorial.visibility = android.view.View.VISIBLE
                is Resource.Success -> {
                    binding.progressHistorial.visibility = android.view.View.GONE
                    adapter.actualizarLista(resource.data)
                }
                is Resource.Error -> {
                    binding.progressHistorial.visibility = android.view.View.GONE
                    android.widget.Toast.makeText(this, resource.mensaje, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}