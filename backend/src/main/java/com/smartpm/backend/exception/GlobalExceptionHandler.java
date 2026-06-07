package com.smartpm.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Intercepta todas las excepciones que lanzan los controladores y
 * las convierte en una respuesta JSON uniforme (ApiError).
 *
 * Sin esto, Spring devuelve su propio formato de error que varía según
 * el tipo de excepción. Con esto, el cliente siempre recibe el mismo shape.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody: aplica a todos
 * los @RestController del proyecto y serializa la respuesta a JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Errores de validación: @Valid falla en algún campo del request body.
     * Ejemplo: contraseña menor de 8 caracteres → 400 con lista de errores.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ApiError.CampoError> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiError.CampoError(fe.getField(), mensajeDeError(fe)))
                .toList();

        ApiError error = ApiError.conCampos(
                HttpStatus.BAD_REQUEST.value(),
                "Solicitud inválida",
                "Uno o más campos no superaron la validación",
                errores
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * ResponseStatusException es lo que lanzamos nosotros manualmente en los servicios.
     * Ejemplo: "Credenciales inválidas" → 401, "El correo ya está registrado" → 409.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        ApiError error = ApiError.simple(
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    /**
     * Cualquier excepción no controlada → 500 genérico.
     * IMPORTANTE: nunca revelamos el stack trace al cliente (filtraría detalles internos).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ApiError error = ApiError.simple(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno",
                "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo."
        );
        return ResponseEntity.internalServerError().body(error);
    }

    private String mensajeDeError(FieldError fe) {
        return fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Campo inválido";
    }
}
