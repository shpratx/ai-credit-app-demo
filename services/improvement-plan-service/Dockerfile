FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/improvement-plan-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]
