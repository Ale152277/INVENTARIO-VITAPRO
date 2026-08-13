package com.example.vitaequip.ui.main.fragments.inicio

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vitaequip.R
import com.example.vitaequip.databinding.FragmentInicioLectorBinding
import com.example.vitaequip.ui.main.MainActivity
import com.example.vitaequip.ui.scanner.QrScannerActivity
import com.example.vitaequip.util.Resource


class InicioLectorFragment : Fragment() {
    private var _binding: FragmentInicioLectorBinding ?= null

    private val binding get() = _binding!!


    private val viewModel : InicioLectorViewModel by viewModels ()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioLectorBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMenu.setOnClickListener {
            (activity as? MainActivity)?.abrirDrawer()
        }

        binding.btnQrScanner.setOnClickListener {
            startActivity(Intent(requireContext(), QrScannerActivity::class.java))
        }

        viewModel.resumen.observe(viewLifecycleOwner){ resource ->
            when (resource){
                is Resource.Loading ->{
                    binding.progressInicio.visibility = View.VISIBLE
                    binding.contenidoInicio.visibility = View.GONE

                }
                is Resource.Success ->{
                    binding.progressInicio.visibility = View.GONE
                    binding.contenidoInicio.visibility = View.VISIBLE


                    val resumen = resource.data
                    binding.tvTotal.text = resumen.totalEquipos.toString()
                    binding.tvActivos.text = resumen.operativos.toString()
                    binding.tvAlertas.text = (resumen.mantenimiento + resumen.fueraDeServicio).toString()

                }

                is Resource.Error ->{
                    binding.progressInicio.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.mensaje, Toast.LENGTH_SHORT).show()
                }
            }

        }

        viewModel.cargarResumen()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}