package com.smartpm.backend.auth.controller;

import com.smartpm.backend.auth.dto.AuthResponse;
import com.smartpm.backend.auth.dto.HttpRequestInfo;
import com.smartpm.backend.auth.dto.LoginRequest;
import com.smartpm.backend.auth.dto.LoginResult;
import com.smartpm.backend.auth.dto.RegistroRequest;
import com.smartpm.backend.auth.service.AuthService;
import com.smartpm.backend.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refreshToken";

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    /** POST /auth/register → crea usuario, devuelve access token + cookie con refresh token. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(
            @Valid @RequestBody RegistroRequest req,
            HttpServletRequest servletReq,
            HttpServletResponse servletRes) {

        LoginResult result = authService.registrar(req, extractRequestInfo(servletReq));
        setRefreshCookie(servletRes, result.refreshToken());
        return AuthResponse.bearer(result.accessToken());
    }

    /** POST /auth/login → valida credenciales, devuelve access token + cookie con refresh token. */
    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest servletReq,
            HttpServletResponse servletRes) {

        LoginResult result = authService.login(req, extractRequestInfo(servletReq));
        setRefreshCookie(servletRes, result.refreshToken());
        return AuthResponse.bearer(result.accessToken());
    }

    /**
     * POST /auth/refresh → intercambia el refresh token (cookie) por un nuevo access token.
     * El navegador envía la cookie automáticamente; el cliente JS no la ve ni la maneja.
     *
     * Si no hay cookie → 401.
     * Respuesta: access token en body + nueva cookie con refresh token rotado.
     */
    @PostMapping("/refresh")
    public AuthResponse refresh(
            HttpServletRequest servletReq,
            HttpServletResponse servletRes) {

        String refreshToken = extractRefreshCookie(servletReq);
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay sesión activa");
        }

        LoginResult result = authService.refresh(refreshToken, extractRequestInfo(servletReq));
        setRefreshCookie(servletRes, result.refreshToken());
        return AuthResponse.bearer(result.accessToken());
    }

    /**
     * POST /auth/logout → revoca el refresh token y borra la cookie.
     * No requiere access token válido: si el access token ya expiró,
     * el usuario aún debe poder cerrar sesión.
     */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest servletReq, HttpServletResponse servletRes) {
        String refreshToken = extractRefreshCookie(servletReq);
        if (refreshToken != null) {
            authService.logout(refreshToken, extractRequestInfo(servletReq));
        }
        clearRefreshCookie(servletRes);
    }

    /** GET /auth/me → ruta protegida de prueba. */
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        return Map.of(
                "correo", authentication.getName(),
                "roles", authentication.getAuthorities()
        );
    }

    // ─── Helpers de cookie ───────────────────────────────────────────────────

    /**
     * Establece la cookie HttpOnly con el refresh token.
     *
     * HttpOnly: JavaScript no puede leerla (protección XSS).
     * SameSite=Lax: el navegador no la envía en peticiones cross-site POST
     *               (mitigación de CSRF sin necesitar un token adicional).
     * Path=/: necesario en dev con Vite proxy. El browser ve las rutas como
     *         /api/auth/..., no /auth/..., así que Path=/auth no haría match.
     *         En producción con nginx proxy_pass, evaluar restringir a /auth.
     * Secure: DESACTIVADO en dev (HTTP). En producción activar (HTTPS obligatorio).
     */
    private void setRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, token)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(refreshTokenService.getExpirationDays()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /** Borra la cookie enviando la misma con maxAge=0. */
    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /** Lee el valor del refresh token de las cookies del request. Null si no existe. */
    private String extractRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (REFRESH_COOKIE.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    /** Extrae IP y User-Agent del request para el historial de accesos. */
    private HttpRequestInfo extractRequestInfo(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");
        return new HttpRequestInfo(ip, userAgent);
    }
}
