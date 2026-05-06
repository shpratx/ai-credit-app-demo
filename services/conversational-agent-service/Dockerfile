FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY target/conversational-agent-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]
