FROM openjdk:17
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
EXPOSE 8080
CMD ["java", "-jar", "build/libs/aquatrack_backend-0.0.1-SNAPSHOT.jar"]
