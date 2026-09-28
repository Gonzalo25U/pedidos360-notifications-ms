# --- Etapa 1: build ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Etapa 2: runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8084

# Variables de entorno requeridas en runtime:
# AZURE_CLIENT_ID, FRONTEND_URL, RABBITMQ_HOST, RABBITMQ_PASSWORD,
# GMAIL_USERNAME, GMAIL_APP_PASSWORD
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
