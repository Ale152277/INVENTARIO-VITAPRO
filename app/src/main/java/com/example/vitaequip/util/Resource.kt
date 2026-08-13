package com.example.vitaequip.util

sealed class Resource <out T>{
    object Loading: Resource<Nothing>()

    data class Success<T>(val data: T): Resource<T>()
    data class Error(
        val mensaje: String,
        val exception: Throwable?= null
    ): Resource<Nothing>()
}