package com.smartpm.backend.auth.repository;

import com.smartpm.backend.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // Revoca todos los tokens activos de un usuario (útil en logout global o detección de robo)
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revocado = true WHERE rt.usuario.id = :usuarioId AND rt.revocado = false")
    void revocarTodosPorUsuario(Long usuarioId);

    // Limpieza periódica: borra tokens ya expirados o revocados (para no acumular basura)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.fechaExpiracion < :antes OR rt.revocado = true")
    void eliminarExpiradosYRevocados(OffsetDateTime antes);
}
