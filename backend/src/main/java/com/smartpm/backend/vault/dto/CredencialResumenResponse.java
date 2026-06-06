package com.smartpm.backend.vault.dto;

import com.smartpm.backend.vault.entity.Credencial;

/**
 * Vista de LISTADO. NO incluye la contrasena: al listar la boveda
 * no hace falta exponer las contrasenas de todas las credenciales.
 */
public record CredencialResumenResponse(
        Long id,
        String titulo,
        String usuarioCuenta,
        String email,
        String url
) {
    public static CredencialResumenResponse desde(Credencial c) {
        return new CredencialResumenResponse(
                c.getId(), c.getTitulo(), c.getUsuarioCuenta(),
                c.getEmail(), c.getUrl());
    }
}
