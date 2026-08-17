package com.example.vitaequip.ui.main.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vitaequip.databinding.FragmentDashboardBinding
import com.example.vitaequip.ui.equipos.form.EquipoFormActivity
import com.example.vitaequip.ui.equipos.form.EquipoFormDialogFragment
import com.example.vitaequip.ui.main.MainActivity
import com.example.vitaequip.ui.main.fragments.equipos.list.EquipoListFragment
import com.example.vitaequip.util.Resource
import com.example.vitaequip.util.SessionManager

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var proximosAdapter: ProximoMantenimientoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMenu.setOnClickListener {
            (activity as? MainActivity)?.abrirDrawer()
        }

        proximosAdapter = ProximoMantenimientoAdapter()
        binding.rvProximosMantenimientos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProximosMantenimientos.adapter = proximosAdapter

        binding.btnRegistrarEquipo.visibility =
            if (SessionManager.esAdmin()) View.VISIBLE else View.GONE

        binding.accionesRapidas.visibility =
            if(!SessionManager.esAdmin()) View.GONE else View.VISIBLE

        binding.btnRegistrarEquipo.setOnClickListener {
            EquipoFormDialogFragment.newInstance(equipoId = null)
                .show(childFragmentManager, "equipo_form")
        }

        viewModel.resumen.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressDashboard.visibility = View.VISIBLE
                    binding.contenidoDashboard.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressDashboard.visibility = View.GONE
                    binding.contenidoDashboard.visibility = View.VISIBLE

                    val resumen = resource.data
                    binding.tvTotalEquipos.text = resumen.totalEquipos.toString()
                    binding.tvOperativos.text = resumen.operativos.toString()
                    binding.tvPorcentajeOperativos.text =
                        getString(com.example.vitaequip.R.string.operativos_desc, resumen.porcentajeOperativos)
                    binding.tvMantenimiento.text = resumen.mantenimiento.toString()
                    binding.tvFueraServicio.text = resumen.fueraDeServicio.toString()

                    proximosAdapter.actualizarLista(resumen.proximosMantenimientos)

                    val total = resumen.totalEquipos
                    binding.tvConteoOperativo.text = resumen.operativos.toString()
                    binding.tvConteoMantenimiento.text = resumen.mantenimiento.toString()
                    binding.tvConteoFueraServicio.text = resumen.fueraDeServicio.toString()

                    binding.pbOperativo.progress = if (total > 0) (resumen.operativos * 100) / total else 0
                    binding.pbMantenimiento.progress = if (total > 0) (resumen.mantenimiento * 100) / total else 0
                    binding.pbFueraServicio.progress = if (total > 0) (resumen.fueraDeServicio * 100) / total else 0
                }
                is Resource.Error -> {
                    binding.progressDashboard.visibility = View.GONE
                    android.widget.Toast.makeText(requireContext(), resource.mensaje, android.widget.Toast.LENGTH_SHORT).show()
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