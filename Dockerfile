FROM gradle:8.0.2-jdk17 AS build
COPY --chown=gradle:gradle . /src
WORKDIR /src
ARG USERNAME
ARG TOKEN

RUN USERNAME=$USERNAME TOKEN=$TOKEN gradle build --no-daemon

FROM eclipse-temurin:17-jre

EXPOSE 6566
EXPOSE 8081

RUN mkdir /app

COPY --from=build /src/build/libs/wise-task-plugin-1.0.0.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
