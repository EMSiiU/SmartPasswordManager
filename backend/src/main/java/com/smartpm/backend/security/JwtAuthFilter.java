package com.smartpm.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro que se ejecuta UNA vez por peticion (antes del controlador).
 *
 * Su trabajo:
 *   1. Buscar la cabecera "Authorization: Bearer <token>".
 *   2. Si existe y el token es valido, marcar la peticion como autenticada
 *      en el contexto de seguridad de Spring.
 *   3. Si no, no hace nada: la peticion sigue como anonima y Spring
 *      decidira despues si la ruta requiere autenticacion.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String header = request.getHeader(HEADER);

        // Sin cabecera Bearer -> dejamos pasar como anonimo.
        if (header == null || !header.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Quitamos el prefijo "Bearer " para quedarnos con el token.
        final String token = header.substring(PREFIX.length());

        if (jwtService.esValido(token)) {
            String correo = jwtService.extraerCorreo(token);
            String rol = jwtService.extraerRol(token);

            // En Spring Security los roles se nombran con el prefijo "ROLE_".
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));

            var authentication = new UsernamePasswordAuthenticationToken(
                    correo,        // "principal": quien es
                    null,          // credenciales: ya no necesitamos la password
                    authorities    // sus permisos
            );
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Marcamos la peticion como autenticada.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
