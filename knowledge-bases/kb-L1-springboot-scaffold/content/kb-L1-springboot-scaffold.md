# Spring Boot 3.3 API Project Scaffold — Knowledge Base
### kb-L1-springboot-scaffold v1.0.0
### Production-ready scaffold for greenfield Spring Boot 3.3.x microservice APIs. Compliant with kb-L1-enterprise-architecture (Lloyds Banking Group). Targets GCP/GKE deployment with Cloud Build CI.

---

## SC1: Scaffold Overview

This scaffold produces a fully runnable Spring Boot 3.3.x microservice with:
- Hexagonal Architecture (ports & adapters) with 4 packages: domain, application, infrastructure, api
- CQRS via custom command/query handler interfaces (Spring DI — no external mediator library needed)
- Health checks (Spring Actuator — liveness + readiness probes)
- Structured logging (Logback + Logstash JSON encoder) with correlation ID via MDC
- OpenTelemetry tracing + metrics (Micrometer + OTLP exporter + OTel Java Agent)
- JWT authentication (Spring Security OAuth2 Resource Server)
- RFC 7807 error handling (Spring's ProblemDetail)
- Spring Data JPA with Flyway migrations (Cloud SQL PostgreSQL)
- Docker + Kubernetes manifests (GKE-optimised)
- Cloud Build CI pipeline (lint → build → test → SAST → SCA → image → deploy)

### Technology Alignment (per Lloyds Enterprise Architecture)
| Concern | Choice | EA Reference |
|---------|--------|--------------|
| Language | Java 21 (LTS) | Confirmed: Lloyds Talent pages |
| Framework | Spring Boot 3.3.x | Inferred: standard for UK banking on GKE |
| Cloud | Google Cloud Platform | Confirmed: Google Cloud case study |
| Container orchestration | GKE | Confirmed |
| Messaging | Pub/Sub | Confirmed |
| Database | Cloud SQL (PostgreSQL) | Inferred: standard GCP banking choice |
| Caching | Memorystore (Redis) | Inferred |
| CI/CD | Cloud Build → Cloud Deploy → GKE | Confirmed |
| IaC | Terraform | Confirmed |
| Registry | Artifact Registry | Confirmed |
| Secrets | Secret Manager | Confirmed |
| Observability | Cloud Monitoring, Cloud Logging, Cloud Trace | Confirmed |
| API Gateway | Apigee | Inferred |

---

## SC2: File Tree

```
{service-name}/
├── src/
│   └── main/
│       ├── java/com/lloyds/{servicename}/
│       │   ├── Application.java                          # @SpringBootApplication
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   │   ├── BaseEntity.java                   # UUID id, audit columns, @Version
│       │   │   │   └── .gitkeep
│       │   │   ├── valueobject/
│       │   │   │   └── .gitkeep
│       │   │   ├── event/
│       │   │   │   ├── DomainEvent.java                  # Marker interface
│       │   │   │   └── .gitkeep
│       │   │   ├── port/
│       │   │   │   └── .gitkeep                          # Repository interfaces (outbound ports)
│       │   │   └── exception/
│       │   │       ├── DomainException.java
│       │   │       └── EntityNotFoundException.java
│       │   ├── application/
│       │   │   ├── command/
│       │   │   │   ├── CommandHandler.java               # Generic command handler interface
│       │   │   │   └── .gitkeep
│       │   │   ├── query/
│       │   │   │   ├── QueryHandler.java                 # Generic query handler interface
│       │   │   │   └── .gitkeep
│       │   │   ├── dto/
│       │   │   │   └── .gitkeep
│       │   │   ├── mapper/
│       │   │   │   └── .gitkeep                          # MapStruct mappers
│       │   │   ├── service/
│       │   │   │   └── .gitkeep                          # Application services (orchestration)
│       │   │   └── validation/
│       │   │       └── .gitkeep                          # Custom validators
│       │   ├── infrastructure/
│       │   │   ├── persistence/
│       │   │   │   ├── repository/                       # JPA repository adapters
│       │   │   │   └── entity/                           # JPA entity mappings (if separate from domain)
│       │   │   ├── client/
│       │   │   │   └── .gitkeep                          # External service clients (CRA, Envoy, Vertex AI)
│       │   │   ├── messaging/
│       │   │   │   ├── publisher/                        # Pub/Sub publishers
│       │   │   │   └── subscriber/                       # Pub/Sub subscribers
│       │   │   ├── cache/
│       │   │   │   └── RedisCacheService.java
│       │   │   ├── encryption/
│       │   │   │   └── CloudKmsEncryptionService.java
│       │   │   └── config/
│       │   │       ├── SecurityConfig.java
│       │   │       ├── JacksonConfig.java
│       │   │       ├── ResilienceConfig.java
│       │   │       ├── OpenApiConfig.java
│       │   │       └── CorsConfig.java
│       │   └── api/
│       │       ├── controller/
│       │       │   └── .gitkeep
│       │       ├── dto/
│       │       │   └── .gitkeep                          # API-specific request/response DTOs
│       │       └── filter/
│       │           ├── CorrelationIdFilter.java
│       │           ├── RequestLoggingFilter.java
│       │           ├── IdempotencyFilter.java
│       │           └── GlobalExceptionHandler.java
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-staging.yml
│           ├── application-prod.yml
│           ├── logback-spring.xml
│           └── db/migration/
│               └── V1__init.sql
├── src/
│   └── test/
│       └── java/com/lloyds/{servicename}/
│           ├── unit/
│           │   └── .gitkeep
│           ├── integration/
│           │   ├── BaseIntegrationTest.java
│           │   └── .gitkeep
│           └── contract/
│               └── .gitkeep
├── pom.xml
├── Dockerfile
├── cloudbuild.yaml
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   └── hpa.yaml
├── checkstyle.xml
├── .editorconfig
├── .gitignore
└── README.md
```


---

## SC3: Build File (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.lloyds</groupId>
    <artifactId>{service-name}</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>{service-name}</name>
    <description>Lloyds Banking Group — {ServiceName} Microservice</description>

    <properties>
        <java.version>21</java.version>
        <spring-cloud-gcp.version>5.8.0</spring-cloud-gcp.version>
        <resilience4j.version>2.2.0</resilience4j.version>
        <flyway.version>10.20.1</flyway.version>
        <springdoc.version>2.6.0</springdoc.version>
        <mapstruct.version>1.6.2</mapstruct.version>
        <lombok.version>1.18.34</lombok.version>
        <testcontainers.version>1.20.3</testcontainers.version>
        <wiremock.version>3.9.2</wiremock.version>
        <logstash-logback.version>8.0</logstash-logback.version>
        <micrometer-otlp.version>1.13.6</micrometer-otlp.version>
        <jacoco.version>0.8.12</jacoco.version>
        <checkstyle.version>10.20.1</checkstyle.version>
        <spotbugs.version>4.8.6</spotbugs.version>
        <owasp.version>10.0.4</owasp.version>
    </properties>

    <dependencies>
        <!-- ── Spring Boot Starters ─────────────────────────── -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- ── GCP ──────────────────────────────────────────── -->
        <dependency>
            <groupId>com.google.cloud</groupId>
            <artifactId>spring-cloud-gcp-starter-pubsub</artifactId>
            <version>${spring-cloud-gcp.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.cloud</groupId>
            <artifactId>spring-cloud-gcp-starter-secretmanager</artifactId>
            <version>${spring-cloud-gcp.version}</version>
        </dependency>

        <!-- ── Resilience ───────────────────────────────────── -->
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>

        <!-- ── Database ─────────────────────────────────────── -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
            <version>${flyway.version}</version>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
            <version>${flyway.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- ── Observability ────────────────────────────────── -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-otlp</artifactId>
            <version>${micrometer-otlp.version}</version>
        </dependency>
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>${logstash-logback.version}</version>
        </dependency>

        <!-- ── API Documentation ────────────────────────────── -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>

        <!-- ── Code Generation ──────────────────────────────── -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>

        <!-- ── Test ─────────────────────────────────────────── -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.wiremock</groupId>
            <artifactId>wiremock-standalone</artifactId>
            <version>${wiremock.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- ── Spring Boot Maven Plugin ─────────────────── -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>

            <!-- ── Compiler (Lombok + MapStruct) ────────────── -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok-mapstruct-binding</artifactId>
                            <version>0.2.0</version>
                        </path>
                    </annotationProcessorPaths>
                    <compilerArgs>
                        <arg>-Amapstruct.defaultComponentModel=spring</arg>
                    </compilerArgs>
                </configuration>
            </plugin>

            <!-- ── JaCoCo (Coverage) ────────────────────────── -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>${jacoco.version}</version>
                <executions>
                    <execution>
                        <goals><goal>prepare-agent</goal></goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals><goal>report</goal></goals>
                    </execution>
                    <execution>
                        <id>check</id>
                        <goals><goal>check</goal></goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>BUNDLE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.80</minimum>
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- ── Checkstyle ───────────────────────────────── -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <configLocation>checkstyle.xml</configLocation>
                    <consoleOutput>true</consoleOutput>
                    <failsOnError>true</failsOnError>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>com.puppycrawl.tools</groupId>
                        <artifactId>checkstyle</artifactId>
                        <version>${checkstyle.version}</version>
                    </dependency>
                </dependencies>
            </plugin>

            <!-- ── SpotBugs + FindSecBugs ───────────────────── -->
            <plugin>
                <groupId>com.github.spotbugs</groupId>
                <artifactId>spotbugs-maven-plugin</artifactId>
                <version>${spotbugs.version}</version>
                <configuration>
                    <effort>Max</effort>
                    <threshold>Medium</threshold>
                    <plugins>
                        <plugin>
                            <groupId>com.h3xstream.findsecbugs</groupId>
                            <artifactId>findsecbugs-plugin</artifactId>
                            <version>1.13.0</version>
                        </plugin>
                    </plugins>
                </configuration>
            </plugin>

            <!-- ── OWASP Dependency-Check ───────────────────── -->
            <plugin>
                <groupId>org.owasp</groupId>
                <artifactId>dependency-check-maven</artifactId>
                <version>${owasp.version}</version>
                <configuration>
                    <failBuildOnCVSS>7</failBuildOnCVSS>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```


---

## SC4: Application Main Class + Configuration

### Application.java
```java
package com.lloyds.{servicename};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### SecurityConfig.java
```java
package com.lloyds.{servicename}.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }
}
```

### JacksonConfig.java
```java
package com.lloyds.{servicename}.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
```

### ResilienceConfig.java
```java
package com.lloyds.{servicename}.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerConfig defaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .build();
    }

    @Bean
    public TimeLimiterConfig defaultTimeLimiterConfig() {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(5))
            .build();
    }
}
```

### OpenApiConfig.java
```java
package com.lloyds.{servicename}.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("{ServiceName} API")
                .version("1.0.0")
                .description("Lloyds Banking Group — {ServiceName} Microservice API"))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
```

### CorsConfig.java
```java
package com.lloyds.{servicename}.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```


---

## SC5: Middleware Equivalents (Filters & Interceptors)

### CorrelationIdFilter.java
```java
package com.lloyds.{servicename}.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
```

### RequestLoggingFilter.java
```java
package com.lloyds.{servicename}.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(2)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("HTTP {} {} responded {} in {}ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs");
    }
}
```

### GlobalExceptionHandler.java
```java
package com.lloyds.{servicename}.api.filter;

import com.lloyds.{servicename}.domain.exception.DomainException;
import com.lloyds.{servicename}.domain.exception.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/not-found"));
        problem.setTitle("Resource Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        log.warn("Domain error: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/domain-error"));
        problem.setTitle("Business Rule Violation");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                (a, b) -> a
            ));

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setType(URI.create("https://api.lloyds.com/errors/validation"));
        problem.setTitle("Validation Error");
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/validation"));
        problem.setTitle("Constraint Violation");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
        problem.setType(URI.create("https://api.lloyds.com/errors/forbidden"));
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support.");
        problem.setType(URI.create("https://api.lloyds.com/errors/internal"));
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
```

### IdempotencyFilter.java
```java
package com.lloyds.{servicename}.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String REDIS_PREFIX = "idempotency:";
    private static final Duration TTL = Duration.ofHours(24);
    private static final Set<String> IDEMPOTENT_METHODS = Set.of("POST", "PUT");

    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        if (!IDEMPOTENT_METHODS.contains(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String redisKey = REDIS_PREFIX + idempotencyKey;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(redisKey, "processing", TTL);

        if (Boolean.FALSE.equals(isNew)) {
            log.warn("Duplicate request detected for idempotency key: {}", idempotencyKey);
            response.setStatus(HttpStatus.CONFLICT.value());
            response.setContentType("application/problem+json");
            response.getWriter().write("""
                {"type":"https://api.lloyds.com/errors/duplicate-request","title":"Duplicate Request","status":409,"detail":"Request with this Idempotency-Key has already been processed"}
                """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !IDEMPOTENT_METHODS.contains(request.getMethod());
    }
}
```


---

## SC6: Health Controller (Actuator)

Spring Actuator provides health endpoints out of the box. Custom health indicators extend readiness checks.

### Actuator Configuration (in application.yml — see SC7)
Health groups are configured to separate liveness from readiness:
- `/actuator/health/liveness` — always UP if JVM is running
- `/actuator/health/readiness` — checks database, redis, pubsub, external services

### Custom Health Indicators

```java
package com.lloyds.{servicename}.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component("redis")
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try {
            var connection = redisConnectionFactory.getConnection();
            String pong = connection.ping();
            connection.close();
            return Health.up().withDetail("ping", pong).build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
```

```java
package com.lloyds.{servicename}.infrastructure.config;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("pubsub")
public class PubSubHealthIndicator implements HealthIndicator {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Override
    public Health health() {
        try (var client = TopicAdminClient.create()) {
            client.listTopics(String.format("projects/%s", projectId));
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
```

```java
package com.lloyds.{servicename}.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("cra")
@RequiredArgsConstructor
public class CraHealthIndicator implements HealthIndicator {

    private final RestClient.Builder restClientBuilder;

    @Override
    public Health health() {
        try {
            var response = restClientBuilder.build()
                .get()
                .uri("${app.cra.base-url}/health")
                .retrieve()
                .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful()
                ? Health.up().build()
                : Health.down().withDetail("status", response.getStatusCode()).build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
```

---

## SC7: Configuration Files

### application.yml (base)
```yaml
spring:
  application:
    name: ${SERVICE_NAME:{service-name}}
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  # ── Database ──────────────────────────────────────────────
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:{service_name}_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

  # ── Security ──────────────────────────────────────────────
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:https://auth.lloyds.com/realms/services}
          audiences: ${JWT_AUDIENCE:{service-name}}

  # ── Redis ─────────────────────────────────────────────────
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      timeout: 2000ms

  # ── Jackson ───────────────────────────────────────────────
  jackson:
    property-naming-strategy: LOWER_CAMEL_CASE
    serialization:
      write-dates-as-timestamps: false
      write-enums-using-to-string: true
    deserialization:
      fail-on-unknown-properties: false
    default-property-inclusion: non_null

  # ── GCP ───────────────────────────────────────────────────
  cloud:
    gcp:
      project-id: ${GCP_PROJECT_ID:lloyds-dev}
      pubsub:
        subscriber:
          parallel-pull-count: 2
          max-ack-extension-period: 600

# ── Actuator ────────────────────────────────────────────────
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
      group:
        liveness:
          include: livenessState
        readiness:
          include: readinessState,db,redis,pubsub
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true

  # ── Metrics (OTLP) ───────────────────────────────────────
  otlp:
    metrics:
      export:
        url: ${OTLP_ENDPOINT:http://localhost:4318}/v1/metrics
        step: 30s
    tracing:
      endpoint: ${OTLP_ENDPOINT:http://localhost:4318}/v1/traces
  tracing:
    sampling:
      probability: 1.0

# ── Resilience4j ────────────────────────────────────────────
resilience4j:
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 3
    instances:
      cra-service:
        base-config: default
      vertex-ai:
        base-config: default
        wait-duration-in-open-state: 60s
  retry:
    configs:
      default:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
    instances:
      cra-service:
        base-config: default
  timelimiter:
    configs:
      default:
        timeout-duration: 5s
    instances:
      cra-service:
        timeout-duration: 10s

# ── Application ─────────────────────────────────────────────
app:
  cors:
    allowed-origins:
      - http://localhost:3000
  cra:
    base-url: ${CRA_BASE_URL:http://localhost:8081}
  encryption:
    key-name: ${KMS_KEY_NAME:projects/lloyds-dev/locations/europe-west2/keyRings/app/cryptoKeys/default}

# ── Springdoc ───────────────────────────────────────────────
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
```

### application-dev.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/{service_name}_db
    username: postgres
    password: postgres
  data:
    redis:
      host: localhost
      port: 6379
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/services

management:
  tracing:
    sampling:
      probability: 1.0

logging:
  level:
    com.lloyds: DEBUG
    org.springframework.security: DEBUG
```

### application-staging.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/{service_name}_db
  cloud:
    gcp:
      project-id: lloyds-staging

management:
  tracing:
    sampling:
      probability: 0.5

logging:
  level:
    com.lloyds: INFO
```

### application-prod.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/{service_name}_db
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
  cloud:
    gcp:
      project-id: lloyds-prod

management:
  tracing:
    sampling:
      probability: 0.1
  endpoint:
    health:
      show-details: never

logging:
  level:
    com.lloyds: INFO
    org.springframework: WARN

app:
  cors:
    allowed-origins:
      - https://www.lloydsbank.com
      - https://www.halifax.co.uk
      - https://www.bankofscotland.co.uk
```

### logback-spring.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <springProperty scope="context" name="SERVICE_NAME" source="spring.application.name" defaultValue="unknown"/>

    <!-- ── Console (dev) ────────────────────────────────────── -->
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} %highlight(%-5level) [%thread] %cyan(%logger{36}) - %X{correlationId:-none} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>

    <!-- ── JSON (staging, prod) ─────────────────────────────── -->
    <springProfile name="staging | prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <customFields>{"service":"${SERVICE_NAME}"}</customFields>
                <includeMdcKeyName>correlationId</includeMdcKeyName>
                <timeZone>UTC</timeZone>
                <timestampPattern>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</timestampPattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="JSON"/>
        </root>
    </springProfile>
</configuration>
```


---

## SC8: Domain Layer

The domain layer contains business logic with ZERO framework dependencies (no Spring annotations). JPA annotations are the only exception for entity mapping.

### BaseEntity.java
```java
package com.lloyds.{servicename}.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
```

### Example Entity
```java
package com.lloyds.{servicename}.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication extends BaseEntity {

    @Column(name = "applicant_id", nullable = false)
    private String applicantId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @Column(name = "decision_reason")
    private String decisionReason;
}
```

### Value Object Example
```java
package com.lloyds.{servicename}.domain.valueobject;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO code");
        }
    }

    public static Money gbp(BigDecimal amount) {
        return new Money(amount, "GBP");
    }
}
```

### Domain Event
```java
package com.lloyds.{servicename}.domain.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID eventId();
    Instant occurredAt();
    String eventType();
}
```

```java
package com.lloyds.{servicename}.domain.event;

import java.time.Instant;
import java.util.UUID;

public record LoanApplicationCreatedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID loanApplicationId,
    String applicantId
) implements DomainEvent {

    public LoanApplicationCreatedEvent(UUID loanApplicationId, String applicantId) {
        this(UUID.randomUUID(), Instant.now(), loanApplicationId, applicantId);
    }

    @Override
    public String eventType() {
        return "loan.application.created";
    }
}
```

### Repository Port (Interface)
```java
package com.lloyds.{servicename}.domain.port;

import com.lloyds.{servicename}.domain.model.LoanApplication;

import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository {
    LoanApplication save(LoanApplication application);
    Optional<LoanApplication> findById(UUID id);
    Optional<LoanApplication> findByApplicantId(String applicantId);
}
```

### Domain Exceptions
```java
package com.lloyds.{servicename}.domain.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
```

```java
package com.lloyds.{servicename}.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s with id '%s' not found", entityName, id));
    }
}
```


---

## SC9: Application Layer

The application layer orchestrates use cases via command/query handlers. Spring's DI replaces the need for a mediator library.

### Command Handler Interface
```java
package com.lloyds.{servicename}.application.command;

public interface CommandHandler<C, R> {
    R handle(C command);
}
```

### Query Handler Interface
```java
package com.lloyds.{servicename}.application.query;

public interface QueryHandler<Q, R> {
    R handle(Q query);
}
```

### Example Command + Handler
```java
package com.lloyds.{servicename}.application.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateLoanApplicationCommand(
    @NotBlank String applicantId,
    @NotNull @DecimalMin("1000.00") BigDecimal amount
) {}
```

```java
package com.lloyds.{servicename}.application.command;

import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.application.mapper.LoanApplicationMapper;
import com.lloyds.{servicename}.domain.event.LoanApplicationCreatedEvent;
import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.domain.model.LoanStatus;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import com.lloyds.{servicename}.infrastructure.messaging.publisher.DomainEventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CreateLoanApplicationHandler implements CommandHandler<CreateLoanApplicationCommand, LoanApplicationResponse> {

    private final LoanApplicationRepository repository;
    private final DomainEventPublisher eventPublisher;
    private final LoanApplicationMapper mapper;

    @Override
    @Transactional
    public LoanApplicationResponse handle(@Valid CreateLoanApplicationCommand command) {
        log.info("Creating loan application for applicant: {}", command.applicantId());

        var application = LoanApplication.builder()
            .applicantId(command.applicantId())
            .amount(command.amount())
            .status(LoanStatus.PENDING)
            .build();

        var saved = repository.save(application);

        eventPublisher.publish(new LoanApplicationCreatedEvent(saved.getId(), saved.getApplicantId()));

        log.info("Loan application created: {}", saved.getId());
        return mapper.toResponse(saved);
    }
}
```

### Example Query + Handler
```java
package com.lloyds.{servicename}.application.query;

import java.util.UUID;

public record GetLoanApplicationQuery(UUID id) {}
```

```java
package com.lloyds.{servicename}.application.query;

import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.application.mapper.LoanApplicationMapper;
import com.lloyds.{servicename}.domain.exception.EntityNotFoundException;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetLoanApplicationHandler implements QueryHandler<GetLoanApplicationQuery, LoanApplicationResponse> {

    private final LoanApplicationRepository repository;
    private final LoanApplicationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public LoanApplicationResponse handle(GetLoanApplicationQuery query) {
        var application = repository.findById(query.id())
            .orElseThrow(() -> new EntityNotFoundException("LoanApplication", query.id()));
        return mapper.toResponse(application);
    }
}
```

### DTO (Response)
```java
package com.lloyds.{servicename}.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanApplicationResponse(
    UUID id,
    String applicantId,
    BigDecimal amount,
    String status,
    String decisionReason,
    Instant createdAt,
    Instant updatedAt
) {}
```

### MapStruct Mapper
```java
package com.lloyds.{servicename}.application.mapper;

import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.domain.model.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    LoanApplicationResponse toResponse(LoanApplication entity);
}
```

### Custom Validator Example
```java
package com.lloyds.{servicename}.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UkPostcodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUkPostcode {
    String message() default "Invalid UK postcode";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

```java
package com.lloyds.{servicename}.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UkPostcodeValidator implements ConstraintValidator<ValidUkPostcode, String> {

    private static final Pattern UK_POSTCODE = Pattern.compile(
        "^[A-Z]{1,2}\\d[A-Z\\d]?\\s?\\d[A-Z]{2}$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // use @NotBlank for required
        return UK_POSTCODE.matcher(value.trim()).matches();
    }
}
```


---

## SC10: Infrastructure Layer

The infrastructure layer implements domain ports and integrates with external systems.

### JPA Repository Adapter
```java
package com.lloyds.{servicename}.infrastructure.persistence.repository;

import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaLoanApplicationRepository extends JpaRepository<LoanApplication, UUID>, LoanApplicationRepository {
    Optional<LoanApplication> findByApplicantId(String applicantId);
}
```

### External Service Client (CRA) with Circuit Breaker
```java
package com.lloyds.{servicename}.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class CraClient {

    private final RestClient restClient;

    public CraClient(RestClient.Builder restClientBuilder,
                     @Value("${app.cra.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "cra-service", fallbackMethod = "getCreditScoreFallback")
    @Retry(name = "cra-service")
    @TimeLimiter(name = "cra-service")
    public CreditScoreResponse getCreditScore(String applicantId) {
        log.info("Fetching credit score for applicant: {}", applicantId);
        return restClient.get()
            .uri("/api/v1/credit-score/{applicantId}", applicantId)
            .retrieve()
            .body(CreditScoreResponse.class);
    }

    private CreditScoreResponse getCreditScoreFallback(String applicantId, Throwable t) {
        log.warn("CRA service unavailable for applicant: {}. Fallback triggered: {}", applicantId, t.getMessage());
        return new CreditScoreResponse(applicantId, -1, "UNAVAILABLE");
    }

    public record CreditScoreResponse(String applicantId, int score, String band) {}
}
```

### Vertex AI Client
```java
package com.lloyds.{servicename}.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class VertexAiClient {

    private final RestClient restClient;

    public VertexAiClient(RestClient.Builder restClientBuilder,
                          @Value("${app.vertex-ai.endpoint:http://localhost:8082}") String endpoint) {
        this.restClient = restClientBuilder.baseUrl(endpoint).build();
    }

    @CircuitBreaker(name = "vertex-ai", fallbackMethod = "predictFallback")
    public PredictionResponse predict(PredictionRequest request) {
        return restClient.post()
            .uri("/v1/predict")
            .body(request)
            .retrieve()
            .body(PredictionResponse.class);
    }

    private PredictionResponse predictFallback(PredictionRequest request, Throwable t) {
        log.warn("Vertex AI unavailable: {}", t.getMessage());
        return new PredictionResponse("UNAVAILABLE", 0.0);
    }

    public record PredictionRequest(String modelId, Object features) {}
    public record PredictionResponse(String prediction, double confidence) {}
}
```

### Pub/Sub Publisher
```java
package com.lloyds.{servicename}.infrastructure.messaging.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.lloyds.{servicename}.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.pubsub.topic:domain-events}")
    private String topicName;

    public void publish(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            pubSubTemplate.publish(topicName, payload)
                .whenComplete((id, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {}: {}", event.eventType(), ex.getMessage());
                    } else {
                        log.info("Published event {} with messageId: {}", event.eventType(), id);
                    }
                });
        } catch (Exception e) {
            log.error("Failed to serialize event {}: {}", event.eventType(), e.getMessage());
        }
    }
}
```

### Pub/Sub Subscriber
```java
package com.lloyds.{servicename}.infrastructure.messaging.subscriber;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Configuration
public class EventSubscriberConfig {

    @ServiceActivator(inputChannel = "domainEventsInputChannel")
    public void handleMessage(String payload,
                              @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
        try {
            log.info("Received event: {}", payload);
            // Process the event
            message.ack();
        } catch (Exception e) {
            log.error("Failed to process event: {}", e.getMessage());
            message.nack();
        }
    }
}
```

### Redis Cache Service
```java
package com.lloyds.{servicename}.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> void put(String key, T value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (Exception e) {
            log.warn("Failed to cache key {}: {}", key, e.getMessage());
        }
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, type));
        } catch (Exception e) {
            log.warn("Failed to read cache key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }
}
```

### Cloud KMS Encryption Service
```java
package com.lloyds.{servicename}.infrastructure.encryption;

import com.google.cloud.kms.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class CloudKmsEncryptionService {

    @Value("${app.encryption.key-name}")
    private String keyName;

    public String encrypt(String plaintext) {
        try (var client = KeyManagementServiceClient.create()) {
            var response = client.encrypt(
                CryptoKeyName.parse(keyName),
                ByteString.copyFromUtf8(plaintext)
            );
            return Base64.getEncoder().encodeToString(response.getCiphertext().toByteArray());
        } catch (Exception e) {
            log.error("Encryption failed: {}", e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String ciphertext) {
        try (var client = KeyManagementServiceClient.create()) {
            var response = client.decrypt(
                CryptoKeyName.parse(keyName),
                ByteString.copyFrom(Base64.getDecoder().decode(ciphertext))
            );
            return response.getPlaintext().toStringUtf8();
        } catch (Exception e) {
            log.error("Decryption failed: {}", e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
```

### Flyway Migration Example
```sql
-- src/main/resources/db/migration/V1__init.sql
CREATE TABLE loan_applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id VARCHAR(255) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    decision_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_loan_applications_applicant_id ON loan_applications(applicant_id);
CREATE INDEX idx_loan_applications_status ON loan_applications(status);
```


---

## SC11: API Layer

### REST Controller
```java
package com.lloyds.{servicename}.api.controller;

import com.lloyds.{servicename}.application.command.CreateLoanApplicationCommand;
import com.lloyds.{servicename}.application.command.CreateLoanApplicationHandler;
import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.application.query.GetLoanApplicationHandler;
import com.lloyds.{servicename}.application.query.GetLoanApplicationQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loan-applications")
@RequiredArgsConstructor
@Tag(name = "Loan Applications", description = "Loan application management endpoints")
public class LoanApplicationController {

    private final CreateLoanApplicationHandler createHandler;
    private final GetLoanApplicationHandler getHandler;

    @PostMapping
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    @Operation(summary = "Create a new loan application")
    public ResponseEntity<LoanApplicationResponse> create(@Valid @RequestBody CreateLoanApplicationRequest request) {
        var command = new CreateLoanApplicationCommand(request.applicantId(), request.amount());
        var response = createHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'UNDERWRITER')")
    @Operation(summary = "Get a loan application by ID")
    public ResponseEntity<LoanApplicationResponse> getById(@PathVariable UUID id) {
        var response = getHandler.handle(new GetLoanApplicationQuery(id));
        return ResponseEntity.ok(response);
    }
}
```

### API Request DTO
```java
package com.lloyds.{servicename}.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateLoanApplicationRequest(
    @NotBlank(message = "Applicant ID is required")
    String applicantId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum loan amount is £1,000")
    BigDecimal amount
) {}
```

### Response Envelope (optional pattern for paginated results)
```java
package com.lloyds.{servicename}.api.dto;

import java.util.List;

public record PagedResponse<T>(
    List<T> data,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return new PagedResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
```


---

## SC12: Testing

### Test Strategy
| Type | Framework | Scope | Location |
|------|-----------|-------|----------|
| Unit | JUnit 5 + Mockito + AssertJ | Domain + Application handlers | `src/test/java/.../unit/` |
| Integration | @SpringBootTest + Testcontainers + WireMock | Full stack with real DB | `src/test/java/.../integration/` |
| Contract | Spring Cloud Contract / Pub/Sub emulator | API contracts | `src/test/java/.../contract/` |
| Slice | @WebMvcTest, @DataJpaTest | Isolated layer testing | `src/test/java/.../unit/` |
| Coverage | JaCoCo | ≥80% line coverage enforced | Maven plugin |

### Unit Test Example (Command Handler)
```java
package com.lloyds.{servicename}.unit;

import com.lloyds.{servicename}.application.command.CreateLoanApplicationCommand;
import com.lloyds.{servicename}.application.command.CreateLoanApplicationHandler;
import com.lloyds.{servicename}.application.mapper.LoanApplicationMapper;
import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.domain.model.LoanStatus;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import com.lloyds.{servicename}.infrastructure.messaging.publisher.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateLoanApplicationHandler")
class CreateLoanApplicationHandlerTest {

    @Mock private LoanApplicationRepository repository;
    @Mock private DomainEventPublisher eventPublisher;
    @Mock private LoanApplicationMapper mapper;

    private CreateLoanApplicationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateLoanApplicationHandler(repository, eventPublisher, mapper);
    }

    @Test
    @DisplayName("should create loan application with PENDING status")
    void shouldCreateLoanApplication() {
        // Given
        var command = new CreateLoanApplicationCommand("APP-001", BigDecimal.valueOf(25000));
        var savedEntity = LoanApplication.builder()
            .applicantId("APP-001")
            .amount(BigDecimal.valueOf(25000))
            .status(LoanStatus.PENDING)
            .build();
        savedEntity.setId(UUID.randomUUID());

        when(repository.save(any(LoanApplication.class))).thenReturn(savedEntity);

        // When
        handler.handle(command);

        // Then
        var captor = ArgumentCaptor.forClass(LoanApplication.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(LoanStatus.PENDING);
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(25000));
        verify(eventPublisher).publish(any());
    }
}
```

### Integration Test Base Class
```java
package com.lloyds.{servicename}.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test_db")
        .withUsername("test")
        .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }
}
```

### Integration Test Example
```java
package com.lloyds.{servicename}.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Loan Application API Integration Tests")
class LoanApplicationApiTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "LOAN_OFFICER")
    @DisplayName("POST /api/v1/loan-applications - should create application")
    void shouldCreateLoanApplication() throws Exception {
        mockMvc.perform(post("/api/v1/loan-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "applicantId": "APP-001",
                        "amount": 25000.00
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.applicantId").value("APP-001"))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "LOAN_OFFICER")
    @DisplayName("POST /api/v1/loan-applications - should reject invalid amount")
    void shouldRejectInvalidAmount() throws Exception {
        mockMvc.perform(post("/api/v1/loan-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "applicantId": "APP-001",
                        "amount": 500.00
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.lloyds.com/errors/validation"));
    }

    @Test
    @DisplayName("POST /api/v1/loan-applications - should reject unauthenticated")
    void shouldRejectUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/loan-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "applicantId": "APP-001",
                        "amount": 25000.00
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }
}
```

### WebMvc Slice Test Example
```java
package com.lloyds.{servicename}.unit;

import com.lloyds.{servicename}.api.controller.LoanApplicationController;
import com.lloyds.{servicename}.application.command.CreateLoanApplicationHandler;
import com.lloyds.{servicename}.application.query.GetLoanApplicationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanApplicationController.class)
@DisplayName("LoanApplicationController Slice Tests")
class LoanApplicationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private CreateLoanApplicationHandler createHandler;
    @MockBean private GetLoanApplicationHandler getHandler;

    @Test
    @WithMockUser(roles = "LOAN_OFFICER")
    @DisplayName("should return 400 when body is empty")
    void shouldReturn400WhenBodyEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/loan-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
```

### WireMock for External Services
```java
package com.lloyds.{servicename}.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public abstract class BaseWireMockTest extends BaseIntegrationTest {

    static WireMockServer wireMockServer = new WireMockServer(0);

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureWireMock(DynamicPropertyRegistry registry) {
        registry.add("app.cra.base-url", () -> "http://localhost:" + wireMockServer.port());
    }

    protected void stubCreditScoreSuccess(String applicantId, int score) {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/credit-score/" + applicantId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"applicantId":"%s","score":%d,"band":"GOOD"}
                    """.formatted(applicantId, score))));
    }

    protected void stubCreditScoreTimeout(String applicantId) {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/credit-score/" + applicantId))
            .willReturn(aResponse().withFixedDelay(10000)));
    }
}
```


---

## SC13: Docker & Kubernetes

### Dockerfile (Multi-stage, GKE-optimised)
```dockerfile
# ── Build Stage ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests -B

# ── Runtime Stage ───────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# OpenTelemetry Java Agent (download at build time for tracing)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.9.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
RUN chmod 644 /app/opentelemetry-javaagent.jar

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -javaagent:/app/opentelemetry-javaagent.jar -jar app.jar"]
```

**Image requirements per EA:** Alpine base, non-root user, health check, < 250MB, OTel agent included.

### k8s/deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {service-name}
  namespace: {namespace}
  labels:
    app: {service-name}
    team: {team-name}
    version: "1.0.0"
spec:
  replicas: 2
  selector:
    matchLabels:
      app: {service-name}
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: {service-name}
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      serviceAccountName: {service-name}-sa
      containers:
        - name: {service-name}
          image: europe-west2-docker.pkg.dev/{gcp-project}/{repo}/{service-name}:latest
          ports:
            - containerPort: 8080
              protocol: TCP
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: OTEL_SERVICE_NAME
              value: "{service-name}"
            - name: OTEL_EXPORTER_OTLP_ENDPOINT
              value: "http://otel-collector.observability:4318"
          envFrom:
            - configMapRef:
                name: {service-name}-config
            - secretRef:
                name: {service-name}-secrets
          resources:
            requests:
              cpu: 250m
              memory: 512Mi
            limits:
              cpu: "1"
              memory: 1Gi
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
            failureThreshold: 3
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 5
            failureThreshold: 3
          startupProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
            failureThreshold: 12
      topologySpreadConstraints:
        - maxSkew: 1
          topologyKey: topology.kubernetes.io/zone
          whenUnsatisfiable: DoNotSchedule
          labelSelector:
            matchLabels:
              app: {service-name}
```

### k8s/service.yaml
```yaml
apiVersion: v1
kind: Service
metadata:
  name: {service-name}
  namespace: {namespace}
spec:
  type: ClusterIP
  selector:
    app: {service-name}
  ports:
    - port: 80
      targetPort: 8080
      protocol: TCP
```

### k8s/configmap.yaml
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {service-name}-config
  namespace: {namespace}
data:
  DB_HOST: "cloud-sql-proxy.database"
  DB_PORT: "5432"
  DB_NAME: "{service_name}_db"
  REDIS_HOST: "memorystore.cache"
  REDIS_PORT: "6379"
  JWT_ISSUER_URI: "https://auth.lloyds.com/realms/services"
  JWT_AUDIENCE: "{service-name}"
  OTLP_ENDPOINT: "http://otel-collector.observability:4318"
  GCP_PROJECT_ID: "{gcp-project}"
```

### k8s/hpa.yaml
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: {service-name}
  namespace: {namespace}
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: {service-name}
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Pods
          value: 1
          periodSeconds: 60
```


---

## SC14: CI Pipeline (Cloud Build)

### cloudbuild.yaml
```yaml
# Cloud Build CI/CD Pipeline
# Stages: Lint → Compile → Test → SAST → SCA → Build Image → Push → Deploy
substitutions:
  _SERVICE_NAME: '{service-name}'
  _REGION: 'europe-west2'
  _REPO: 'microservices'
  _GKE_CLUSTER: 'lloyds-prod-cluster'
  _NAMESPACE: '{namespace}'

steps:
  # ── Step 1: Lint (Checkstyle) ─────────────────────────────
  - id: 'lint'
    name: 'maven:3.9-eclipse-temurin-21-alpine'
    entrypoint: 'mvn'
    args: ['checkstyle:check', '-B']

  # ── Step 2: Compile ───────────────────────────────────────
  - id: 'compile'
    name: 'maven:3.9-eclipse-temurin-21-alpine'
    entrypoint: 'mvn'
    args: ['compile', '-B']
    waitFor: ['lint']

  # ── Step 3: Unit + Integration Tests ──────────────────────
  - id: 'test'
    name: 'maven:3.9-eclipse-temurin-21-alpine'
    entrypoint: 'mvn'
    args: ['verify', '-B', '-Djacoco.check.lineRatio=0.80']
    waitFor: ['compile']

  # ── Step 4: SAST (SpotBugs + FindSecBugs) ─────────────────
  - id: 'sast'
    name: 'maven:3.9-eclipse-temurin-21-alpine'
    entrypoint: 'mvn'
    args: ['spotbugs:check', '-B']
    waitFor: ['compile']

  # ── Step 5: SCA (OWASP Dependency-Check) ──────────────────
  - id: 'sca'
    name: 'maven:3.9-eclipse-temurin-21-alpine'
    entrypoint: 'mvn'
    args: ['dependency-check:check', '-B', '-DfailBuildOnCVSS=7']
    waitFor: ['compile']

  # ── Step 6: Build Docker Image ────────────────────────────
  - id: 'build-image'
    name: 'gcr.io/cloud-builders/docker'
    args:
      - 'build'
      - '-t'
      - '${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:$SHORT_SHA'
      - '-t'
      - '${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:latest'
      - '.'
    waitFor: ['test', 'sast', 'sca']

  # ── Step 7: Scan Image (Trivy) ────────────────────────────
  - id: 'scan-image'
    name: 'aquasec/trivy:latest'
    args:
      - 'image'
      - '--exit-code'
      - '1'
      - '--severity'
      - 'CRITICAL,HIGH'
      - '${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:$SHORT_SHA'
    waitFor: ['build-image']

  # ── Step 8: Push to Artifact Registry ─────────────────────
  - id: 'push-image'
    name: 'gcr.io/cloud-builders/docker'
    args:
      - 'push'
      - '${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:$SHORT_SHA'
    waitFor: ['scan-image']

  - id: 'push-latest'
    name: 'gcr.io/cloud-builders/docker'
    args:
      - 'push'
      - '${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:latest'
    waitFor: ['scan-image']

  # ── Step 9: Deploy to GKE ─────────────────────────────────
  - id: 'deploy'
    name: 'gcr.io/cloud-builders/gke-deploy'
    args:
      - 'run'
      - '--filename=k8s/'
      - '--image=${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:$SHORT_SHA'
      - '--location=${_REGION}'
      - '--cluster=${_GKE_CLUSTER}'
      - '--namespace=${_NAMESPACE}'
    waitFor: ['push-image']

options:
  machineType: 'E2_HIGHCPU_8'
  logging: CLOUD_LOGGING_ONLY

timeout: '1800s'

images:
  - '${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:$SHORT_SHA'
  - '${_REGION}-docker.pkg.dev/$PROJECT_ID/${_REPO}/${_SERVICE_NAME}:latest'
```

**Pipeline stages per EA:**
```
Lint (Checkstyle) → Compile → Test (JaCoCo ≥80%) → SAST (SpotBugs/FindSecBugs) → SCA (OWASP Dependency-Check) → Docker Build → Image Scan (Trivy) → Push to Artifact Registry → Deploy to GKE
```


---

## SC15: Code Quality

### checkstyle.xml (Google Java Style)
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="error"/>

    <module name="TreeWalker">
        <!-- Google Java Style checks -->
        <module name="OuterTypeFilename"/>
        <module name="IllegalTokenText"/>
        <module name="AvoidStarImport"/>
        <module name="OneTopLevelClass"/>
        <module name="NoLineWrap"/>
        <module name="EmptyBlock"/>
        <module name="NeedBraces"/>
        <module name="LeftCurly"/>
        <module name="RightCurly"/>
        <module name="WhitespaceAround"/>
        <module name="OneStatementPerLine"/>
        <module name="MultipleVariableDeclarations"/>
        <module name="MissingSwitchDefault"/>
        <module name="FallThrough"/>
        <module name="UpperEll"/>
        <module name="ModifierOrder"/>
        <module name="PackageName">
            <property name="format" value="^[a-z]+(\.[a-z][a-z0-9]*)*$"/>
        </module>
        <module name="TypeName"/>
        <module name="MemberName"/>
        <module name="ParameterName"/>
        <module name="LocalVariableName"/>
        <module name="MethodName"/>
        <module name="EmptyCatchBlock">
            <property name="exceptionVariableName" value="expected|ignore"/>
        </module>
        <module name="Indentation">
            <property name="basicOffset" value="4"/>
            <property name="caseIndent" value="4"/>
        </module>
        <module name="LineLength">
            <property name="max" value="120"/>
            <property name="ignorePattern" value="^package.*|^import.*|a]href|href|http://|https://|ftp://"/>
        </module>
    </module>

    <module name="FileTabCharacter"/>
    <module name="NewlineAtEndOfFile"/>
</module>
```

### .editorconfig
```ini
root = true

[*]
indent_style = space
indent_size = 4
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.java]
indent_size = 4
max_line_length = 120

[*.{yml,yaml}]
indent_size = 2

[*.xml]
indent_size = 4

[*.md]
trim_trailing_whitespace = false

[Dockerfile]
indent_size = 4
```

### .gitignore
```gitignore
# Maven
target/
!.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# OS
.DS_Store
Thumbs.db

# Env
.env
*.env.local

# Logs
*.log

# Build
*.jar
*.war
```

### SpotBugs Configuration
SpotBugs + FindSecBugs is configured in the pom.xml (see SC3). Run with:
```bash
./mvnw spotbugs:check
```

### OWASP Dependency-Check
Configured in pom.xml with `failBuildOnCVSS=7`. Run with:
```bash
./mvnw dependency-check:check
```

### JaCoCo Coverage
Configured in pom.xml with 80% minimum line coverage. Run with:
```bash
./mvnw verify
# Report at: target/site/jacoco/index.html
```

---

## SC15a: PII Redaction in Logs

**All services MUST prevent PII from appearing in application logs.**

### Logback Pattern Masking

```xml
<!-- In logback-spring.xml: custom converter for PII masking -->
<conversionRule conversionWord="maskedMsg"
    converterClass="com.lloyds.creditcoach.infrastructure.logging.PiiMaskingConverter" />
```

### PII Masking Converter

```java
package com.lloyds.creditcoach.infrastructure.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Pattern;

public class PiiMaskingConverter extends MessageConverter {

    private static final Pattern EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern NI_NUMBER = Pattern.compile("[A-Z]{2}\\d{6}[A-Z]");
    private static final Pattern SCORE_VALUE = Pattern.compile("score[\":]\\s*\\d{3,4}");

    @Override
    public String convert(ILoggingEvent event) {
        String msg = super.convert(event);
        msg = EMAIL.matcher(msg).replaceAll("***@***.***");
        msg = NI_NUMBER.matcher(msg).replaceAll("**REDACTED-NI**");
        msg = SCORE_VALUE.matcher(msg).replaceAll("score:***");
        return msg;
    }
}
```

### @SensitiveData Annotation (for structured logging)

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveData {
    String replacement() default "***";
}
```

**Rules:**
- NEVER log raw score values, IP addresses, device fingerprints, or customer identifiers
- Use customer ID hash (first 8 chars of SHA-256) for log correlation, not raw UUID
- Structured log fields: use `customerId: "hash:a1b2c3d4"` not the full UUID
- CRA API responses: log status code and latency only, never response body
- Audit trail (separate from app logs): stores full data in encrypted audit table, not logs

---

## SC15b: Java 21 Modern Features

### Virtual Threads (Project Loom)

Enable virtual threads for I/O-bound operations (CRA calls, database queries):

```yaml
# application.yml
spring:
  threads:
    virtual:
      enabled: true  # Spring Boot 3.2+ — uses virtual threads for request handling
```

```java
// For explicit async operations (parallel BFF calls)
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    var scoreFuture = executor.submit(() -> scoreClient.getScore(customerId));
    var factorsFuture = executor.submit(() -> scoreClient.getFactors(customerId));
    // Both run on virtual threads — no platform thread blocking
    return new DashboardResponse(scoreFuture.get(), factorsFuture.get());
}
```

**Benefits for Credit Coach:**
- CRA API calls (2.5s timeout) don't block platform threads
- 10M+ customers with daily refreshes handled without thread pool exhaustion
- Redis cache reads on virtual threads = near-zero overhead

### Records for DTOs (immutable, concise)

```java
public record ScoreResponse(
    UUID customerId,
    String provider,
    int score,
    int maxScore,
    String band,
    String bandLabel,
    Integer previousScore,
    Integer change,
    String changeDirection,
    Instant retrievedAt,
    boolean isStale,
    int dataQualityScore
) {}
```

### Sealed Interfaces for Command/Query Separation

```java
public sealed interface Command<R> permits GrantConsentCommand, WithdrawConsentCommand, RefreshScoreCommand {}
public sealed interface Query<R> permits GetScoreQuery, GetFactorsQuery, GetHistoryQuery {}
```

---

## SC15c: Rate Limiting (Bucket4j)

```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-redis</artifactId>
    <version>8.10.1</version>
</dependency>
```

```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedissonClient redisson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        String customerId = extractCustomerId(request);
        String key = "rate:" + customerId + ":" + (isWriteMethod(request) ? "write" : "read");

        ProxyManager<String> proxyManager = Bucket4jRedisson.casBasedBuilder(redisson).build();
        Bucket bucket = proxyManager.builder()
            .addLimit(isWriteMethod(request)
                ? Bandwidth.simple(20, Duration.ofMinutes(1))   // 20 writes/min
                : Bandwidth.simple(100, Duration.ofMinutes(1))) // 100 reads/min
            .build(key, RecoveryStrategy.RECONSTRUCT);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setHeader("Retry-After", "30");
            response.getWriter().write("{\"type\":\"rate-limited\",\"title\":\"Rate limit exceeded\",\"status\":429}");
        }
    }
}
```

**Configuration (per API spec):**
- Read endpoints: 100 requests/minute per customer
- Write endpoints: 20 requests/minute per customer
- Score refresh: 1 per 24 hours per customer (separate limit)
- Backed by Redis (Memorystore) for distributed rate limiting across pods

---

## SC16: Scaffold Checklist

Before first commit, verify:

| Category | Check | Standard |
|----------|-------|----------|
| Structure | Hexagonal architecture (domain/application/infrastructure/api) | SC2 |
| Build | `./mvnw compile` succeeds with zero warnings | SC3 |
| Health | `GET /actuator/health/liveness` returns 200 | SC6 |
| Health | `GET /actuator/health/readiness` returns 200 | SC6 |
| Logging | Structured JSON with correlationId in staging/prod | SC7 |
| Tracing | OpenTelemetry spans on HTTP + DB (via Java Agent) | SC4 |
| Auth | JWT validation configured (OAuth2 Resource Server) | SC4 |
| Errors | RFC 7807 ProblemDetail on all exceptions | SC5 |
| Docker | Image builds, < 250MB, non-root, health check | SC13 |
| K8s | Deployment + HPA + liveness/readiness/startup probes | SC13 |
| CI | All Cloud Build pipeline stages pass | SC14 |
| Tests | Unit + integration tests compile and pass | SC12 |
| Coverage | JaCoCo ≥80% line coverage | SC15 |
| Lint | `./mvnw checkstyle:check` passes (Google Java Style) | SC15 |
| SAST | `./mvnw spotbugs:check` passes (FindSecBugs) | SC15 |
| SCA | `./mvnw dependency-check:check` passes (CVSS < 7) | SC15 |
| Security | No secrets in code or config files | EA5 |
| Security | CORS configured per environment | SC4 |
| .editorconfig | Consistent formatting enforced | SC15 |
| Flyway | At least V1__init.sql migration present | SC10 |
| Domain | No Spring annotations in domain layer (except JPA) | SC8 |
| CQRS | Command/Query handlers separated | SC9 |

---

## SC17: Quick Start Commands

```bash
# Create new service from scaffold
mkdir {service-name} && cd {service-name}

# Run locally (requires Docker for PostgreSQL + Redis)
docker compose up -d postgres redis
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw verify

# Run specific test types
./mvnw test -Dtest="**/unit/**"                    # Unit only
./mvnw test -Dtest="**/integration/**"             # Integration only

# Code quality
./mvnw checkstyle:check                            # Lint
./mvnw spotbugs:check                              # SAST
./mvnw dependency-check:check                      # SCA

# Build Docker image
docker build -t {service-name}:local .

# Run Docker image
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DB_HOST=host.docker.internal \
  {service-name}:local

# Access
# API:     http://localhost:8080/api/v1/...
# Swagger: http://localhost:8080/swagger-ui.html
# Health:  http://localhost:8080/actuator/health
```

