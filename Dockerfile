
FROM openjdk:17-jdk-slim

WORKDIR /app

# 소스 코드 및 JAR 파일 복사
COPY build/libs/*.jar /app/streaming.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/streaming.jar"]