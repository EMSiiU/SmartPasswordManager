package com.smartpm.backend.user.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

/**
 * Entidad que representa a un usuario en la base de datos.
 * Cada campo @Column mapea a una columna de la tabla "usuarios"
 * que creamos en la migracion V1.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 255)
    private String correo;

    // Aqui se guarda el HASH de la contrasena (BCrypt), nunca el texto plano.
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.USER;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    // JPA requiere un constructor sin argumentos.
    protected Usuario() {
    }

    public Usuario(String nombre, String correo, String passwordHash) {
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = Rol.USER;
    }

    /**
     * Se ejecuta justo antes de insertar el registro:
     * fija la fecha de creacion en el servidor.
     */
    @PrePersist
    void onCreate() {
        this.fechaCreacion = OffsetDateTime.now();
    }

    // ---- Getters y setters ----
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
}
