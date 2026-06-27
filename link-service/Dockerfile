FROM gradle:8.14-jdk17 AS build
WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY api-gateway/build.gradle api-gateway/
COPY core-service/build.gradle core-service/
COPY link-service/build.gradle link-service/
RUN gradle :link-service:dependencies --no-daemon || true
COPY link-service/src link-service/src
RUN gradle :link-service:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/link-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
