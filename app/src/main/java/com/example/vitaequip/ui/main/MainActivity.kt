package com.example.vitaequip.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.vitaequip.R
import com.example.vitaequip.data.remote.SupabaseClient
import com.example.vitaequip.databinding.ActivityMainBinding
import com.example.vitaequip.ui.roleselect.RoleSelectActivity
import com.example.vitaequip.util.SessionManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
           .findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        viewModel.cargarUsuarioActual()

        if(!viewModel.esAdmin()){
            binding.bottomNav.menu.findItem(R.id.reporteListFragment)?.isVisible = false
        }

        binding.navigationView.setNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.menuCerrarSesion ->{
                    cerraSesion()
                    true
                }
                else-> false
            }
        }

    }
    private fun cerraSesion(){
        lifecycleScope.launch {
            SupabaseClient.client.auth.signOut()
            SessionManager.cerrarSesion()

            val intent = Intent(this@MainActivity, RoleSelectActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    fun abrirDrawer() {
        binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }
}
