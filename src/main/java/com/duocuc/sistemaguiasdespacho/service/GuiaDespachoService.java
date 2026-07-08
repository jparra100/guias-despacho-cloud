package com.duocuc.sistemaguiasdespacho.service;

import com.duocuc.sistemaguiasdespacho.dto.GuiaDespachoRequest;
import com.duocuc.sistemaguiasdespacho.dto.GuiaDespachoResponse;
import com.duocuc.sistemaguiasdespacho.entity.GuiaDespacho;
import com.duocuc.sistemaguiasdespacho.exception.RecursoNoEncontradoException;
import com.duocuc.sistemaguiasdespacho.repository.GuiaDespachoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GuiaDespachoService {

    private final GuiaDespachoRepository repository;
    private final PdfGeneratorService pdfGeneratorService;
    private final S3StorageService s3StorageService;
    private final ProductorService productorService;

    private static final DateTimeFormatter FORMATO_NUMERO = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GuiaDespachoService(GuiaDespachoRepository repository,
                               PdfGeneratorService pdfGeneratorService,
                               S3StorageService s3StorageService,
                               ProductorService productorService) {
        this.repository = repository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.s3StorageService = s3StorageService;
        this.productorService = productorService;
    }

    @Transactional
    public GuiaDespachoResponse crear(GuiaDespachoRequest request) {
        GuiaDespacho guia = GuiaDespacho.builder()
                .numeroGuia(generarNumeroGuia())
                .transportista(request.getTransportista())
                .fechaDespacho(request.getFechaDespacho())
                .destino(request.getDestino())
                .contenido(request.getContenido())
                .pesoKg(request.getPesoKg())
                .estado(GuiaDespacho.EstadoGuia.CREADA)
                .build();

        GuiaDespacho guardada = repository.save(guia);

        String mensaje = "Guía creada: ID=" + guardada.getId()
                + ", Número=" + guardada.getNumeroGuia()
                + ", Transportista=" + guardada.getTransportista()
                + ", Fecha=" + guardada.getFechaDespacho()
                + ", Destino=" + guardada.getDestino();

        productorService.sendMessage(mensaje);

        return GuiaDespachoResponse.desdeEntidad(guardada);
    }

    @Transactional
    public GuiaDespachoResponse generarYSubirPdf(Long id) {
        GuiaDespacho guia = obtenerEntidadPorId(id);

        byte[] pdf = pdfGeneratorService.generarPdf(guia);
        String s3Key = s3StorageService.subirPdf(guia.getNumeroGuia(), pdf);

        guia.setS3Key(s3Key);
        guia.setEstado(GuiaDespacho.EstadoGuia.PDF_GENERADO);
        GuiaDespacho actualizada = repository.save(guia);

        return GuiaDespachoResponse.desdeEntidad(actualizada);
    }

    @Transactional(readOnly = true)
    public byte[] descargarPdf(Long id) {
        GuiaDespacho guia = obtenerEntidadPorId(id);

        if (guia.getS3Key() == null || guia.getS3Key().isBlank()) {
            throw new RecursoNoEncontradoException(
                    "La guia " + guia.getNumeroGuia() + " aun no tiene un PDF generado");
        }

        return s3StorageService.descargarPdf(guia.getS3Key());
    }

    @Transactional
    public GuiaDespachoResponse actualizar(Long id, GuiaDespachoRequest request) {
        GuiaDespacho guia = obtenerEntidadPorId(id);

        guia.setTransportista(request.getTransportista());
        guia.setFechaDespacho(request.getFechaDespacho());
        guia.setDestino(request.getDestino());
        guia.setContenido(request.getContenido());
        guia.setPesoKg(request.getPesoKg());

        GuiaDespacho actualizada = repository.save(guia);
        return GuiaDespachoResponse.desdeEntidad(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        GuiaDespacho guia = obtenerEntidadPorId(id);
        repository.delete(guia);
    }

    @Transactional(readOnly = true)
    public List<GuiaDespachoResponse> consultar(String transportista, LocalDate fecha) {
        List<GuiaDespacho> resultado;

        if (transportista != null && !transportista.isBlank() && fecha != null) {
            resultado = repository.findByTransportistaContainingIgnoreCaseAndFechaDespacho(transportista, fecha);
        } else if (transportista != null && !transportista.isBlank()) {
            resultado = repository.findByTransportistaContainingIgnoreCase(transportista);
        } else if (fecha != null) {
            resultado = repository.findByFechaDespacho(fecha);
        } else {
            resultado = repository.findAll();
        }

        return resultado.stream()
                .map(GuiaDespachoResponse::desdeEntidad)
                .collect(Collectors.toList());
    }

    public GuiaDespachoResponse obtenerPorId(Long id) {
        return GuiaDespachoResponse.desdeEntidad(obtenerEntidadPorId(id));
    }

    private GuiaDespacho obtenerEntidadPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro la guia de despacho con id " + id));
    }

    private String generarNumeroGuia() {
        String fecha = LocalDate.now().format(FORMATO_NUMERO);
        String sufijo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "GD-" + fecha + "-" + sufijo;
    }
}
