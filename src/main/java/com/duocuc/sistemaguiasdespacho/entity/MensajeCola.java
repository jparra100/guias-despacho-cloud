package com.duocuc.sistemaguiasdespacho.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_cola")
public class MensajeCola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contenido", columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(name = "cola_origen", nullable = false)
    private String colaOrigen;

    @Column(name = "fecha_consumo", nullable = false)
    private LocalDateTime fechaConsumo;

    public MensajeCola() {
    }

    public MensajeCola(String contenido, String colaOrigen, LocalDateTime fechaConsumo) {
        this.contenido = contenido;
        this.colaOrigen = colaOrigen;
        this.fechaConsumo = fechaConsumo;
    }

    public Long getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getColaOrigen() {
        return colaOrigen;
    }

    public void setColaOrigen(String colaOrigen) {
        this.colaOrigen = colaOrigen;
    }

    public LocalDateTime getFechaConsumo() {
        return fechaConsumo;
    }

    public void setFechaConsumo(LocalDateTime fechaConsumo) {
        this.fechaConsumo = fechaConsumo;
    }
}
