FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . /app
RUN ./gradlew --no-daemon clean bootJar \
    && cp "$(ls -1 build/libs/*.jar | grep -v -- '-plain\.jar$' | head -n 1)" /app/app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
