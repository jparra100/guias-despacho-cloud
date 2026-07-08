package com.duocuc.sistemaguiasdespacho.controller;

import com.duocuc.sistemaguiasdespacho.service.ProductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductorController {

    @Autowired
    private ProductorService producer;

    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        producer.sendMessage(message);
        return "Mensaje enviado: " + message;
    }
}