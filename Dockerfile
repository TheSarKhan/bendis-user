FROM openjdk:17-jdk-slim
COPY backend/target/backend-0.0.1-SNAPSHOT.jar ecommerce.jar

CMD ["java", "-jar","backend.jar"]
