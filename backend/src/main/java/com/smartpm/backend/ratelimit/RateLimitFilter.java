package com.smartpm.backend.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Limita los intentos por IP en endpoints sensibles para frenar ataques de fuerza bruta.
 *
 * Algoritmo: ventana deslizante. Guardamos los timestamps (ms) de los últimos intentos
 * de cada IP. En cada petición descartamos los que ya salieron de la ventana y
 * contamos los que quedan. Si superan el límite, devolvemos 429.
 *
 * Limitaciones (deuda técnica aceptada):
 * - In-memory: se reinicia con el servidor y no funciona en clústeres.
 * - Sin evicción: las IPs antiguas nunca se eliminan del mapa (memory leak en producción).
 * - Para producción: usar Bucket4j + Redis o similar.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMITE = 10;
    private static final long VENTANA_MS = 60_000L; // 1 minuto

    // Rutas a las que aplicamos rate limiting
    private static final Set<String> RUTAS_LIMITADAS = Set.of("/auth/login", "/auth/register");

    private final ConcurrentMap<String, Deque<Long>> ventanasPorIp = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        if (!RUTAS_LIMITADAS.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String ip = resolverIp(request);
        long ahora = System.currentTimeMillis();

        Deque<Long> ventana = ventanasPorIp.computeIfAbsent(ip, k -> new ArrayDeque<>());

        synchronized (ventana) {
            // Eliminar timestamps fuera de la ventana de 1 minuto
            while (!ventana.isEmpty() && ahora - ventana.peekFirst() > VENTANA_MS) {
                ventana.pollFirst();
            }

            if (ventana.size() >= LIMITE) {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        "{\"status\":429,\"error\":\"Too Many Requests\"," +
                        "\"message\":\"Demasiados intentos. Espera un minuto antes de intentar de nuevo.\"}"
                );
                return;
            }

            ventana.addLast(ahora);
        }

        chain.doFilter(request, response);
    }

    /**
     * Extrae la IP real del request.
     * X-Forwarded-For la pone un proxy/balanceador delante del servidor.
     * En dev directo, usamos getRemoteAddr().
     */
    private String resolverIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
