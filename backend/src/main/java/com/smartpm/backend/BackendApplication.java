package com.smartpm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Smart Password Manager.
 * La anotacion @SpringBootApplication activa la autoconfiguracion,
 * el escaneo de componentes y la configuracion por defecto de Spring.
 */
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
