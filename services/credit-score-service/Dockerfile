FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY target/credit-score-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]
