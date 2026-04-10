# Use Java 17
FROM openjdk:17

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build project
RUN ./mvnw clean package

# Run application
CMD ["java", "-jar", "target/*.jar"]