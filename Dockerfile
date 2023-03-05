FROM eclipse-temurin:19-jdk-alpine
VOLUME /tmp
COPY /build/libs/wise-task-plugin-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","--enable-preview","/app.jar"]