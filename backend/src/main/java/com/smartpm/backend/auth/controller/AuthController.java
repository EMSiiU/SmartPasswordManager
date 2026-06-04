package com.smartpm.backend.auth.controller;

import com.smartpm.backend.auth.dto.AuthResponse;
import com.smartpm.backend.auth.dto.LoginRequest;
import com.smartpm.backend.auth.dto.RegistroRequest;
import com.smartpm.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de autenticacion (publicos) y uno protegido de prueba.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /auth/register -> crea usuario y devuelve token. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegistroRequest req) {
        return authService.registrar(req);
    }

    /** POST /auth/login -> valida credenciales y devuelve token. */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    /**
     * GET /auth/me -> ruta PROTEGIDA de prueba.
     * Solo responde si llega un token valido. Sirve para comprobar
     * que todo el circuito (filtro + seguridad) funciona.
     * El objeto Authentication lo inyecta Spring desde el contexto
     * que llenamos en el filtro JWT.
     */
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "correo", authentication.getName(),
                "roles", authentication.getAuthorities()
        );
    }
}
