package com.smartpm.backend.vault.controller;

import com.smartpm.backend.vault.dto.CredencialDetalleResponse;
import com.smartpm.backend.vault.dto.CredencialRequest;
import com.smartpm.backend.vault.dto.CredencialResumenResponse;
import com.smartpm.backend.vault.service.VaultService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de la boveda. Todos requieren JWT (la ruta /vault no es publica).
 * El usuario autenticado se obtiene de authentication.getName() (su correo),
 * que el JwtAuthFilter coloco a partir del token. El cliente NUNCA envia
 * su id de usuario: se deduce del token, asi no puede falsificarlo.
 */
@RestController
@RequestMapping("/vault")
public class VaultController {

    private final VaultService vaultService;

    public VaultController(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    /** GET /vault -> lista las credenciales del usuario (sin contrasenas). */
    @GetMapping
    public List<CredencialResumenResponse> listar(Authentication auth) {
        return vaultService.listar(auth.getName());
    }

    /** GET /vault/{id} -> detalle de una credencial (con contrasena descifrada). */
    @GetMapping("/{id}")
    public CredencialDetalleResponse ver(Authentication auth, @PathVariable Long id) {
        return vaultService.verDetalle(auth.getName(), id);
    }

    /** POST /vault -> crea una credencial. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CredencialDetalleResponse crear(Authentication auth,
                                           @Valid @RequestBody CredencialRequest req) {
        return vaultService.crear(auth.getName(), req);
    }

    /** PUT /vault/{id} -> actualiza una credencial. */
    @PutMapping("/{id}")
    public CredencialDetalleResponse actualizar(Authentication auth,
                                                @PathVariable Long id,
                                                @Valid @RequestBody CredencialRequest req) {
        return vaultService.actualizar(auth.getName(), id, req);
    }

    /** DELETE /vault/{id} -> borra una credencial. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(Authentication auth, @PathVariable Long id) {
        vaultService.borrar(auth.getName(), id);
    }
}
