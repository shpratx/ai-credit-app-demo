FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY target/consent-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]
