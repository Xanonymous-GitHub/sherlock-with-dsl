# syntax=docker/dockerfile:experimental

# For solving https://github.com/sass/dart-sass/issues/617, we shouldn't use alpine image.
FROM gradle:7-alpine AS build

# Set the working directory in the Docker image
WORKDIR /app

# Copy the Gradle configuration files
COPY --chown=gradle:gradle build.gradle settings.gradle web-test.gradle gradle.properties gradlew /app/
COPY --chown=gradle:gradle gradlew /app/gradlew

# Load all necessary Gradle dependencies (for caching purposes)
RUN gradle wrapper \
    && ./gradlew --no-daemon dependencies

# Copy the source code into the Docker image
COPY --chown=gradle:gradle src /app/src

# Build the application
RUN ./gradlew --no-daemon build

FROM alpine:latest AS final

# Install minimal JRE (Java 17) in the Alpine image
RUN apk --no-cache add openjdk17-jre

# Create a non-privileged user that the app will run under.
# See https://docs.docker.com/go/dockerfile-user-best-practices/
# Only for sherlock: we SHOULD create a home directory for the user, since the app will write to it.
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/sherlock" \
    --shell "/sbin/nologin" \
    --uid "${UID}" \
    appuser
USER appuser

# Set the working directory in the Docker image
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build --chown=appuser:appuser /app/build/out/*.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
