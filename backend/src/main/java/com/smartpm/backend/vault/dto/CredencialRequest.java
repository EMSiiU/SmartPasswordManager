package com.smartpm.backend.vault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente envia al crear o actualizar una credencial.
 * La contrasena llega en texto plano por HTTPS y se cifra en el servidor
 * antes de guardarla; nunca se persiste tal cual.
 */
public record CredencialRequest(

        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 150, message = "El titulo no puede exceder 150 caracteres")
        String titulo,

        @Size(max = 255)
        String usuarioCuenta,

        @Size(max = 255)
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        String password,

        @Size(max = 500)
        String url,

        String notas
) {
}
