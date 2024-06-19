FROM openjdk:17-jdk-slim

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y wget unzip

# Gradle 설치
RUN wget https://services.gradle.org/distributions/gradle-7.5.1-bin.zip -P /tmp
RUN unzip -d /opt/gradle /tmp/gradle-7.5.1-bin.zip
ENV GRADLE_HOME /opt/gradle/gradle-7.5.1
ENV PATH ${GRADLE_HOME}/bin:${PATH}

WORKDIR /app

# 소스 코드 복사
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/
COPY settings.gradle /app/
COPY src /app/src

# gradlew 파일에 실행 권한 부여
RUN chmod +x /app/gradlew

# 디버깅: gradlew 파일이 올바르게 복사되었는지 확인
RUN ls -l /app/gradlew

# Gradle 빌드 실행
RUN ./gradlew clean build -x test

# 디버깅: 빌드 후 결과물을 확인
RUN ls -l /app/build/libs/

# 빌드된 JAR 파일을 이미지에 복사
COPY build/libs/*.jar /app/spring-h.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/spring-h.jar"]
