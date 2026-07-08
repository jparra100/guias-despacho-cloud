package com.duocuc.sistemaguiasdespacho.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "guias_despacho")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroGuia;

    @Column(nullable = false, length = 120)
    private String transportista;

    @Column(nullable = false)
    private LocalDate fechaDespacho;

    @Column(nullable = false, length = 200)
    private String destino;

    @Column(nullable = false, length = 500)
    private String contenido;

    @Column(nullable = false)
    private Double pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoGuia estado;

    /** Clave del archivo PDF dentro del bucket de S3 (no la URL completa). */
    @Column(length = 300)
    private String s3Key;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoGuia.CREADA;
        }
    }

    @PreUpdate
    public void alActualizar() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public enum EstadoGuia {
        CREADA,
        PDF_GENERADO,
        DESPACHADA,
        ENTREGADA,
        ANULADA
    }
}
