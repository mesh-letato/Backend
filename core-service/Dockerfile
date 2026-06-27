FROM gradle:8.14-jdk17 AS build
WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY api-gateway/build.gradle api-gateway/
COPY core-service/build.gradle core-service/
COPY link-service/build.gradle link-service/
RUN gradle :core-service:dependencies --no-daemon || true
COPY core-service/src core-service/src
RUN gradle :core-service:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/core-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
