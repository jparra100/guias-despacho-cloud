package com.duocuc.sistemaguiasdespacho.controller;

import com.duocuc.sistemaguiasdespacho.dto.GuiaDespachoRequest;
import com.duocuc.sistemaguiasdespacho.dto.GuiaDespachoResponse;
import com.duocuc.sistemaguiasdespacho.service.GuiaDespachoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Expone los 6 endpoints requeridos por el caso.
 *
 * Reglas de rol (definidas via Azure AD B2C custom claim -> mapeadas a
 * authorities de Spring Security en JwtAuthenticationConverterConfig):
 *  - ROLE_DESCARGA_GUIAS: solo puede usar /descargar
 *  - ROLE_OPERADOR: puede usar el resto de los endpoints (y tambien descargar)
 */
@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    private final GuiaDespachoService service;

    public GuiaDespachoController(GuiaDespachoService service) {
        this.service = service;
    }

    // 1. Crear guia de despacho
    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<GuiaDespachoResponse> crear(@Valid @RequestBody GuiaDespachoRequest request) {
        GuiaDespachoResponse creada = service.crear(request);
        return ResponseEntity.status(201).body(creada);
    }

    // 2. Generar y subir el PDF de la guia a S3
    @PostMapping("/{id}/generar-pdf")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<GuiaDespachoResponse> generarYSubirPdf(@PathVariable Long id) {
        return ResponseEntity.ok(service.generarYSubirPdf(id));
    }

    // 3. Descargar guia (PDF) - accesible por ambos roles
    @GetMapping("/{id}/descargar")
    @PreAuthorize("hasAnyRole('OPERADOR', 'DESCARGA_GUIAS')")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id) {
        byte[] pdf = service.descargarPdf(id);
        GuiaDespachoResponse guia = service.obtenerPorId(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + guia.getNumeroGuia() + ".pdf\"")
                .body(pdf);
    }

    // 4. Modificar / actualizar guia
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<GuiaDespachoResponse> actualizar(@PathVariable Long id,
                                                             @Valid @RequestBody GuiaDespachoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    // 5. Eliminar guia especifica
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Consultar guias por transportista y/o fecha
    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<GuiaDespachoResponse>> consultar(
            @RequestParam(required = false) String transportista,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.consultar(transportista, fecha));
    }

    // Extra: obtener una guia puntual por id (util para pruebas)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<GuiaDespachoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}
