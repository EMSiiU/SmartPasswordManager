package com.smartpm.backend.config;

import com.smartpm.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracion central de seguridad.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * BCrypt para hashear contrasenas. Es un algoritmo lento a proposito
     * (resistente a fuerza bruta) y agrega "salt" automaticamente.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF: al usar JWT (sin cookies de sesion) no aplica
                // el ataque CSRF clasico. Lo revisaremos en la etapa de endurecimiento.
                .csrf(AbstractHttpConfigurer::disable)

                // Sin estado: el servidor no guarda sesiones. Cada peticion
                // se autentica por si misma con su token.
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reglas de acceso por ruta.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/health", "/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )

                // Insertamos nuestro filtro JWT antes del filtro estandar de login.
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
