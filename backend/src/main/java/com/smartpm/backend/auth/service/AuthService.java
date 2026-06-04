package com.smartpm.backend.auth.service;

import com.smartpm.backend.auth.dto.AuthResponse;
import com.smartpm.backend.auth.dto.LoginRequest;
import com.smartpm.backend.auth.dto.RegistroRequest;
import com.smartpm.backend.security.JwtService;
import com.smartpm.backend.user.entity.Usuario;
import com.smartpm.backend.user.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Logica de negocio de autenticacion.
 * El controlador solo recibe peticiones; aqui ocurre el trabajo real.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un usuario nuevo.
     * - Verifica que el correo no exista.
     * - Hashea la contrasena con BCrypt (NUNCA se guarda en texto plano).
     * - Devuelve un token para que quede logueado de una vez.
     */
    public AuthResponse registrar(RegistroRequest req) {
        if (usuarioRepository.existsByCorreo(req.correo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo ya esta registrado");
        }

        String hash = passwordEncoder.encode(req.password());
        Usuario usuario = new Usuario(req.nombre(), req.correo(), hash);
        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(
                usuario.getCorreo(), usuario.getRol().name());
        return AuthResponse.bearer(token);
    }

    /**
     * Inicia sesion.
     * - Busca el usuario por correo.
     * - Compara la contrasena recibida contra el hash con BCrypt.
     * - Importante: el mensaje de error es el mismo si falla el correo
     *   o la contrasena, para no revelar cual de los dos existe.
     */
    public AuthResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByCorreo(req.correo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));

        if (!passwordEncoder.matches(req.password(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }

        String token = jwtService.generarToken(
                usuario.getCorreo(), usuario.getRol().name());
        return AuthResponse.bearer(token);
    }
}
