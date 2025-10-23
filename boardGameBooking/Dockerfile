FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . /app
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
