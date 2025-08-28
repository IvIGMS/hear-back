# ====== Build stage ======
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependencias
COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:go-offline

# Compilar
COPY src ./src
RUN mvn -q -e -B -DskipTests package

# ====== Runtime stage ======
FROM eclipse-temurin:21-jre

# Usuario no root (opcional recomendado)
RUN useradd -m spring
USER spring

WORKDIR /app
# Copiamos el jar empacado por Spring Boot (target/*.jar)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
