package com.smartpm.backend.auth.repository;

import com.smartpm.backend.auth.entity.IntentoAcceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntentoAccesoRepository extends JpaRepository<IntentoAcceso, Long> {

    // Últimos N intentos de un usuario (para mostrar en el dashboard de seguridad en Etapa 6)
    List<IntentoAcceso> findTop20ByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
