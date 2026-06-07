-- Refresh tokens de larga duración.
-- Cada fila es un token activo (o revocado) vinculado a un usuario.
--
-- Por qué 'token' es VARCHAR(36): los tokens son UUIDs
-- (ej. "550e8400-e29b-41d4-a716-446655440000"), que ocupan exactamente 36 chars.
--
-- La columna 'revocado' permite invalidar un token antes de que expire naturalmente:
-- al hacer logout, al detectar posible robo, o al cambiar la contraseña.

CREATE TABLE refresh_tokens (
    id               BIGSERIAL    PRIMARY KEY,
    token            VARCHAR(36)  NOT NULL UNIQUE,
    usuario_id       BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha_expiracion TIMESTAMPTZ  NOT NULL,
    revocado         BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_creacion   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Índice en 'token': todas las consultas buscan por este campo
CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens(token);
-- Índice en usuario_id: útil para revocar todos los tokens de un usuario
CREATE INDEX idx_refresh_tokens_usuario ON refresh_tokens(usuario_id);
