package com.example.vitaequip.ui.roleselect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vitaequip.data.model.RolUsuario
import com.example.vitaequip.data.remote.SupabaseClient
import com.example.vitaequip.data.repository.UsuarioRepository
import com.example.vitaequip.databinding.ActivityRoleSelectBinding
import com.example.vitaequip.ui.login.LoginActivity
import com.example.vitaequip.util.Constants
import com.example.vitaequip.util.Resource
import com.example.vitaequip.util.SessionManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class RoleSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectBinding
    private val usuarioRepository = UsuarioRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)


        verificarSesionActiva()

        binding.btnAdminLogin.setOnClickListener {
            irALogin(RolUsuario.ADMIN)
        }

        binding.btnUserLogin.setOnClickListener {
            irALogin(RolUsuario.LECTOR)
        }

    }
    private fun verificarSesionActiva(){
        lifecycleScope.launch {

            try {
                val session = SupabaseClient.client.auth.currentSessionOrNull()

                if(session!=null){
                    val authId = session.user?.id

                    if(authId != null){
                        val usuario = usuarioRepository.obtenerPorAuthId(authId)
                        if(usuario != null && usuario.activo){
                            SessionManager.iniciarSesion(usuario)
                            irAMain()
                            return@launch
                        }
                    }
                }

            }catch (e: Exception){
                Resource.Error(e.message?: "Error al cargar el token")

            }

        }

    }


    private fun irAMain(){
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun irALogin(rolSeleccionado: RolUsuario){
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(Constants.EXTRA_ROL_SELECCIONADO, rolSeleccionado.name)
        startActivity(intent)
    }
}