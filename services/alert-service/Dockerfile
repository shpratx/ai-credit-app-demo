FROM eclipse-temurin:21-jre-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "/app.jar"]
