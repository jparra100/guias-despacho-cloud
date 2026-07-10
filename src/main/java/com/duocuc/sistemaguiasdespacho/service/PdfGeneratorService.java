package com.duocuc.sistemaguiasdespacho.service;

import com.duocuc.sistemaguiasdespacho.entity.GuiaDespacho;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Genera el documento PDF de una Guia de Despacho usando openhtmltopdf.
 */
@Service
public class PdfGeneratorService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public byte[] generarPdf(GuiaDespacho guia) {
        String html = construirHtml(guia);

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(salida);
            builder.run();
            return salida.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la guia " + guia.getNumeroGuia(), e);
        }
    }

    private String construirHtml(GuiaDespacho guia) {
        return """
                <html>
                <head>
                <style>
                    body { font-family: Helvetica, Arial, sans-serif; margin: 40px; color: #222; }
                    h1 { color: #1F4E79; border-bottom: 2px solid #1F4E79; padding-bottom: 8px; }
                    table { width: 100%%; border-collapse: collapse; margin-top: 20px; }
                    td { padding: 8px; border: 1px solid #ccc; }
                    td.label { background-color: #EFF4FA; font-weight: bold; width: 35%%; }
                    .footer { margin-top: 40px; font-size: 10px; color: #777; }
                </style>
                </head>
                <body>
                    <h1>Guia de Despacho N°; %s</h1>
                    <table>
                        <tr><td class="label">Transportista</td><td>%s</td></tr>
                        <tr><td class="label">Fecha de despacho</td><td>%s</td></tr>
                        <tr><td class="label">Destino</td><td>%s</td></tr>
                        <tr><td class="label">Contenido</td><td>%s</td></tr>
                        <tr><td class="label">Peso (Kg)</td><td>%s</td></tr>
                        <tr><td class="label">Estado</td><td>%s</td></tr>
                    </table>
                    <p class="footer">Documento generado automaticamente por Sistema de Guias de Despacho - CDY2204</p>
                </body>
                </html>
                """.formatted(
                guia.getNumeroGuia(),
                guia.getTransportista(),
                guia.getFechaDespacho().format(FORMATO_FECHA),
                guia.getDestino(),
                guia.getContenido(),
                guia.getPesoKg(),
                guia.getEstado()
        );
    }
}
