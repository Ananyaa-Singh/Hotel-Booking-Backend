# Use a supported OpenJDK image
FROM eclipse-temurin:20-jdk-focal

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (leverage Docker cache)
COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn

# Make Maven wrapper executable
RUN chmod +x mvnw

# Copy source code
COPY src ./src

# Build Spring Boot app and skip tests
RUN ./mvnw clean package -DskipTests

# Expose Spring Boot default port
EXPOSE 8080

# Run the generated jar
CMD ["java", "-jar", "target/HotelBooking-0.0.1-SNAPSHOT.jar"]
