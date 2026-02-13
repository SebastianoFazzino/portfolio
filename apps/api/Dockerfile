# ---------- Build stage ----------
FROM gradle:9.1.0-jdk25 AS builder
WORKDIR /app

COPY . .
RUN gradle build -x test --no-daemon

# ---------- Runtime stage ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 9909
ENTRYPOINT ["java", "-jar", "app.jar"]