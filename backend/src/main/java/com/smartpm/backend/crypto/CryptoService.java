package com.smartpm.backend.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifra y descifra textos con AES-256 en modo GCM.
 *
 * Modelo mental (la caja fuerte):
 *  - La LLAVE (clave de 32 bytes) cierra y abre. Vive en el entorno, nunca en el codigo.
 *  - El IV es un valor ALEATORIO y UNICO por cada cifrado. NO es secreto:
 *    se guarda junto al texto cifrado para poder descifrar despues.
 *  - GCM agrega un TAG de autenticacion: si alguien altera el dato cifrado,
 *    el descifrado FALLA con excepcion en vez de devolver datos corruptos.
 *
 * Formato que se almacena en la BD:
 *    Base64( IV[12 bytes] + textoCifrado+tag )
 */
@Service
public class CryptoService {

    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int IV_LONGITUD_BYTES = 12;    // 96 bits, recomendado para GCM
    private static final int TAG_LONGITUD_BITS = 128;   // tamano del tag de autenticacion
    private static final int CLAVE_LONGITUD_BYTES = 32; // AES-256

    private final SecretKeySpec llave;
    private final SecureRandom random = new SecureRandom();

    /**
     * La clave llega en Base64 desde el entorno y debe decodificar
     * a exactamente 32 bytes. Si no, fallamos al arrancar (mejor que
     * descubrirlo en tiempo de ejecucion).
     */
    public CryptoService(@Value("${app.crypto.secret}") String claveBase64) {
        byte[] claveBytes = Base64.getDecoder().decode(claveBase64);
        if (claveBytes.length != CLAVE_LONGITUD_BYTES) {
            throw new IllegalStateException(
                    "La clave de cifrado debe decodificar a 32 bytes (AES-256). " +
                    "Genera una valida con:  openssl rand -base64 32");
        }
        this.llave = new SecretKeySpec(claveBytes, "AES");
    }

    /**
     * Cifra un texto plano y devuelve un Base64 listo para guardar en la BD.
     */
    public String cifrar(String textoPlano) {
        try {
            // 1. IV aleatorio NUEVO en cada cifrado (con SecureRandom, no Random).
            byte[] iv = new byte[IV_LONGITUD_BYTES];
            random.nextBytes(iv);

            // 2. Inicializar el cipher en modo cifrado.
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, llave,
                    new GCMParameterSpec(TAG_LONGITUD_BITS, iv));

            // 3. Cifrar (el tag de autenticacion queda incluido al final).
            byte[] cifrado = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));

            // 4. Concatenar IV + cifrado y codificar en Base64.
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cifrado.length);
            buffer.put(iv);
            buffer.put(cifrado);
            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            // No incluimos el texto plano en el mensaje, por seguridad.
            throw new RuntimeException("Error al cifrar el dato", e);
        }
    }

    /**
     * Descifra un Base64 producido por cifrar().
     * Si el dato fue manipulado, GCM lanza excepcion (verificacion de integridad).
     */
    public String descifrar(String almacenadoBase64) {
        try {
            byte[] todo = Base64.getDecoder().decode(almacenadoBase64);

            // 1. Separar IV (primeros 12 bytes) del resto (cifrado + tag).
            ByteBuffer buffer = ByteBuffer.wrap(todo);
            byte[] iv = new byte[IV_LONGITUD_BYTES];
            buffer.get(iv);
            byte[] cifrado = new byte[buffer.remaining()];
            buffer.get(cifrado);

            // 2. Inicializar el cipher en modo descifrado con ese MISMO IV.
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, llave,
                    new GCMParameterSpec(TAG_LONGITUD_BITS, iv));

            // 3. Descifrar. Si el tag no cuadra, salta excepcion aqui.
            byte[] plano = cipher.doFinal(cifrado);
            return new String(plano, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar el dato", e);
        }
    }
}
