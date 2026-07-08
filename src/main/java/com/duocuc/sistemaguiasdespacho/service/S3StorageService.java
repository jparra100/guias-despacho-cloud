package com.duocuc.sistemaguiasdespacho.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Encapsula la subida y descarga de los PDF de las guias de despacho en AWS S3.
 */
@Service
public class S3StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Sube el PDF generado al bucket S3 y devuelve la key (nombre del archivo
     * dentro del bucket) para guardarla en la base de datos.
     */
    public String subirPdf(String numeroGuia, byte[] contenidoPdf) {
        String key = "guias-despacho/%s.pdf".formatted(numeroGuia);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(contenidoPdf));
        return key;
    }

    /**
     * Descarga el PDF desde S3 a partir de su key.
     */
    public byte[] descargarPdf(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> objeto = s3Client.getObject(request)) {
            return objeto.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar el PDF desde S3 (key: " + key + ")", e);
        }
    }
}
