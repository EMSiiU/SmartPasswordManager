package com.smartpm.backend.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controlador minimo para verificar que el backend responde.
 * @RestController indica que esta clase maneja peticiones HTTP
 * y que lo que devuelven sus metodos se serializa a JSON.
 */
@RestController
public class HealthController {

    /**
     * GET /health -> devuelve un JSON simple.
     * Sirve para confirmar de un vistazo que la app esta viva.
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "smart-password-manager",
                "timestamp", Instant.now().toString()
        );
    }
}
