
FROM openjdk:17-jdk-slim

WORKDIR /app

# 소스 코드 및 JAR 파일 복사
COPY build/libs/*.jar /app/spring-h.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/spring-h.jar"]