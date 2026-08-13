package com.example.vitaequip.ui.main.fragments.equipos.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vitaequip.R
import com.example.vitaequip.databinding.FragmentEquipoListBinding
import com.example.vitaequip.ui.equipos.detail.EquipoDetailActivity
import com.example.vitaequip.ui.equipos.form.EquipoFormActivity
import com.example.vitaequip.util.Constants
import com.example.vitaequip.util.Resource
import androidx.core.widget.addTextChangedListener
import com.example.vitaequip.ui.equipos.form.EquipoFormDialogFragment
import com.example.vitaequip.util.SessionManager

class EquipoListFragment : Fragment() {

    private var _binding : FragmentEquipoListBinding? = null
    private val binding get() = _binding!!



    private val viewModel : EquipoListViewModel by viewModels()//permite que la instancia del viewmodel sobreviva a rotaciones
    private lateinit var adapter: EquipoListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipoListBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EquipoListAdapter(
            onEditarClick = { equipo ->
                EquipoFormDialogFragment.newInstance(equipoId = equipo.id)
                    .show(childFragmentManager, "equipo_form")

            },

            onHistorialClick = {equipo ->
                val intent = android.content.Intent(requireContext(), EquipoDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_EQUIPO_ID, equipo.id)
                startActivity(intent)

            }
        )
        childFragmentManager.setFragmentResultListener(Constants.RESULT_EQUIPO_GUARDADO, viewLifecycleOwner) { _, _ ->
            viewModel.cargarEquipos()
        }




        binding.rvEquipos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEquipos.adapter = adapter

        binding.etBuscar.addTextChangedListener { texto ->
            viewModel.buscar(texto?.toString() ?: "")
        }

        binding.btnNuevo.setOnClickListener {
            EquipoFormDialogFragment.newInstance(equipoId = null)
                .show(childFragmentManager,"equipo_form")

        }

        binding.btnRegresar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnNuevo.visibility =
            if(SessionManager.esAdmin()){
                View.VISIBLE
            }else{
                View.GONE
            }


        viewModel.marcasFiltro.observe(viewLifecycleOwner) { marcas ->
            val nombres = listOf(getString(R.string.todas_las_marcas)) + marcas.map { it.nombre }
            binding.spinnerMarca.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres
            )
            binding.spinnerMarca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.filtrarPorMarca(if (position == 0) null else marcas[position - 1].id)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        viewModel.areasFiltro.observe(viewLifecycleOwner) { areas ->
            val nombres = listOf(getString(R.string.todas_las_areas)) + areas.map { it.nombre }
            binding.spinnerArea.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres
            )
            binding.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.filtrarPorArea(if (position == 0) null else areas[position - 1].id)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        viewModel.equipos.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressEquipos.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressEquipos.visibility = View.GONE
                    adapter.actualizarLista(resource.data)
                    binding.tvEquiposEncontrados.text =
                        getString(R.string.equipos_encontrados, resource.data.size)
                }
                is Resource.Error -> {
                    binding.progressEquipos.visibility = View.GONE
                    android.widget.Toast.makeText(requireContext(), resource.mensaje, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.cargarEquipos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}




