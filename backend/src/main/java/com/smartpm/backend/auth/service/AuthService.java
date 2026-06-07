package com.smartpm.backend.auth.service;

import com.smartpm.backend.auth.dto.HttpRequestInfo;
import com.smartpm.backend.auth.dto.LoginRequest;
import com.smartpm.backend.auth.dto.LoginResult;
import com.smartpm.backend.auth.dto.RegistroRequest;
import com.smartpm.backend.auth.entity.RefreshToken;
import com.smartpm.backend.auth.entity.TipoEvento;
import com.smartpm.backend.security.JwtService;
import com.smartpm.backend.user.entity.Usuario;
import com.smartpm.backend.user.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AccesoService accesoService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AccesoService accesoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.accesoService = accesoService;
    }

    /**
     * Registra un usuario nuevo y devuelve access token + refresh token.
     */
    public LoginResult registrar(RegistroRequest req, HttpRequestInfo requestInfo) {
        if (usuarioRepository.existsByCorreo(req.correo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        String hash = passwordEncoder.encode(req.password());
        Usuario usuario = new Usuario(req.nombre(), req.correo(), hash);
        usuarioRepository.save(usuario);

        String accessToken = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().name());
        String refreshToken = refreshTokenService.crear(usuario.getId());

        accesoService.registrar(usuario.getId(), requestInfo.ip(), requestInfo.userAgent(), TipoEvento.REGISTER_OK);

        return new LoginResult(accessToken, refreshToken);
    }

    /**
     * Valida credenciales y devuelve access token + refresh token.
     *
     * SEGURIDAD: el mensaje de error es idéntico tanto si el correo no existe
     * como si la contraseña es incorrecta. Así no revelamos qué campo falló
     * (técnica de "username enumeration" que explotan los atacantes).
     */
    public LoginResult login(LoginRequest req, HttpRequestInfo requestInfo) {
        Optional<Usuario> optUsuario = usuarioRepository.findByCorreo(req.correo());

        // Verificamos contraseña solo si el usuario existe, pero el mensaje de error es el mismo
        if (optUsuario.isEmpty() ||
                !passwordEncoder.matches(req.password(), optUsuario.get().getPasswordHash())) {

            Long usuarioId = optUsuario.map(Usuario::getId).orElse(null);
            accesoService.registrar(usuarioId, requestInfo.ip(), requestInfo.userAgent(), TipoEvento.LOGIN_FAIL);

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        Usuario usuario = optUsuario.get();
        String accessToken = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().name());
        String refreshToken = refreshTokenService.crear(usuario.getId());

        accesoService.registrar(usuario.getId(), requestInfo.ip(), requestInfo.userAgent(), TipoEvento.LOGIN_OK);

        return new LoginResult(accessToken, refreshToken);
    }

    /**
     * Valida el refresh token, genera un nuevo access token y rota el refresh token.
     * El refresh token viejo queda inmediatamente inválido.
     */
    public LoginResult refresh(String refreshTokenStr, HttpRequestInfo requestInfo) {
        RefreshToken rt = refreshTokenService.validar(refreshTokenStr);
        Usuario usuario = rt.getUsuario();

        String newAccessToken = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().name());
        String newRefreshToken = refreshTokenService.rotar(rt);

        accesoService.registrar(usuario.getId(), requestInfo.ip(), requestInfo.userAgent(), TipoEvento.TOKEN_REFRESH);

        return new LoginResult(newAccessToken, newRefreshToken);
    }

    /**
     * Revoca el refresh token del dispositivo actual.
     * Si el token no existe (ya revocado, o nunca existió), no hace nada.
     */
    public void logout(String refreshTokenStr, HttpRequestInfo requestInfo) {
        // Intentamos obtener el usuario para registrar el evento antes de revocar
        refreshTokenService.revocar(refreshTokenStr);
        // El acceso de logout queda fuera del log para no revelar qué tokens existen.
        // En una Etapa 6 (auditoría completa) podríamos añadirlo.
    }
}
