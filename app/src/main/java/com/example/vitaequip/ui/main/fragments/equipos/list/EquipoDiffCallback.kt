package com.example.vitaequip.ui.main.fragments.equipos.list

import androidx.recyclerview.widget.DiffUtil //anima los cambios en vez de poner todo de golpe

class EquipoDiffCallback (
    private val listaVieja: List<EquipoUiModel>,
    private val listaNueva: List<EquipoUiModel>
): DiffUtil.Callback(){

    override fun getOldListSize() = listaVieja.size
    override fun getNewListSize() = listaNueva.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return listaVieja[oldItemPosition].id == listaNueva[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return listaVieja[oldItemPosition] == listaNueva [newItemPosition]
    }



}

