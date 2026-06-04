package com.smartpm.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Encargado de crear y verificar JSON Web Tokens.
 *
 * Recuerda el modelo mental: el token es un "boleto con holograma".
 * - Lo FIRMAMOS con una clave secreta (la que solo conoce el servidor).
 * - Al recibirlo, VERIFICAMOS esa firma para confiar en su contenido.
 * - El payload NO esta cifrado: nunca metemos datos sensibles aqui.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    /**
     * El secreto y la expiracion vienen de variables de entorno
     * (definidas en application.properties -> .env). Nunca hardcodeados.
     *
     * @param secret   clave secreta de firma (minimo 32 bytes para HS256)
     * @param expirationMillis tiempo de vida del token en milisegundos
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMillis
    ) {
        // Convertimos el texto secreto en una clave criptografica para HMAC-SHA.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    /**
     * Genera un token firmado para un usuario.
     *
     * @param correo identificador del usuario (va en el "subject")
     * @param rol    rol del usuario (claim personalizado, para autorizacion)
     */
    public String generarToken(String correo, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(correo)            // quien es el usuario
                .claim("rol", rol)          // que rol tiene
                .issuedAt(ahora)            // cuando se emitio
                .expiration(expira)         // cuando caduca
                .signWith(signingKey)       // el "holograma"
                .compact();                 // produce el string final
    }

    /**
     * Verifica la firma y la expiracion, y devuelve el correo del token.
     * Si el token es invalido o expiro, jjwt lanza una excepcion.
     */
    public String extraerCorreo(String token) {
        return parsearClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return parsearClaims(token).get("rol", String.class);
    }

    /**
     * Devuelve true si el token es valido (firma correcta y no expirado).
     */
    public boolean esValido(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)     // verifica la firma con nuestra clave
                .build()
                .parseSignedClaims(token)   // lanza excepcion si algo falla
                .getPayload();
    }
}
