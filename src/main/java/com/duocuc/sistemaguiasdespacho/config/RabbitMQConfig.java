package com.duocuc.sistemaguiasdespacho.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_GUIAS = "guias.queue";
    public static final String COLA_ERRORES = "guias.error.queue";

    @Bean
    public Queue guiasQueue() {
        return new Queue(COLA_GUIAS, true);
    }

    @Bean
    public Queue guiasErrorQueue() {
        return new Queue(COLA_ERRORES, true);
    }
}