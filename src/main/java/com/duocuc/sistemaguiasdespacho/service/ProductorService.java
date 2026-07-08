package com.duocuc.sistemaguiasdespacho.service;

import com.duocuc.sistemaguiasdespacho.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductorService {

    private final RabbitTemplate rabbitTemplate;

    public ProductorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_GUIAS, message);
            System.out.println("Mensaje enviado a cola principal: " + message);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_ERRORES, message);
            System.out.println("Mensaje enviado a cola de errores: " + message);
        }
    }
}