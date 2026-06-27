FROM gradle:8.14-jdk17 AS build
WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY api-gateway/build.gradle api-gateway/
COPY core-service/build.gradle core-service/
COPY link-service/build.gradle link-service/
RUN gradle :api-gateway:dependencies --no-daemon || true
COPY api-gateway/src api-gateway/src
RUN gradle :api-gateway:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/api-gateway/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
