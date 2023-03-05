FROM eclipse-temurin:19-jdk-alpine
COPY . .
EXPOSE 8080
ENTRYPOINT ["./gradlew", "bootRun", "--args='--spring.profiles.active=prod'"]