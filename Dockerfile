# Use OpenJDK 20 slim as base image
FROM openjdk:20-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn

# Copy source code
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

# Expose default Spring Boot port
EXPOSE 8080

# Run the generated jar
CMD ["java", "-jar", "target/HotelBooking-0.0.1-SNAPSHOT.jar"]
