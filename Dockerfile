# Stage 1: Build the JAR file
FROM maven:3.9.9-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies first to leverage Docker caching
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the lightweight runtime image
FROM eclipse-temurin:25-jre
WORKDIR /app
# Copy the compiled JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
