## OpenJDK 17의 slim 버전을 기반 이미지로 사용합니다
#FROM openjdk:17-jdk-slim
#
## 작업 디렉토리 설정
#WORKDIR /app
#
## 빌드된 JAR 파일 복사
#COPY build/libs/*.jar app.jar
#
### .env 파일도 컨테이너 내부에 복사
##COPY .env .env
#
## 컨테이너가 8080 포트에서 통신하도록 설정합니다.
#EXPOSE 8080
#
## 컨테이너가 실행될 때 실행할 명령어
#ENTRYPOINT ["java", "-jar", "app.jar"]

# OpenJDK 17의 slim 버전을 기반 이미지로 사용합니다
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# .env 파일도 필요하면 복사
# COPY .env .env

# 컨테이너가 8080 포트에서 통신하도록 설정
EXPOSE 8080

# JVM 옵션을 포함해서 컨테이너 실행
ENTRYPOINT ["java", "-Djdk.internal.platform.useContainerSupport=false", "-jar", "app.jar"]
