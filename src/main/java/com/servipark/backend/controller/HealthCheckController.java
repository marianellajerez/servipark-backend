package com.servipark.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @GetMapping
    public Map<String, String> check() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "La API de ServiPark está corriendo");
        return response;
    }
}