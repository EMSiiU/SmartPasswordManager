-- Historial de eventos de autenticación.
-- Registra cada intento de login, registro y uso de refresh token.
--
-- 'usuario_id' es nullable porque un intento de login fallido con un correo
-- inexistente no tiene usuario asociado — pero aún queremos registrar la IP.
--
-- 'evento' usa VARCHAR en vez de un tipo ENUM de Postgres para facilitar
-- las migraciones futuras (añadir un valor a un ENUM en Postgres requiere
-- más pasos que cambiar un string).

CREATE TABLE intentos_acceso (
    id          BIGSERIAL    PRIMARY KEY,
    usuario_id  BIGINT       REFERENCES usuarios(id) ON DELETE SET NULL,
    ip          VARCHAR(45)  NOT NULL,  -- IPv4 max 15 chars, IPv6 max 45 chars
    user_agent  VARCHAR(500),
    evento      VARCHAR(30)  NOT NULL,  -- LOGIN_OK, LOGIN_FAIL, REGISTER_OK, TOKEN_REFRESH, LOGOUT
    fecha       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_intentos_acceso_usuario ON intentos_acceso(usuario_id);
CREATE INDEX idx_intentos_acceso_fecha   ON intentos_acceso(fecha DESC);
