# Use a builder stage for building the jar
FROM eclipse-temurin:17-alpine AS builder

# Set the work dir to gradle home for building
WORKDIR /home/gradle

# Cache gradle
COPY gradlew .
COPY ./gradle ./gradle
RUN ./gradlew --no-daemon --version

# Copy all source files
COPY . .

# Build the jar
RUN ./gradlew --no-daemon build


# Use the Java 8 image
FROM eclipse-temurin:17-alpine

# Create a bot directory
RUN mkdir /bot && \
    chown -R 65534:65534 /bot
WORKDIR /bot

# Copy the compiled jar
COPY --from=builder --chown=65534:65534 /home/gradle/build/libs/Neptune*-all.jar ./Neptune.jar

# Set the user to a non-root user
USER nobody

# Set the command to run the bot
CMD [ "java", "-jar", "Neptune.jar" ]
