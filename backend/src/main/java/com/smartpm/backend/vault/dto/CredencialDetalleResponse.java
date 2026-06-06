package com.smartpm.backend.vault.dto;

/**
 * Vista de DETALLE. Incluye la contrasena YA DESCIFRADA.
 * Solo se devuelve cuando el usuario pide ver una credencial concreta.
 */
public record CredencialDetalleResponse(
        Long id,
        String titulo,
        String usuarioCuenta,
        String email,
        String password,
        String url,
        String notas
) {
}
