-- ============================================================
--  V2: Tabla de credenciales (la boveda)
-- ============================================================

CREATE TABLE credenciales (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Dueno de la credencial. ON DELETE CASCADE: si se borra el usuario,
    -- se borran automaticamente sus credenciales.
    usuario_id         BIGINT NOT NULL
                       REFERENCES usuarios (id) ON DELETE CASCADE,

    titulo             VARCHAR(150)  NOT NULL,
    usuario_cuenta     VARCHAR(255),          -- el "usuario" del sitio (login)
    email              VARCHAR(255),
    -- AQUI va la contrasena CIFRADA (AES-GCM, en Base64). Nunca texto plano.
    password_encrypted TEXT          NOT NULL,
    url                VARCHAR(500),
    notas              TEXT,                  -- en claro en esta etapa

    fecha_creacion     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    fecha_modificacion TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Indice para listar rapido las credenciales de un usuario.
CREATE INDEX idx_credenciales_usuario ON credenciales (usuario_id);
