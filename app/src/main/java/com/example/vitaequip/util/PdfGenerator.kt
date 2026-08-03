package com.example.vitaequip.util

import android.content.Context
import android.print.PrintAttributes
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.ui.equipos.detail.EventoHistorialUiModel
import android.print.PrintManager

object PdfGenerator {

    fun generarPdfHistorial(
        context: Context,
        equipo: Equipo,
        eventos: List<EventoHistorialUiModel>,
        reportes: List<ReporteIncidencia>
    ) {
        val webView = WebView(context)
        webView.settings.javaScriptEnabled = false

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                imprimir(context, webView, equipo.codigo)
            }
        }

        webView.loadDataWithBaseURL(null, construirHtml(equipo, eventos, reportes), "text/html", "UTF-8", null)
    }

    private fun imprimir(context: Context, webView: WebView, nombreEquipo: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val adapter = webView.createPrintDocumentAdapter("historial_$nombreEquipo")

        val atributos = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print("historial_$nombreEquipo", adapter, atributos)
    }

    private fun construirHtml(
        equipo: Equipo, eventos: List<EventoHistorialUiModel>, reportes: List<ReporteIncidencia>
    ): String {
        val filasEventos = eventos.joinToString("") { evento ->
            when (evento) {
                is EventoHistorialUiModel.CambioEstado -> """
                    <tr>
                        <td>${evento.fecha}</td>
                        <td>Estado</td>
                        <td>De ${evento.estadoAnteriorNombre} a ${evento.estadoNuevoNombre}</td>
                        <td>${evento.usuarioNombre}</td>
                    </tr>
                """.trimIndent()
                is EventoHistorialUiModel.CambioUbicacion -> """
                    <tr>
                        <td>${evento.fecha}</td>
                        <td>Ubicación</td>
                        <td>Trasladado a ${evento.areaNombre}</td>
                        <td>${evento.usuarioNombre}</td>
                    </tr>
                """.trimIndent()
            }
        }

        val filasReportes = reportes.joinToString("") { r ->
            """
                <tr>
                    <td>${r.creadoEn}</td>
                    <td>${r.descripcion}</td>
                    <td>${if (r.atendido) "Sí" else "No"}</td>
                </tr>
            """.trimIndent()
        }

        return """
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: sans-serif; color: #0D3B54; padding: 24px; }
                    h1 { color: #1B6E8C; font-size: 20px; margin-bottom: 4px; }
                    h2 { color: #1B6E8C; font-size: 15px; margin-top: 22px; border-bottom: 2px solid #2FD4C4; padding-bottom: 4px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 8px; }
                    th { background-color: #1B6E8C; color: white; text-align: left; padding: 6px; font-size: 11px; }
                    td { border-bottom: 1px solid #D1D5DB; padding: 6px; font-size: 11px; }
                    .info-tabla td { border: none; padding: 3px 6px; }
                </style>
            </head>
            <body>
                <h1>Historial de Equipo — VitaEquip</h1>

                <h2>Datos generales</h2>
                <table class="info-tabla">
                    <tr><td><b>Código</b></td><td>${equipo.codigo}</td></tr>
                    <tr><td><b>Descripción</b></td><td>${equipo.descripcion}</td></tr>
                    <tr><td><b>Modelo</b></td><td>${equipo.modelo ?: "-"}</td></tr>
                </table>

                <h2>Historial de cambios</h2>
                <table>
                    <tr><th>Fecha</th><th>Tipo</th><th>Detalle</th><th>Modificado por</th></tr>
                    $filasEventos
                </table>

                <h2>Reportes de incidencia</h2>
                <table>
                    <tr><th>Fecha</th><th>Descripción</th><th>Atendido</th></tr>
                    $filasReportes
                </table>
            </body>
            </html>
        """.trimIndent()
    }
}