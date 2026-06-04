package com.smartpm.backend.auth.dto;

/**
 * Respuesta tras un login/registro exitoso.
 * tokenType es siempre "Bearer", la convencion estandar para JWT.
 */
public record AuthResponse(
        String accessToken,
        String tokenType
) {
    public static AuthResponse bearer(String token) {
        return new AuthResponse(token, "Bearer");
    }
}
