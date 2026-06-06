package com.smartpm.backend.vault.repository;

import com.smartpm.backend.vault.entity.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de credenciales.
 *
 * IMPORTANTE (seguridad): los metodos de lectura por id SIEMPRE exigen
 * tambien el usuarioId. Asi es imposible que un usuario lea o modifique
 * una credencial de otro, aunque adivine el id. Nunca usamos findById
 * "a secas" para credenciales.
 */
public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    List<Credencial> findByUsuarioId(Long usuarioId);

    Optional<Credencial> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);
}
