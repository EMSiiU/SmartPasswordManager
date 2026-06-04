-- ============================================================
--  V1: Creacion de la tabla de usuarios
-- ============================================================

CREATE TABLE usuarios (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    correo        VARCHAR(255)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    rol           VARCHAR(20)   NOT NULL DEFAULT 'USER',
    fecha_creacion TIMESTAMPTZ  NOT NULL DEFAULT now(),

    -- El correo debe ser unico: no dos cuentas con el mismo email.
    CONSTRAINT uq_usuarios_correo UNIQUE (correo)
);

-- Indice para acelerar la busqueda por correo (se usa en cada login).
CREATE INDEX idx_usuarios_correo ON usuarios (correo);
