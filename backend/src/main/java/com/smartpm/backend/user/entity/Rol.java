package com.smartpm.backend.user.entity;

/**
 * Roles posibles de un usuario.
 * USER: usuario normal, accede solo a su propia boveda.
 * ADMIN: privilegios administrativos (gestion de usuarios, etc.).
 */
public enum Rol {
    USER,
    ADMIN
}
