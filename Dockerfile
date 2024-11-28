# First Stage: Build the Application
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Second Stage: Build the Runtime Image
FROM openjdk:17.0-jdk-slim
WORKDIR /app
COPY --from=build /app/target/respo-0.0.1-SNAPSHOT.jar respo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "respo.jar"]
