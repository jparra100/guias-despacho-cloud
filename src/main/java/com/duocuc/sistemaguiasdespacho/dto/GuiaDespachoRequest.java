package com.duocuc.sistemaguiasdespacho.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuiaDespachoRequest {

    @NotBlank(message = "El transportista es obligatorio")
    private String transportista;

    @NotNull(message = "La fecha de despacho es obligatoria")
    private LocalDate fechaDespacho;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    @Positive(message = "El peso debe ser mayor a 0")
    private Double pesoKg;
}
