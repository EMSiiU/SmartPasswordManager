package com.smartpm.backend.auth.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "intentos_acceso")
public class IntentoAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nullable: un login fallido con email inexistente no tiene usuario
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, length = 45)
    private String ip;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEvento evento;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime fecha;

    @PrePersist
    void onCreate() {
        this.fecha = OffsetDateTime.now();
    }

    // ---- Getters y setters ----
    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public TipoEvento getEvento() { return evento; }
    public void setEvento(TipoEvento evento) { this.evento = evento; }
    public OffsetDateTime getFecha() { return fecha; }
}
