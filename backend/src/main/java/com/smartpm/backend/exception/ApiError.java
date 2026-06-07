package com.smartpm.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Respuesta JSON unificada para todos los errores de la API.
 * Todos los endpoints usan este mismo formato, sin importar el tipo de error.
 *
 * @JsonInclude(NON_NULL) hace que "errores" no aparezca en el JSON
 * si es null (por ejemplo, en un 401 no hay errores de campo).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        List<CampoError> errores
) {
    /** Error de un campo específico tras una validación fallida. */
    public record CampoError(String campo, String mensaje) {}

    public static ApiError simple(int status, String error, String message) {
        return new ApiError(OffsetDateTime.now(), status, error, message, null);
    }

    public static ApiError conCampos(int status, String error, String message, List<CampoError> errores) {
        return new ApiError(OffsetDateTime.now(), status, error, message, errores);
    }
}
