# Smart Password Manager

Gestor de contraseñas con cifrado en reposo. Proyecto personal de aprendizaje
(arquitectura, seguridad, full-stack y DevOps).

> Nota sobre el modelo de seguridad: esta versión usa **cifrado en servidor**
> (las credenciales se guardan cifradas en la base de datos). No es Zero
> Knowledge: el servidor tiene la capacidad técnica de descifrarlas. El modelo
> Zero Knowledge (cifrado en el cliente) queda como posible evolución futura.

## Stack (Etapa 0)

- **Backend:** Spring Boot 4.0.6 (Java 21)
- **Base de datos:** PostgreSQL 17
- **Infraestructura:** Docker + Docker Compose

## Requisitos previos

- Docker y Docker Compose instalados.
- (Opcional, para correr el backend sin Docker) JDK 21 y Maven.

## Cómo arrancar

1. Copia la plantilla de variables de entorno y edítala:

   ```bash
   cp .env.example .env
   # edita .env y pon una contraseña fuerte en DB_PASSWORD
   ```

2. Levanta todo con Docker Compose:

   ```bash
   docker compose up --build
   ```

3. Verifica que el backend responde:

   ```bash
   curl http://localhost:8080/health
   ```

   Deberías ver algo como:

   ```json
   {"status":"UP","service":"smart-password-manager","timestamp":"..."}
   ```

   También está disponible el health check de Spring Actuator:

   ```bash
   curl http://localhost:8080/actuator/health
   ```

## Estructura del proyecto

```
.
├── docker-compose.yml      # Orquesta base de datos + backend
├── .env.example            # Plantilla de variables (SÍ va a Git)
├── .env                    # Secretos reales (NO va a Git)
├── .gitignore
└── backend/
    ├── Dockerfile          # Imagen del backend (multi-stage)
    ├── pom.xml             # Dependencias Maven
    └── src/
        ├── main/java/com/smartpm/backend/
        │   ├── BackendApplication.java
        │   └── health/HealthController.java
        ├── main/resources/application.properties
        └── test/java/com/smartpm/backend/HealthControllerTest.java
```

## Estado

- [x] Etapa 0 — Fundación (repo, Docker Compose, esqueleto, /health)
- [ ] Etapa 1 — Autenticación
- [ ] Etapa 2 — Bóveda (CRUD cifrado)
- [ ] Etapa 3 — Frontend
- [ ] Etapa 4 — Endurecimiento
```
