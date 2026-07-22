package com.example.vitaequip.ui.equipos.form

object EquipoFormValidator {

    fun validarCodigo(codigo: String): String?{
        return if(codigo.isBlank()){
            "El codigo es obligatorio"
        }else{
            null
        }
    }

    fun validarDescripcion(descripcion: String): String?{
        return if(descripcion.isBlank()){
            "La descripcion es obligatoria"
        }else{
            null
        }
    }

    fun validarEstado(estadoId:Int?): String?{
        return if(estadoId == null){
            "Debes seleccionar un estado"
        }else{
            null
        }
    }
}