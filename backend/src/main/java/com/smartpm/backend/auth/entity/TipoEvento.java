package com.smartpm.backend.auth.entity;

/**
 * Eventos de autenticación que se registran en la tabla intentos_acceso.
 */
public enum TipoEvento {
    LOGIN_OK,
    LOGIN_FAIL,
    REGISTER_OK,
    TOKEN_REFRESH,
    LOGOUT
}
