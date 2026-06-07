package com.smartpm.backend.auth.dto;

/**
 * Resultado interno del login/registro/refresh.
 * No se serializa directamente: el controlador convierte el accessToken en AuthResponse
 * y el refreshToken en una cookie HttpOnly.
 */
public record LoginResult(String accessToken, String refreshToken) {}
