# ---- Etapa 1: compilacion ----
# Usamos una imagen con Maven + JDK 21 para construir el .jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos primero el pom para aprovechar la cache de capas de Docker:
# si el pom no cambia, no se vuelven a descargar las dependencias.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Ahora el codigo fuente y empaquetado
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Etapa 2: ejecucion ----
# Imagen ligera, solo con el runtime de Java, sin Maven ni codigo fuente.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear un usuario sin privilegios (principio de minimo privilegio).
RUN addgroup -S spm && adduser -S spm -G spm
USER spm

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
