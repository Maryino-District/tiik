FROM gradle:8.14.3 AS build

WORKDIR /app

# Copy Gradle configuration files
COPY gradle ./gradle
COPY settings.gradle.kts ./
COPY build.gradle.kts ./
COPY gradle.properties ./

# Copy modules with correct structure
COPY shared ./shared
COPY server ./server

# Build the application
RUN gradle :server:installDist --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built application from the correct path
COPY --from=build /app/server/build/install/server ./app

EXPOSE 8080

ENV PORT=8080

# Run the application
CMD ["./app/bin/server"]