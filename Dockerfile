# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Cache dependencies first: copy only what the resolver needs.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q dependency:go-offline

# Build the application (skip tests here; CI runs them separately).
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Run as an unprivileged user.
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /workspace/target/app.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
