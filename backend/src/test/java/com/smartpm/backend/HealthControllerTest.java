package com.smartpm.backend;

import com.smartpm.backend.health.HealthController;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Prueba unitaria del HealthController.
 * No levanta el contexto completo de Spring ni necesita base de datos:
 * simplemente verifica que el metodo devuelve lo esperado.
 */
class HealthControllerTest {

    @Test
    void healthDevuelveEstadoUp() {
        HealthController controller = new HealthController();
        Map<String, Object> result = controller.health();

        assertEquals("UP", result.get("status"));
        assertEquals("smart-password-manager", result.get("service"));
        assertNotNull(result.get("timestamp"));
    }
}
