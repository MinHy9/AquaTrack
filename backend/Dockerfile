FROM openjdk:17-slim

# 필수 유틸리티 설치 (xargs는 기본 내장됨)
RUN apt update && apt install -y curl unzip

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test
EXPOSE 8080
CMD ["java", "-jar", "build/libs/aquatrack_backend-0.0.1-SNAPSHOT.jar"]
