package com.smartpm.backend.auth.entity;

import com.smartpm.backend.user.entity.Usuario;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;

    // ManyToOne: muchos tokens pueden pertenecer a un usuario (multidispositivo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;

    @Column(nullable = false)
    private boolean revocado = false;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @PrePersist
    void onCreate() {
        this.fechaCreacion = OffsetDateTime.now();
    }

    // ---- Getters y setters ----
    public Long getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public OffsetDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(OffsetDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public boolean isRevocado() { return revocado; }
    public void setRevocado(boolean revocado) { this.revocado = revocado; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
}
