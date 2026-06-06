package com.smartpm.backend.vault.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

/**
 * Una credencial guardada en la boveda de un usuario.
 * El campo passwordEncrypted guarda SIEMPRE el texto cifrado (AES-GCM),
 * nunca la contrasena en claro.
 */
@Entity
@Table(name = "credenciales")
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guardamos solo el id del dueno (relacion simple para esta etapa).
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(name = "usuario_cuenta", length = 255)
    private String usuarioCuenta;

    @Column(length = 255)
    private String email;

    @Column(name = "password_encrypted", nullable = false, columnDefinition = "TEXT")
    private String passwordEncrypted;

    @Column(length = 500)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private OffsetDateTime fechaModificacion;

    protected Credencial() {
    }

    public Credencial(Long usuarioId, String titulo, String passwordEncrypted) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.passwordEncrypted = passwordEncrypted;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime ahora = OffsetDateTime.now();
        this.fechaCreacion = ahora;
        this.fechaModificacion = ahora;
    }

    @PreUpdate
    void onUpdate() {
        this.fechaModificacion = OffsetDateTime.now();
    }

    // ---- Getters y setters ----
    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getUsuarioCuenta() { return usuarioCuenta; }
    public void setUsuarioCuenta(String usuarioCuenta) { this.usuarioCuenta = usuarioCuenta; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordEncrypted() { return passwordEncrypted; }
    public void setPasswordEncrypted(String passwordEncrypted) { this.passwordEncrypted = passwordEncrypted; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public OffsetDateTime getFechaModificacion() { return fechaModificacion; }
}
