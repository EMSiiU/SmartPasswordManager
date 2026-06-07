package com.smartpm.backend.auth.service;

import com.smartpm.backend.auth.entity.RefreshToken;
import com.smartpm.backend.auth.repository.RefreshTokenRepository;
import com.smartpm.backend.user.entity.Usuario;
import com.smartpm.backend.user.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UsuarioRepository usuarioRepository;

    @Value("${refresh.token.expiration-days:7}")
    private long expirationDays;

    public RefreshTokenService(RefreshTokenRepository repo, UsuarioRepository usuarioRepository) {
        this.repo = repo;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea un nuevo refresh token para el usuario y lo persiste en BD.
     * Genera un UUID aleatorio como valor del token (impredecible, único).
     */
    @Transactional
    public String crear(Long usuarioId) {
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);

        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUsuario(usuario);
        rt.setFechaExpiracion(OffsetDateTime.now().plusDays(expirationDays));
        rt.setRevocado(false);

        return repo.save(rt).getToken();
    }

    /**
     * Valida que el token exista, no esté revocado y no haya expirado.
     * Si el token está revocado, es señal de posible robo (alguien usó el token
     * viejo tras una rotación): revocamos todos los del usuario para forzar
     * un re-login completo.
     */
    @Transactional
    public RefreshToken validar(String tokenStr) {
        RefreshToken rt = repo.findByToken(tokenStr)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Sesión inválida. Inicia sesión de nuevo."));

        if (rt.isRevocado()) {
            // Posible reutilización de token robado: invalidamos todo por seguridad
            repo.revocarTodosPorUsuario(rt.getUsuario().getId());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Sesión inválida. Inicia sesión de nuevo.");
        }

        if (rt.getFechaExpiracion().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "La sesión ha expirado. Inicia sesión de nuevo.");
        }

        return rt;
    }

    /**
     * Rotación: invalida el token actual y crea uno nuevo.
     * El cliente recibirá el nuevo token en la cookie; el viejo queda inutilizable.
     */
    @Transactional
    public String rotar(RefreshToken viejo) {
        viejo.setRevocado(true);
        repo.save(viejo);
        return crear(viejo.getUsuario().getId());
    }

    /**
     * Logout: revoca un token específico (el del dispositivo actual).
     * Si el token no existe, no hacemos nada (operación idempotente).
     */
    @Transactional
    public void revocar(String tokenStr) {
        repo.findByToken(tokenStr).ifPresent(rt -> {
            rt.setRevocado(true);
            repo.save(rt);
        });
    }

    /**
     * Logout global: revoca todos los tokens del usuario (todos sus dispositivos).
     */
    @Transactional
    public void revocarTodos(Long usuarioId) {
        repo.revocarTodosPorUsuario(usuarioId);
    }

    public long getExpirationDays() {
        return expirationDays;
    }
}
