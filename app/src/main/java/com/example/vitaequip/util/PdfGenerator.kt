package com.example.vitaequip.util

import android.content.Context
import android.print.PrintAttributes
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.vitaequip.data.model.Equipo
import com.example.vitaequip.data.model.ReporteIncidencia
import com.example.vitaequip.ui.equipos.detail.EventoHistorialUiModel
import android.print.PrintManager
import com.example.vitaequip.ui.equipos.detail.EquipoDetalleUiModel

object PdfGenerator {

    fun generarPdfHistorial(
        context: Context,
        detalle: EquipoDetalleUiModel,
        eventos: List<EventoHistorialUiModel>,
        reportes: List<ReporteIncidencia>
    ) {
        val webView = WebView(context)
        webView.settings.javaScriptEnabled = false

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                imprimir(context, webView, detalle.equipo.codigo)
            }
        }

        webView.loadDataWithBaseURL(null, construirHtml(detalle, eventos, reportes), "text/html", "UTF-8", null)
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

     fun construirHtml(
        detalle: EquipoDetalleUiModel,
        eventos: List<EventoHistorialUiModel>,
        reportes: List<ReporteIncidencia>
    ): String {
        val equipo = detalle.equipo

        val filasEventos = eventos.mapIndexed { index, evento ->
            val fondoFila = if (index % 2 == 0) "#FFFFFF" else "#F4F7F9"
            when (evento) {
                is EventoHistorialUiModel.CambioEstado -> """
                <tr style="background-color:$fondoFila">
                    <td>${DateUtils.formatearFechaHora(evento.fecha)}</td>
                    <td><span class="tag-tipo">Estado</span></td>
                    <td>De <b>${evento.estadoAnteriorNombre}</b> a <b>${evento.estadoNuevoNombre}</b></td>
                    <td>${evento.usuarioNombre}</td>
                </tr>
            """.trimIndent()
                is EventoHistorialUiModel.CambioUbicacion -> """
                <tr style="background-color:$fondoFila">
                    <td>${DateUtils.formatearFechaHora(evento.fecha)}</td>
                    <td><span class="tag-tipo">Ubicación</span></td>
                    <td>Trasladado a <b>${evento.areaNombre}</b></td>
                    <td>${evento.usuarioNombre}</td>
                </tr>
            """.trimIndent()
            }
        }.joinToString("")

        val filasReportes = reportes.mapIndexed { index, r ->
            val fondoFila = if (index % 2 == 0) "#FFFFFF" else "#F4F7F9"
            val badge = if (r.atendido)
                """<span class="badge badge-ok">Sí</span>"""
            else
                """<span class="badge badge-pendiente">No</span>"""
            """
            <tr style="background-color:$fondoFila">
                <td>${DateUtils.formatearFechaHora(r.creadoEn)}</td>
                <td>${r.descripcion}</td>
                <td>$badge</td>
            </tr>
        """.trimIndent()
        }.joinToString("")

        val filasEventosHtml = if (eventos.isEmpty())
            """<tr><td colspan="4" class="vacio">Sin cambios registrados</td></tr>"""
        else filasEventos

        val filasReportesHtml = if (reportes.isEmpty())
            """<tr><td colspan="3" class="vacio">Sin reportes registrados</td></tr>"""
        else filasReportes

        return """
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                * { box-sizing: border-box; }
                body {
                    font-family: sans-serif;
                    color: #0D3B54;
                    padding: 0;
                    margin: 0;
                    background-color: #FFFFFF;
                }
                .contenido { padding: 28px; }

                .header {
                    background-color: #0D3B54;
                    background-image: linear-gradient(135deg, #0D3B54 0%, #1B6E8C 100%);
                    padding: 24px 28px;
                    color: #FFFFFF;
                }
                .header h1 {
                    font-size: 22px;
                    margin: 0 0 4px 0;
                }
                .header .subtitulo {
                    font-size: 12px;
                    color: #2FD4C4;
                    letter-spacing: 0.5px;
                    text-transform: uppercase;
                }

                h2 {
                    color: #1B6E8C;
                    font-size: 15px;
                    margin-top: 26px;
                    margin-bottom: 10px;
                    padding-bottom: 6px;
                    border-bottom: 3px solid #2FD4C4;
                }

                .grid-datos {
                    display: grid;
                    grid-template-columns: repeat(3, 1fr);
                    gap: 10px;
                }
                .dato-card {
                    background-color: #F4F7F9;
                    border: 1px solid #D1D5DB;
                    border-radius: 10px;
                    padding: 10px 12px;
                }
                .dato-card .etiqueta {
                    font-size: 10px;
                    color: #6B7280;
                    text-transform: uppercase;
                    letter-spacing: 0.3px;
                    margin-bottom: 2px;
                }
                .dato-card .valor {
                    font-size: 13px;
                    font-weight: bold;
                    color: #0D3B54;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    border-radius: 10px;
                    overflow: hidden;
                    border: 1px solid #D1D5DB;
                }
                th {
                    background-color: #1B6E8C;
                    color: #FFFFFF;
                    text-align: left;
                    padding: 8px 10px;
                    font-size: 11px;
                }
                td {
                    padding: 8px 10px;
                    font-size: 11px;
                    border-top: 1px solid #E5E7EB;
                }
                td.vacio {
                    text-align: center;
                    color: #6B7280;
                    font-style: italic;
                    padding: 16px;
                }

                .tag-tipo {
                    background-color: #D9EAF7;
                    color: #1B6E8C;
                    padding: 2px 8px;
                    border-radius: 6px;
                    font-size: 10px;
                    font-weight: bold;
                }

                .badge {
                    padding: 2px 10px;
                    border-radius: 12px;
                    font-size: 10px;
                    font-weight: bold;
                }
                .badge-ok {
                    background-color: #D1FAE5;
                    color: #22C55E;
                }
                .badge-pendiente {
                    background-color: #FCE4E4;
                    color: #EF4444;
                }

                .footer {
                    margin-top: 30px;
                    padding-top: 10px;
                    border-top: 1px solid #D1D5DB;
                    font-size: 9px;
                    color: #6B7280;
                    text-align: center;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="subtitulo">VitaEquip</div>
                <h1>Historial de Equipo — ${equipo.codigo}</h1>
            </div>

            <div class="contenido">
                <h2>Datos generales</h2>
                <div class="grid-datos">
                    <div class="dato-card"><div class="etiqueta">Código</div><div class="valor">${equipo.codigo}</div></div>
                    <div class="dato-card"><div class="etiqueta">Descripción</div><div class="valor">${equipo.descripcion}</div></div>
                    <div class="dato-card"><div class="etiqueta">Modelo</div><div class="valor">${equipo.modelo ?: "-"}</div></div>
                    <div class="dato-card"><div class="etiqueta">Serie</div><div class="valor">${equipo.serie ?: "-"}</div></div>
                    <div class="dato-card"><div class="etiqueta">Marca</div><div class="valor">${detalle.marcaNombre}</div></div>
                    <div class="dato-card"><div class="etiqueta">Área</div><div class="valor">${detalle.areaNombre}</div></div>
                    <div class="dato-card"><div class="etiqueta">Piso</div><div class="valor">${detalle.pisoNombre}</div></div>
                    <div class="dato-card"><div class="etiqueta">Ubicación</div><div class="valor">${equipo.ubicacionEspecifica ?: "-"}</div></div>
                    <div class="dato-card"><div class="etiqueta">Potencia</div><div class="valor">${equipo.potenciaValor ?: "-"} ${equipo.potenciaUnidad ?: ""}</div></div>
                    <div class="dato-card"><div class="etiqueta">Batería Voltaje</div><div class="valor">${equipo.bateriaVoltaje ?: "No aplica"}</div></div>
                    <div class="dato-card"><div class="etiqueta">Batería Amperaje</div><div class="valor">${equipo.bateriaAmperaje ?: "No aplica"}</div></div>
                    <div class="dato-card"><div class="etiqueta">Batería Cantidad</div><div class="valor">${equipo.bateriaCantidad ?: "No aplica"}</div></div>
                </div>

                <h2>Historial de cambios</h2>
                <table>
                    <tr><th>Fecha</th><th>Tipo</th><th>Detalle</th><th>Modificado por</th></tr>
                    $filasEventosHtml
                </table>

                <h2>Reportes de incidencia</h2>
                <table>
                    <tr><th>Fecha</th><th>Descripción</th><th>Atendido</th></tr>
                    $filasReportesHtml
                </table>

                <div class="footer">Generado desde VitaEquip</div>
            </div>
        </body>
        </html>
    """.trimIndent()
    }
}