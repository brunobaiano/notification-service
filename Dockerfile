# Use the official Maven image with JDK 21 to build the application
FROM maven:3.9.8-eclipse-temurin AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src /app/src

# Build the application
RUN mvn clean package -Dmaven.test.skip=true

# Use a smaller image for the runtime environment with JDK 21
FROM eclipse-temurin:21-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built application JAR from the previous stage
COPY --from=build /app/target/notification-service-1.0-SNAPSHOT.jar /app/notification-service.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]
