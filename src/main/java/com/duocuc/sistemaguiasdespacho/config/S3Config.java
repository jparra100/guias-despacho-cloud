package com.duocuc.sistemaguiasdespacho.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.session-token:}")
    private String sessionToken;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * AWS Academy entrega credenciales TEMPORALES (access key, secret key y
     * session token). Este bean detecta automaticamente si hay session token
     * configurado y arma las credenciales correctas.
     * Recuerda: cada vez que reinicies el lab de AWS Academy, estas credenciales
     * cambian y debes actualizarlas en application.yml o como variables de entorno.
     */
    @Bean
    public S3Client s3Client() {
        AwsCredentials credenciales = (sessionToken != null && !sessionToken.isBlank())
                ? AwsSessionCredentials.create(accessKey, secretKey, sessionToken)
                : AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credenciales))
                .build();
    }
}
