# Etapa 1: compilación del microservicio con Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen liviana para ejecución en AWS ECS Fargate
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Usuario no-root requerido como buena práctica de seguridad
RUN addgroup -S techstore && adduser -S techstore -G techstore

COPY --from=build /build/target/techstore-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

USER techstore

ENTRYPOINT ["java", "-jar", "app.jar"]
