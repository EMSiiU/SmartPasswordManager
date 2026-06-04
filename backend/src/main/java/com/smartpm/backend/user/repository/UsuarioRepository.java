package com.smartpm.backend.user.repository;

import com.smartpm.backend.user.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos de Usuario.
 * Al extender JpaRepository, Spring Data implementa automaticamente
 * los metodos CRUD (save, findById, findAll, delete...).
 * Solo declaramos los metodos de busqueda personalizados.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring genera la consulta a partir del nombre del metodo:
    // "buscar un usuario cuyo campo correo sea igual a..."
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
