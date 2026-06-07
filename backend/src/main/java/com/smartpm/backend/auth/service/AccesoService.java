package com.smartpm.backend.auth.service;

import com.smartpm.backend.auth.entity.IntentoAcceso;
import com.smartpm.backend.auth.entity.TipoEvento;
import com.smartpm.backend.auth.repository.IntentoAccesoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Registra eventos de autenticación en la tabla intentos_acceso.
 *
 * El método es @Async: no bloquea el hilo del request mientras se escribe en BD.
 * Para que funcione, necesitamos @EnableAsync en alguna clase @Configuration.
 * Lo añadimos en BackendApplication.
 */
@Service
public class AccesoService {

    private final IntentoAccesoRepository repo;

    public AccesoService(IntentoAccesoRepository repo) {
        this.repo = repo;
    }

    @Async
    public void registrar(Long usuarioId, String ip, String userAgent, TipoEvento evento) {
        IntentoAcceso intento = new IntentoAcceso();
        intento.setUsuarioId(usuarioId);
        intento.setIp(ip != null ? ip : "desconocida");
        // Truncamos user-agent para no exceder los 500 chars de la columna
        if (userAgent != null) {
            intento.setUserAgent(userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent);
        }
        intento.setEvento(evento);
        repo.save(intento);
    }
}
