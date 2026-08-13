package com.example.vitaequip.ui.scanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.vitaequip.R
import com.example.vitaequip.data.repository.EquipoRepository
import com.example.vitaequip.ui.equipos.detail.EquipoDetailActivity
import com.example.vitaequip.util.Constants
import com.example.vitaequip.util.Resource
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch

class QrScannerActivity : AppCompatActivity() {

    private val equipoRepository = EquipoRepository()

    private val scanLauncher = registerForActivityResult(ScanContract())
    {
        resultado ->
        if(resultado.contents == null){
            //si el usuario cancela el escanel
            finish()
            return@registerForActivityResult

        }

        val equipoId = resultado.contents.toLongOrNull()
        if(equipoId == null){
            Toast.makeText(this, "Codigo QR no vailido para vitapro", Toast.LENGTH_LONG)
            finish()
            return@registerForActivityResult
        }
        buscarYAbrirEquipo(equipoId)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val opciones = ScanOptions()
        opciones.setBeepEnabled(true)
        opciones.setOrientationLocked(true)
        scanLauncher.launch(opciones)



    }



    private fun buscarYAbrirEquipo(equipoId: Long){
        lifecycleScope.launch {
            try{
                val equipo = equipoRepository.obtenerPorId(equipoId)

                if(equipo == null){
                    Toast.makeText(
                        this@QrScannerActivity,
                        "NO SE ENCONTRÓ NINGUN EQUIPO CON ESE CODIGO",
                        Toast.LENGTH_SHORT
                    )
                }else{
                    val intent = Intent(this@QrScannerActivity, EquipoDetailActivity::class.java)
                    intent.putExtra(Constants.EXTRA_EQUIPO_ID, equipoId)
                    startActivity(intent)
                }
            }catch (e: Exception){

                Log.e(
                    "CODIGO_EQUIPO_ERROR",
                    "Error al buscar equipo con ID: ${equipoId}",
                    e
                )
                Toast.makeText(
                    this@QrScannerActivity,
                    e.message ?: "Error al buscar el equipo",
                    Toast.LENGTH_SHORT
                ).show()
            }

            finish()
        }
    }

}