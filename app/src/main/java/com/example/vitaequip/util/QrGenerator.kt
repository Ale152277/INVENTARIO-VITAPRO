package com.example.vitaequip.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

object QrGenerator {
    fun  generarBytesPng(contenido: String, tamano: Int = 512): ByteArray{
        val writer = QRCodeWriter()
        val matriz = writer.encode(contenido, BarcodeFormat.QR_CODE, tamano, tamano)

        val bitmap = Bitmap.createBitmap(tamano, tamano, Bitmap.Config.RGB_565)

        for (x in 0 until tamano){
            for(y in 0 until tamano){
                bitmap.setPixel(x, y, if (matriz.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}