FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test \
    && cp "$(ls -1 build/libs/*.jar | grep -v -- '-plain\.jar$' | head -n 1)" /app/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
