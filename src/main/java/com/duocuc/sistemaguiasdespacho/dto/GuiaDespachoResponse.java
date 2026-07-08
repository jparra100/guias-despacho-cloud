package com.duocuc.sistemaguiasdespacho.dto;

import com.duocuc.sistemaguiasdespacho.entity.GuiaDespacho;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespachoResponse {

    private Long id;
    private String numeroGuia;
    private String transportista;
    private LocalDate fechaDespacho;
    private String destino;
    private String contenido;
    private Double pesoKg;
    private String estado;
    private boolean pdfDisponible;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public static GuiaDespachoResponse desdeEntidad(GuiaDespacho g) {
        return GuiaDespachoResponse.builder()
                .id(g.getId())
                .numeroGuia(g.getNumeroGuia())
                .transportista(g.getTransportista())
                .fechaDespacho(g.getFechaDespacho())
                .destino(g.getDestino())
                .contenido(g.getContenido())
                .pesoKg(g.getPesoKg())
                .estado(g.getEstado().name())
                .pdfDisponible(g.getS3Key() != null && !g.getS3Key().isBlank())
                .fechaCreacion(g.getFechaCreacion())
                .fechaActualizacion(g.getFechaActualizacion())
                .build();
    }
}
