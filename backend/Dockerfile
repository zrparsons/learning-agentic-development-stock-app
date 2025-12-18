# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle.kts ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application (creates fat JAR)
RUN gradle shadowJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built fat JAR from build stage
COPY --from=build /app/build/libs/app.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables (these will be overridden by Kubernetes)
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=stockapp
ENV DB_USER=stockapp
ENV DB_PASSWORD=stockapp
ENV JWT_SECRET=your-secret-key-change-in-production

# Run the application
CMD ["java", "-jar", "app.jar"]
