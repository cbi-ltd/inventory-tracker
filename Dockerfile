# Stage 1: Build the JAR
FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root application user
RUN groupadd --system spring \
    && useradd --system --gid spring --uid 10001 spring

COPY --from=builder --chown=10001:10001 /app/target/*.jar app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
