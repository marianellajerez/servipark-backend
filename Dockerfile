# --- Etapa 1: Construcción (Build) ---
# Usamos una imagen de Gradle con JDK 17 (asegúrate que coincida con tu versión)
FROM gradle:8.5.0-jdk17 AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos los archivos de configuración y el wrapper de Gradle
# 'build.gradle' y 'settings.gradle' definen el proyecto y sus módulos
COPY build.gradle settings.gradle ./
# El wrapper ('gradlew') permite construir el proyecto sin instalar Gradle localmente
COPY gradlew ./
COPY gradle ./gradle

# Descargamos las dependencias PRIMERO
# Esto aprovecha el caché de capas de Docker. Si no cambian las dependencias,
# esta capa no se vuelve a ejecutar, haciendo builds futuros más rápidos.
RUN gradle dependencies

# Copiamos el resto del código fuente
COPY src ./src

# Construimos la aplicación y empaquetamos el .jar
# Saltamos los tests ('-x test') durante la construcción del Docker
RUN gradle build -x test

# --- Etapa 2: Ejecución (Run) ---
# Usamos una imagen ligera de Java 17 para ejecutar la app
FROM eclipse-temurin:17-jre-alpine

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el .jar construido desde la etapa anterior
# Gradle guarda los artefactos en 'build/libs/'
COPY --from=build /app/build/libs/servipark-backend-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]