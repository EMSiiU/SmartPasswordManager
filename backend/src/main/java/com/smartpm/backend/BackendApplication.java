package com.smartpm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Punto de entrada de la aplicacion Smart Password Manager.
 * La anotacion @SpringBootApplication activa la autoconfiguracion,
 * el escaneo de componentes y la configuracion por defecto de Spring.
 *
 * @EnableAsync permite usar @Async en servicios (lo usamos en AccesoService
 * para registrar eventos sin bloquear el hilo del request).
 */
@SpringBootApplication
@EnableAsync
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
