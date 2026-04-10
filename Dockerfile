# Use latest Java 17 image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build project
RUN ./mvnw clean package

# Run application
CMD ["java", "-jar", "target/*.jar"]