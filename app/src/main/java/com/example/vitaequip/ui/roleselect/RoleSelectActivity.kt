package com.example.vitaequip.ui.roleselect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vitaequip.data.model.RolUsuario
import com.example.vitaequip.databinding.ActivityRoleSelectBinding
import com.example.vitaequip.ui.login.LoginActivity
import com.example.vitaequip.util.Constants

class RoleSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdminLogin.setOnClickListener {
            irALogin(RolUsuario.ADMIN)
        }

        binding.btnUserLogin.setOnClickListener {
            irALogin(RolUsuario.LECTOR)
        }



    }

    private fun irALogin(rolSeleccionado: RolUsuario){
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(Constants.EXTRA_ROL_SELECCIONADO, rolSeleccionado.name)
        startActivity(intent)
    }
}