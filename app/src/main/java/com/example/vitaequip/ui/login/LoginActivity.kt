package com.example.vitaequip.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vitaequip.R
import com.example.vitaequip.data.model.RolUsuario
import com.example.vitaequip.databinding.ActivityLoginBinding
import com.example.vitaequip.ui.main.MainActivity
import com.example.vitaequip.util.Constants
import com.example.vitaequip.util.Resource

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var rolEsperado: RolUsuario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rolTexto = intent.getStringExtra(Constants.EXTRA_ROL_SELECCIONADO)
        rolEsperado = rolTexto?.let { RolUsuario.valueOf(it) } ?: RolUsuario.LECTOR

        binding.btnIniciarSesion.setOnClickListener {
            val correo = binding.etCorreo.text.toString()
            val contraseña = binding.etContrasena.text.toString()

            viewModel.iniciarSesion(correo, contraseña, rolEsperado)
        }

        viewModel.loginState.observe(this){resource ->
            when(resource){
                is Resource.Loading->{
                    binding.progressLogin.visibility = android.view.View.VISIBLE
                    binding.tvErrorLogin.visibility = android.view.View.GONE
                }

                is Resource.Success ->{
                    binding.progressLogin.visibility = android.view.View.GONE
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                is Resource.Error ->{
                    binding.progressLogin.visibility = android.view.View.GONE
                    Toast.makeText(
                        this,
                        "Credenciales incorrecas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    }
}