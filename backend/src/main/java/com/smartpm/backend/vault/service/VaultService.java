package com.smartpm.backend.vault.service;

import com.smartpm.backend.crypto.CryptoService;
import com.smartpm.backend.user.entity.Usuario;
import com.smartpm.backend.user.repository.UsuarioRepository;
import com.smartpm.backend.vault.dto.CredencialDetalleResponse;
import com.smartpm.backend.vault.dto.CredencialRequest;
import com.smartpm.backend.vault.dto.CredencialResumenResponse;
import com.smartpm.backend.vault.entity.Credencial;
import com.smartpm.backend.vault.repository.CredencialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Logica de la boveda. Aqui ocurren las dos cosas clave de la etapa:
 *  - El CIFRADO al guardar y el DESCIFRADO al leer el detalle.
 *  - El AISLAMIENTO por usuario: todas las operaciones se hacen contra
 *    el usuario autenticado, nunca contra ids ajenos.
 */
@Service
public class VaultService {

    private final CredencialRepository credencialRepository;
    private final UsuarioRepository usuarioRepository;
    private final CryptoService cryptoService;

    public VaultService(CredencialRepository credencialRepository,
                        UsuarioRepository usuarioRepository,
                        CryptoService cryptoService) {
        this.credencialRepository = credencialRepository;
        this.usuarioRepository = usuarioRepository;
        this.cryptoService = cryptoService;
    }

    /**
     * Traduce el correo del usuario autenticado (que viene del JWT) a su id.
     */
    private Long idDeUsuario(String correo) {
        Usuario u = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        return u.getId();
    }

    /** Lista las credenciales del usuario (sin contrasenas). */
    public List<CredencialResumenResponse> listar(String correo) {
        Long usuarioId = idDeUsuario(correo);
        return credencialRepository.findByUsuarioId(usuarioId).stream()
                .map(CredencialResumenResponse::desde)
                .toList();
    }

    /** Devuelve el detalle de UNA credencial, con la contrasena descifrada. */
    public CredencialDetalleResponse verDetalle(String correo, Long id) {
        Long usuarioId = idDeUsuario(correo);
        Credencial c = buscarPropia(id, usuarioId);

        String passwordPlano = cryptoService.descifrar(c.getPasswordEncrypted());

        return new CredencialDetalleResponse(
                c.getId(), c.getTitulo(), c.getUsuarioCuenta(),
                c.getEmail(), passwordPlano, c.getUrl(), c.getNotas());
    }

    /** Crea una credencial nueva, cifrando la contrasena. */
    public CredencialDetalleResponse crear(String correo, CredencialRequest req) {
        Long usuarioId = idDeUsuario(correo);

        Credencial c = new Credencial(
                usuarioId, req.titulo(),
                cryptoService.cifrar(req.password()));   // <-- cifrado aqui
        c.setUsuarioCuenta(req.usuarioCuenta());
        c.setEmail(req.email());
        c.setUrl(req.url());
        c.setNotas(req.notas());

        credencialRepository.save(c);
        return verComoDetalle(c, req.password());
    }

    /** Actualiza una credencial existente del usuario. */
    public CredencialDetalleResponse actualizar(String correo, Long id, CredencialRequest req) {
        Long usuarioId = idDeUsuario(correo);
        Credencial c = buscarPropia(id, usuarioId);

        c.setTitulo(req.titulo());
        c.setUsuarioCuenta(req.usuarioCuenta());
        c.setEmail(req.email());
        c.setPasswordEncrypted(cryptoService.cifrar(req.password())); // re-cifrar
        c.setUrl(req.url());
        c.setNotas(req.notas());

        credencialRepository.save(c);
        return verComoDetalle(c, req.password());
    }

    /** Borra una credencial del usuario. */
    public void borrar(String correo, Long id) {
        Long usuarioId = idDeUsuario(correo);
        Credencial c = buscarPropia(id, usuarioId);
        credencialRepository.delete(c);
    }

    // ---- Helpers privados ----

    /**
     * Busca una credencial EXIGIENDO que pertenezca al usuario.
     * Si no existe o es de otro, responde 404 (no revelamos que existe
     * una credencial ajena con ese id).
     */
    private Credencial buscarPropia(Long id, Long usuarioId) {
        return credencialRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Credencial no encontrada"));
    }

    private CredencialDetalleResponse verComoDetalle(Credencial c, String passwordPlano) {
        return new CredencialDetalleResponse(
                c.getId(), c.getTitulo(), c.getUsuarioCuenta(),
                c.getEmail(), passwordPlano, c.getUrl(), c.getNotas());
    }
}
