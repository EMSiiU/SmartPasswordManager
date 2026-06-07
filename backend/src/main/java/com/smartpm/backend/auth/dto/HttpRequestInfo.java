package com.smartpm.backend.auth.dto;

/**
 * Datos HTTP relevantes para el historial de accesos.
 * El controlador los extrae del HttpServletRequest y los pasa al servicio,
 * manteniendo al servicio libre de dependencias del servlet.
 */
public record HttpRequestInfo(String ip, String userAgent) {}
