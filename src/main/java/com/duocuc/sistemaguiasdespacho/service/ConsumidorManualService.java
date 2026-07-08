package com.duocuc.sistemaguiasdespacho.service;

import com.duocuc.sistemaguiasdespacho.config.RabbitMQConfig;
import com.duocuc.sistemaguiasdespacho.entity.MensajeCola;
import com.duocuc.sistemaguiasdespacho.repository.MensajeColaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsumidorManualService {

    private final RabbitTemplate rabbitTemplate;
    private final MensajeColaRepository mensajeColaRepository;

    public ConsumidorManualService(RabbitTemplate rabbitTemplate,
                                   MensajeColaRepository mensajeColaRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.mensajeColaRepository = mensajeColaRepository;
    }

    public String consumirMensaje() {
        Object mensaje = rabbitTemplate.receiveAndConvert(RabbitMQConfig.COLA_GUIAS);

        if (mensaje == null) {
            return "No hay mensajes en la cola principal";
        }

        MensajeCola mensajeCola = new MensajeCola(
                mensaje.toString(),
                RabbitMQConfig.COLA_GUIAS,
                LocalDateTime.now()
        );

        mensajeColaRepository.save(mensajeCola);

        return "Mensaje consumido y guardado en BD: " + mensaje;
    }
}
