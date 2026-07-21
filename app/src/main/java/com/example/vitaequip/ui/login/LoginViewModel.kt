package com.example.vitaequip.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitaequip.data.model.Usuario
import com.example.vitaequip.data.remote.SupabaseClient
import com.example.vitaequip.data.repository.UsuarioRepository
import com.example.vitaequip.util.Resource
import com.example.vitaequip.util.SessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){

    private val usuarioRepository = UsuarioRepository()

    private val _loginState = MutableLiveData<Resource<Usuario>>()
    val loginState: LiveData<Resource<Usuario>> = _loginState

    fun iniciarSesion(correo: String, contrasena: String){
        _loginState.value = Resource.Loading

        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signInWith(Email){
                    email = correo
                    password = contrasena
                }

                val authId = SupabaseClient.client.auth.currentUserOrNull()?.id

                if(authId ==null){
                    _loginState.value = Resource.Error("No se pudo obtener la sesion.")
                    return@launch
                }

                val usuario = usuarioRepository.obtenerPorAuthId(authId)

                if(usuario == null){
                    _loginState.value = Resource.Error("Usuario no registrado en el sistema")
                }else if(!usuario.activo){
                    _loginState.value = Resource.Error("Usuario inactivo")
                }else{
                    SessionManager.iniciarSesion(usuario)
                    _loginState.value = Resource.Success(usuario)
                }

            }catch (e: Exception){
                _loginState.value = Resource.Error(e.message ?: "Error al iniciar sesión.")
            }

        }
    }
}