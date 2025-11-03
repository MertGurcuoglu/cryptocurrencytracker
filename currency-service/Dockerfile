FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/currency-service-0.0.1-SNAPSHOT.jar currency-service.jar

ENTRYPOINT ["java", "-jar", "currency-service.jar"]

EXPOSE 8082
