package com.smartpm.backend.crypto;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica las tres propiedades de seguridad del cifrado AES-GCM.
 * Genera su propia clave de prueba (no usa la del entorno).
 */
class CryptoServiceTest {

    // Clave de 32 bytes en Base64, solo para pruebas.
    private static final String CLAVE_TEST =
            Base64.getEncoder().encodeToString(new byte[32]); // 32 bytes (ceros) -> valida en tamano

    private final CryptoService crypto = new CryptoService(CLAVE_TEST);

    @Test
    void cifrarYDescifrarDevuelveElTextoOriginal() {
        String original = "MiContrasenaSecreta123!";
        String cifrado = crypto.cifrar(original);
        String descifrado = crypto.descifrar(cifrado);
        assertEquals(original, descifrado);
    }

    @Test
    void cifrarDosVecesProduceResultadosDistintos() {
        // Por el IV aleatorio, el mismo texto cifrado dos veces NO debe coincidir.
        String original = "texto repetido";
        String a = crypto.cifrar(original);
        String b = crypto.cifrar(original);
        assertNotEquals(a, b, "Dos cifrados del mismo texto no deben ser iguales (IV unico)");
        // Pero ambos descifran al mismo original.
        assertEquals(original, crypto.descifrar(a));
        assertEquals(original, crypto.descifrar(b));
    }

    @Test
    void datoAlteradoFallaAlDescifrar() {
        // Si manipulamos el dato cifrado, GCM debe rechazarlo (integridad).
        String cifrado = crypto.cifrar("dato importante");
        byte[] bytes = Base64.getDecoder().decode(cifrado);
        bytes[bytes.length - 1] ^= 0x01; // alteramos un bit del final
        String manipulado = Base64.getEncoder().encodeToString(bytes);

        assertThrows(RuntimeException.class, () -> crypto.descifrar(manipulado));
    }

    @Test
    void claveDeTamanoIncorrectoEsRechazada() {
        String claveCorta = Base64.getEncoder().encodeToString(new byte[16]); // 16 bytes
        assertThrows(IllegalStateException.class, () -> new CryptoService(claveCorta));
    }
}
