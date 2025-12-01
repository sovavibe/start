# Multi-stage Dockerfile for Jmix/Vaadin application
# Stage 1: Build
FROM gradle:8.11-jdk21-alpine AS build

WORKDIR /app

# Copy Gradle files for dependency caching
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle gradle.properties ./

# Download dependencies (cached layer if build files don't change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src/ src/

# Build application with production mode for Vaadin
RUN ./gradlew -Pvaadin.productionMode=true bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Install wget for health check
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]

