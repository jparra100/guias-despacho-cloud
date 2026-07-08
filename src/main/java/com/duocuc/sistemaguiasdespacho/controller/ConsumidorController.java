package com.duocuc.sistemaguiasdespacho.controller;

import com.duocuc.sistemaguiasdespacho.service.ConsumidorManualService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colas")
public class ConsumidorController {

    private final ConsumidorManualService consumidorManualService;

    public ConsumidorController(ConsumidorManualService consumidorManualService) {
        this.consumidorManualService = consumidorManualService;
    }

    @PostMapping("/consumir")
    public String consumirMensaje() {
        return consumidorManualService.consumirMensaje();
    }
}