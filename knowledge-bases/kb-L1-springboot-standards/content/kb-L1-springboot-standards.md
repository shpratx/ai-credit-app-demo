# Spring Boot 3.x / Java 21 API Development Standards — Knowledge Base
### kb-L1-springboot-standards v1.0.0
### Production standards for Spring Boot microservices at Lloyds Banking Group. Code generator agents MUST follow these standards.

---

## SB1: Technology Stack

| Component | Technology | Version | Notes |
|-----------|-----------|---------|-------|
| Language | Java | 21 (LTS) | Records, sealed classes, pattern matching, virtual threads |
| Runtime | JVM | 21 | GraalVM-compatible, `-XX:+UseZGC` for low-latency |
| Framework | Spring Boot | 3.3.x | Spring Framework 6.1, Jakarta EE 10 namespace |
| CQRS | Custom Handler Pattern | — | `CommandHandler<C,R>` / `QueryHandler<Q,R>` interfaces (no MediatR equivalent needed) |
| Validation | Jakarta Bean Validation | 3.0 | Hibernate Validator 8.x implementation |
| ORM | Spring Data JPA | 3.3.x | Hibernate 6.4, JPA 3.1 |
| Logging | Logback + Logstash Encoder | 8.0 | Structured JSON, MDC for correlation |
| API Docs | Springdoc OpenAPI | 2.6.0 | OpenAPI 3.1 auto-generation |
| Database | PostgreSQL | 15 | Cloud SQL (GCP), Flyway migrations |
| Testing | JUnit 5 + Mockito | 5.10 / 5.x | AssertJ for fluent assertions, Testcontainers for integration |
| DI | Spring IoC | 6.1 | Constructor injection, `@Component` scanning |
| Resilience | Resilience4j | 2.2.x | Circuit breaker, retry, rate limiter, time limiter |
| Messaging | Spring Cloud GCP Pub/Sub | 5.8.x | Event-driven async communication |
| Caching | Spring Cache + Redis | — | Memorystore (GCP) |
| Security | Spring Security OAuth2 Resource Server | 6.3.x | JWT validation, method-level auth |

---

## SB2: Project Structure

```
{service-name}/
├── src/main/java/com/lloyds/{servicename}/
│   ├── Application.java                          # @SpringBootApplication entry point
│   ├── domain/
│   │   ├── model/                                # Domain entities (one file per entity)
│   │   │   └── BaseEntity.java                   # UUID id, audit columns, @Version
│   │   ├── valueobject/                          # Value objects (immutable)
│   │   ├── event/                                # Domain events (marker interface)
│   │   ├── port/                                 # Repository interfaces (outbound ports)
│   │   ├── service/                              # Domain services (pure business logic)
│   │   └── exception/                            # Domain exceptions
│   ├── application/
│   │   ├── command/                              # Commands + CommandHandlers
│   │   ├── query/                                # Queries + QueryHandlers
│   │   ├── dto/                                  # Data transfer objects (Java records)
│   │   ├── mapper/                               # MapStruct mappers
│   │   ├── service/                              # Application services (orchestration)
│   │   └── validation/                           # Custom Jakarta validators
│   ├── infrastructure/
│   │   ├── persistence/
│   │   │   └── repository/                       # JPA repository implementations
│   │   ├── client/                               # External service clients
│   │   ├── messaging/
│   │   │   ├── publisher/                        # Pub/Sub publishers
│   │   │   └── subscriber/                       # Pub/Sub subscribers
│   │   ├── cache/                                # Redis cache implementations
│   │   └── config/                               # @Configuration classes
│   └── api/
│       ├── controller/                           # @RestController classes
│       ├── dto/                                  # API-specific request/response DTOs
│       └── filter/                               # Servlet filters (correlation, logging, exceptions)
├── src/main/resources/
│   ├── application.yml                           # Base config
│   ├── application-dev.yml                       # Dev profile
│   ├── application-prod.yml                      # Prod profile
│   ├── logback-spring.xml                        # Logging config
│   └── db/migration/                             # Flyway SQL migrations
├── src/test/java/com/lloyds/{servicename}/
│   ├── unit/                                     # Unit tests (Mockito)
│   ├── integration/                              # Integration tests (Testcontainers)
│   └── contract/                                 # Contract tests
├── pom.xml
├── Dockerfile
├── cloudbuild.yaml
└── k8s/                                          # Kubernetes manifests
```

**RULES:**
- Strict dependency direction: `domain` ← `application` ← `infrastructure` ← `api`
- `domain` package has ZERO framework dependencies (no Spring, no JPA annotations)
- `application` references `domain` only
- `infrastructure` implements `domain` ports (interfaces)
- `api` is the composition root — wires everything together
- One class per file
- Package-private classes where possible; public only when needed cross-package

---

## SB3: Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Packages | `lowercase.dot.separated` | `com.lloyds.loanservice.application.command` |
| Classes | PascalCase | `LoanApplicationService` |
| Interfaces | PascalCase (no `I` prefix) | `LoanRepository` |
| Methods | camelCase | `calculateInterestRate()` |
| Fields | camelCase | `requestedAmount` |
| Constants | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Enums | PascalCase (singular) | `ApplicationStatus.SUBMITTED` |
| DTOs (Response) | PascalCase + `Response` suffix | `LoanApplicationResponse` |
| DTOs (Request) | PascalCase + `Request` suffix | `CreateLoanRequest` |
| Commands | `{Verb}{Entity}Command` | `CreateLoanApplicationCommand` |
| Queries | `Get{Entity}Query` | `GetLoanApplicationQuery` |
| Command Handlers | `{Command}Handler` | `CreateLoanApplicationCommandHandler` |
| Query Handlers | `{Query}Handler` | `GetLoanApplicationQueryHandler` |
| Controllers | `{Entity}Controller` | `LoanApplicationController` |
| Configurations | `{Concern}Config` | `SecurityConfig` |
| Repositories | `{Entity}Repository` | `LoanApplicationRepository` |
| Mappers | `{Entity}Mapper` | `LoanApplicationMapper` |
| Filters | `{Concern}Filter` | `CorrelationIdFilter` |
| Exceptions | `{Noun}Exception` | `EntityNotFoundException` |
| Test classes | `{ClassUnderTest}Test` | `CreateLoanApplicationCommandHandlerTest` |
| Test methods | `should_{expected}_when_{condition}` | `should_createLoan_when_validRequest()` |

**RULES:**
- Java records for all DTOs, commands, and queries (immutable by design)
- No abbreviations in class names (use `LoanApplication` not `LoanApp`)
- Boolean methods: `is` or `has` prefix (`isEligible()`, `hasActiveLoans()`)
- Collections: plural nouns (`List<Loan> loans`)
- Optional: use `Optional<T>` return type, never `null` for query results

---

## SB4: Entity Pattern

### BaseEntity (all entities extend this)
```java
package com.lloyds.{servicename}.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Base entity with UUID primary key, optimistic locking, and audit columns.
 * All domain entities MUST extend this class.
 */
public abstract class BaseEntity {

    private UUID id;
    private Long version;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private boolean isDeleted;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.isDeleted = false;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}
```

### JPA Entity (Infrastructure layer — maps domain model to DB)
```java
package com.lloyds.{servicename}.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
@EntityListeners(AuditingEntityListener.class)
public class LoanApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "requested_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "requested_tenure_months", nullable = false)
    private Integer requestedTenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // ── Audit Columns (Spring Data Auditing) ──────────────────
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // ── Relationships ─────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<OfferEntity> offers = new java.util.ArrayList<>();

    // Getters and setters omitted for brevity — use Lombok @Getter @Setter in implementation
}
```

### Auditing Configuration
```java
package com.lloyds.{servicename}.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName)
            .or(() -> Optional.of("system"));
    }
}
```

**RULES:**
- `UUID` for ALL primary keys — never `Long` or `Integer`
- `@Version` on EVERY entity for optimistic locking
- Audit columns (`createdAt`, `createdBy`, `updatedAt`, `updatedBy`) on EVERY entity — use Spring Data Auditing
- Soft delete via `isDeleted` boolean flag — never hard delete
- Use `@Enumerated(EnumType.STRING)` — never `EnumType.ORDINAL`
- `BigDecimal` for all monetary values — never `double` or `float`
- `Instant` for all timestamps — never `Date` or `LocalDateTime`
- Lazy loading for `@ManyToOne` and `@OneToMany` relationships
- Domain model in `domain/model/` has NO JPA annotations — JPA entities live in `infrastructure/persistence/entity/`
- Map between domain model and JPA entity via MapStruct mapper

---

## SB5: CQRS Handler Pattern

### Handler Interfaces
```java
package com.lloyds.{servicename}.application.command;

/**
 * Generic command handler interface.
 * @param <C> Command type
 * @param <R> Result type
 */
public interface CommandHandler<C, R> {
    R handle(C command);
}
```

```java
package com.lloyds.{servicename}.application.query;

/**
 * Generic query handler interface.
 * @param <Q> Query type
 * @param <R> Result type
 */
public interface QueryHandler<Q, R> {
    R handle(Q query);
}
```

### Command Record
```java
package com.lloyds.{servicename}.application.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanApplicationCommand(
    @NotNull UUID customerId,
    @NotNull UUID productId,
    @NotNull @DecimalMin("1000.00") BigDecimal requestedAmount,
    @NotNull @Min(6) Integer requestedTenureMonths,
    @NotBlank String fullName
) {}
```

### Command Handler Implementation
```java
package com.lloyds.{servicename}.application.command;

import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.application.mapper.LoanApplicationMapper;
import com.lloyds.{servicename}.domain.exception.EntityNotFoundException;
import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import com.lloyds.{servicename}.domain.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CreateLoanApplicationCommandHandler
        implements CommandHandler<CreateLoanApplicationCommand, LoanApplicationResponse> {

    private static final Logger log = LoggerFactory.getLogger(CreateLoanApplicationCommandHandler.class);

    private final LoanApplicationRepository loanApplicationRepository;
    private final ProductRepository productRepository;
    private final LoanApplicationMapper mapper;

    public CreateLoanApplicationCommandHandler(
            LoanApplicationRepository loanApplicationRepository,
            ProductRepository productRepository,
            LoanApplicationMapper mapper) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public LoanApplicationResponse handle(CreateLoanApplicationCommand command) {
        log.info("Creating loan application for customer: {}", command.customerId());

        var product = productRepository.findById(command.productId())
            .orElseThrow(() -> new EntityNotFoundException("Product", command.productId()));

        var loanApplication = LoanApplication.create(
            command.customerId(),
            product,
            command.requestedAmount(),
            command.requestedTenureMonths(),
            command.fullName()
        );

        var saved = loanApplicationRepository.save(loanApplication);
        log.info("Loan application created: {}", saved.getId());

        return mapper.toResponse(saved);
    }
}
```

### Query Record
```java
package com.lloyds.{servicename}.application.query;

import java.util.UUID;

public record GetLoanApplicationQuery(UUID applicationId) {}
```

### Query Handler Implementation
```java
package com.lloyds.{servicename}.application.query;

import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.application.mapper.LoanApplicationMapper;
import com.lloyds.{servicename}.domain.exception.EntityNotFoundException;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetLoanApplicationQueryHandler
        implements QueryHandler<GetLoanApplicationQuery, LoanApplicationResponse> {

    private static final Logger log = LoggerFactory.getLogger(GetLoanApplicationQueryHandler.class);

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationMapper mapper;

    public GetLoanApplicationQueryHandler(
            LoanApplicationRepository loanApplicationRepository,
            LoanApplicationMapper mapper) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public LoanApplicationResponse handle(GetLoanApplicationQuery query) {
        log.debug("Fetching loan application: {}", query.applicationId());

        var application = loanApplicationRepository.findById(query.applicationId())
            .orElseThrow(() -> new EntityNotFoundException("LoanApplication", query.applicationId()));

        return mapper.toResponse(application);
    }
}
```

**RULES:**
- One handler per command/query — Single Responsibility Principle
- Commands and queries are Java `record` types (immutable)
- Handlers are `@Service` beans — auto-discovered by Spring component scanning
- Command handlers use `@Transactional` (read-write)
- Query handlers use `@Transactional(readOnly = true)`
- Handlers MUST be < 50 lines — extract complex logic to domain services
- Constructor injection only — no `@Autowired` on fields
- Throw `EntityNotFoundException` for 404, `BusinessRuleException` for 422
- Log at method entry (INFO) and exit (DEBUG)
- Never catch and swallow exceptions — let them propagate to `@ControllerAdvice`

---

## SB6: Controller Pattern

```java
package com.lloyds.{servicename}.api.controller;

import com.lloyds.{servicename}.api.dto.ApiResponse;
import com.lloyds.{servicename}.application.command.CreateLoanApplicationCommand;
import com.lloyds.{servicename}.application.command.CreateLoanApplicationCommandHandler;
import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.application.query.GetLoanApplicationQuery;
import com.lloyds.{servicename}.application.query.GetLoanApplicationQueryHandler;
import com.lloyds.{servicename}.application.query.ListLoanApplicationsQuery;
import com.lloyds.{servicename}.application.query.ListLoanApplicationsQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loan-applications")
@Tag(name = "Loan Applications", description = "Loan application management endpoints")
public class LoanApplicationController {

    private static final Logger log = LoggerFactory.getLogger(LoanApplicationController.class);

    private final CreateLoanApplicationCommandHandler createHandler;
    private final GetLoanApplicationQueryHandler getHandler;
    private final ListLoanApplicationsQueryHandler listHandler;

    public LoanApplicationController(
            CreateLoanApplicationCommandHandler createHandler,
            GetLoanApplicationQueryHandler getHandler,
            ListLoanApplicationsQueryHandler listHandler) {
        this.createHandler = createHandler;
        this.getHandler = getHandler;
        this.listHandler = listHandler;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new loan application")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> create(
            @Valid @RequestBody CreateLoanApplicationCommand command) {
        log.info("POST /api/v1/loan-applications");
        var result = createHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get loan application by ID")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/loan-applications/{}", id);
        var result = getHandler.handle(new GetLoanApplicationQuery(id));
        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all loan applications (admin only)")
    public ResponseEntity<ApiResponse<java.util.List<LoanApplicationResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/loan-applications?page={}&size={}", page, size);
        var result = listHandler.handle(new ListLoanApplicationsQuery(page, size));
        return ResponseEntity.ok(ApiResponse.of(result));
    }
}
```

**RULES:**
- `@RestController` + `@RequestMapping("/api/v1/{plural-resource}")` on every controller
- Route pattern: lowercase, hyphen-separated, plural nouns (`/api/v1/loan-applications`)
- Controllers are THIN — delegate ALL logic to command/query handlers
- `@Valid` on ALL `@RequestBody` parameters — triggers Jakarta Bean Validation
- Return `ResponseEntity<ApiResponse<T>>` — never raw objects
- Use `@PreAuthorize` for method-level authorization
- `@PathVariable UUID id` — use UUID type directly (Spring auto-converts)
- Return `201 Created` for POST creation, `200 OK` for GET/PUT, `204 No Content` for DELETE
- NO business logic in controllers — not even null checks
- One controller per aggregate/resource
- Log the HTTP method and path at INFO level
- Use Springdoc `@Operation` annotations for API documentation

---

## SB7: Exception Handling (@ControllerAdvice)

### Domain Exceptions
```java
package com.lloyds.{servicename}.domain.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, UUID id) {
        super(String.format("%s with id '%s' not found", entityName, id));
    }
}
```

```java
package com.lloyds.{servicename}.domain.exception;

public class BusinessRuleException extends RuntimeException {
    private final String ruleCode;

    public BusinessRuleException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() { return ruleCode; }
}
```

### Global Exception Handler (RFC 7807)
```java
package com.lloyds.{servicename}.api.filter;

import com.lloyds.{servicename}.domain.exception.BusinessRuleException;
import com.lloyds.{servicename}.domain.exception.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/not-found"));
        problem.setTitle("Resource Not Found");
        enrichWithCorrelation(problem);
        return problem;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex) {
        log.warn("Business rule violation [{}]: {}", ex.getRuleCode(), ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setType(URI.create("https://api.lloyds.com/errors/business-rule-violation"));
        problem.setTitle("Business Rule Violation");
        problem.setProperty("ruleCode", ex.getRuleCode());
        enrichWithCorrelation(problem);
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {} errors", ex.getBindingResult().getErrorCount());
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://api.lloyds.com/errors/validation-failed"));
        problem.setTitle("Validation Failed");
        problem.setDetail("One or more fields failed validation");

        List<Map<String, String>> violations = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "invalid",
                "rejectedValue", String.valueOf(error.getRejectedValue())
            ))
            .toList();

        problem.setProperty("violations", violations);
        enrichWithCorrelation(problem);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://api.lloyds.com/errors/constraint-violation"));
        problem.setTitle("Constraint Violation");
        problem.setDetail(ex.getMessage());

        List<Map<String, String>> violations = ex.getConstraintViolations().stream()
            .map(v -> Map.of(
                "field", v.getPropertyPath().toString(),
                "message", v.getMessage()
            ))
            .toList();

        problem.setProperty("violations", violations);
        enrichWithCorrelation(problem);
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Insufficient permissions");
        problem.setType(URI.create("https://api.lloyds.com/errors/access-denied"));
        problem.setTitle("Access Denied");
        enrichWithCorrelation(problem);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        var problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setType(URI.create("https://api.lloyds.com/errors/internal-error"));
        problem.setTitle("Internal Server Error");
        enrichWithCorrelation(problem);
        return problem;
    }

    private void enrichWithCorrelation(ProblemDetail problem) {
        problem.setProperty("correlationId", MDC.get("correlationId"));
        problem.setProperty("timestamp", Instant.now().toString());
    }
}
```

**RULES:**
- Single `@RestControllerAdvice` handles ALL exceptions globally
- Use Spring's `ProblemDetail` (RFC 7807) — never custom error DTOs
- Exception mapping: `EntityNotFoundException` → 404, `BusinessRuleException` → 422, `MethodArgumentNotValidException` → 400, `ConstraintViolationException` → 400, `AccessDeniedException` → 403, all others → 500
- ALWAYS include `correlationId` from MDC in error responses
- ALWAYS include `timestamp` in error responses
- NEVER expose stack traces in production (log them at ERROR, return generic message)
- Log at WARN for client errors (4xx), ERROR for server errors (5xx)
- Include `violations` array for validation errors (field + message)

---

## SB8: Validation Pattern

### Request DTO with Jakarta Bean Validation
```java
package com.lloyds.{servicename}.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanRequest(
    @NotNull(message = "Customer ID is required")
    UUID customerId,

    @NotNull(message = "Product ID is required")
    UUID productId,

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum loan amount is £1,000")
    @DecimalMax(value = "500000.00", message = "Maximum loan amount is £500,000")
    BigDecimal requestedAmount,

    @NotNull(message = "Tenure is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    Integer requestedTenureMonths,

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 200, message = "Full name must be between 2 and 200 characters")
    String fullName,

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber
) {}
```

### Custom Validator
```java
package com.lloyds.{servicename}.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SortCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortCode {
    String message() default "Invalid sort code format (expected: XX-XX-XX)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

```java
package com.lloyds.{servicename}.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortCodeValidator implements ConstraintValidator<ValidSortCode, String> {

    private static final java.util.regex.Pattern SORT_CODE_PATTERN =
        java.util.regex.Pattern.compile("^\\d{2}-\\d{2}-\\d{2}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Use @NotNull for null checks
        return SORT_CODE_PATTERN.matcher(value).matches();
    }
}
```

### Validation Groups
```java
package com.lloyds.{servicename}.application.validation;

public interface ValidationGroups {
    interface OnCreate {}
    interface OnUpdate {}
}
```

```java
// Usage in DTO
public record UpdateLoanRequest(
    @Null(groups = ValidationGroups.OnCreate.class)
    @NotNull(groups = ValidationGroups.OnUpdate.class)
    UUID id,

    @NotNull
    BigDecimal requestedAmount
) {}
```

```java
// Usage in controller
@PostMapping
public ResponseEntity<?> create(
        @Validated(ValidationGroups.OnCreate.class) @RequestBody UpdateLoanRequest request) {
    // ...
}
```

**RULES:**
- Validate at controller entry with `@Valid` or `@Validated` — first line of defence
- Validate business rules in handlers — throw `BusinessRuleException` for domain violations
- NEVER trust client input — validate everything, even UUIDs
- Use `@NotNull` for required fields, `@NotBlank` for required strings (rejects empty/whitespace)
- Custom validators for domain-specific formats (sort codes, account numbers, NI numbers)
- Validation messages MUST be user-friendly (no technical jargon)
- Use validation groups when create/update have different rules
- Null in custom validators means "not my concern" — combine with `@NotNull`
- `@Size` for string length limits — always set max to prevent overflow
- `@DecimalMin`/`@DecimalMax` for monetary ranges — use `String` value parameter

---

## SB9: Repository Pattern

### Domain Port (Interface)
```java
package com.lloyds.{servicename}.domain.port;

import com.lloyds.{servicename}.domain.model.LoanApplication;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for loan application persistence.
 * Defined in domain — implemented in infrastructure.
 */
public interface LoanApplicationRepository {
    Optional<LoanApplication> findById(UUID id);
    LoanApplication save(LoanApplication loanApplication);
    void deleteById(UUID id);
    java.util.List<LoanApplication> findByCustomerId(UUID customerId);
    org.springframework.data.domain.Page<LoanApplication> findAll(org.springframework.data.domain.Pageable pageable);
}
```

### Spring Data JPA Repository (Infrastructure)
```java
package com.lloyds.{servicename}.infrastructure.persistence.repository;

import com.lloyds.{servicename}.infrastructure.persistence.entity.LoanApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanApplicationJpaRepository
        extends JpaRepository<LoanApplicationEntity, UUID>,
                JpaSpecificationExecutor<LoanApplicationEntity> {

    // Derived query — Spring Data generates SQL
    List<LoanApplicationEntity> findByCustomerIdAndIsDeletedFalse(UUID customerId);

    // Custom JPQL query
    @Query("SELECT la FROM LoanApplicationEntity la WHERE la.status = :status AND la.isDeleted = false")
    List<LoanApplicationEntity> findByStatus(@Param("status") String status);

    // Native query (use sparingly)
    @Query(value = "SELECT * FROM loan_applications WHERE created_at > :since AND is_deleted = false",
           nativeQuery = true)
    List<LoanApplicationEntity> findRecentApplications(@Param("since") java.time.Instant since);

    // Existence check (efficient — no full entity load)
    boolean existsByCustomerIdAndStatusAndIsDeletedFalse(UUID customerId, String status);

    // Soft delete override
    @Override
    default void deleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }
}
```

### Specification for Dynamic Queries
```java
package com.lloyds.{servicename}.infrastructure.persistence.repository;

import com.lloyds.{servicename}.infrastructure.persistence.entity.LoanApplicationEntity;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class LoanApplicationSpecifications {

    private LoanApplicationSpecifications() {}

    public static Specification<LoanApplicationEntity> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<LoanApplicationEntity> hasCustomerId(UUID customerId) {
        return (root, query, cb) -> customerId == null ? null : cb.equal(root.get("customerId"), customerId);
    }

    public static Specification<LoanApplicationEntity> hasStatus(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<LoanApplicationEntity> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("requestedAmount"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("requestedAmount"), min);
            return cb.lessThanOrEqualTo(root.get("requestedAmount"), max);
        };
    }

    public static Specification<LoanApplicationEntity> createdAfter(Instant since) {
        return (root, query, cb) -> since == null ? null : cb.greaterThan(root.get("createdAt"), since);
    }
}
```

### Repository Adapter (bridges domain port to JPA)
```java
package com.lloyds.{servicename}.infrastructure.persistence.repository;

import com.lloyds.{servicename}.application.mapper.LoanApplicationMapper;
import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepository {

    private final LoanApplicationJpaRepository jpaRepository;
    private final LoanApplicationMapper mapper;

    public LoanApplicationRepositoryAdapter(
            LoanApplicationJpaRepository jpaRepository,
            LoanApplicationMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LoanApplication> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public LoanApplication save(LoanApplication loanApplication) {
        var entity = mapper.toEntity(loanApplication);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id); // Soft delete via override
    }

    @Override
    public List<LoanApplication> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdAndIsDeletedFalse(customerId)
            .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Page<LoanApplication> findAll(Pageable pageable) {
        return jpaRepository.findAll(
            LoanApplicationSpecifications.notDeleted(), pageable
        ).map(mapper::toDomain);
    }
}
```

**RULES:**
- One repository per aggregate root — not per entity
- Domain port (interface) in `domain/port/` — no Spring/JPA imports
- JPA repository in `infrastructure/persistence/repository/` — extends `JpaRepository` + `JpaSpecificationExecutor`
- Adapter pattern bridges domain port to JPA repository
- No raw SQL in handlers — use derived queries, JPQL, or Specifications
- Use `Specification` for dynamic/complex filters (search endpoints)
- Always filter by `isDeleted = false` — use global `@Where` or Specifications
- `@Query` for complex JPQL — prefer derived queries for simple cases
- Pagination via `Pageable` parameter — never load unbounded lists
- Soft delete: override `deleteById` to set `isDeleted = true`

---

## SB10: DTO Pattern

### Response DTOs (Java Records)
```java
package com.lloyds.{servicename}.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LoanApplicationResponse(
    UUID id,
    UUID customerId,
    UUID productId,
    BigDecimal requestedAmount,
    Integer requestedTenureMonths,
    String status,
    String fullName,
    String email,
    Instant createdAt,
    Instant updatedAt,
    ProductSummaryResponse product,
    List<OfferResponse> offers
) {}

public record ProductSummaryResponse(
    UUID id,
    String name,
    String type,
    BigDecimal minAmount,
    BigDecimal maxAmount
) {}

public record OfferResponse(
    UUID id,
    BigDecimal approvedAmount,
    @JsonProperty("interestRate")
    BigDecimal annualInterestRate,
    Integer tenureMonths,
    BigDecimal monthlyPayment,
    Instant expiresAt
) {}
```

### MapStruct Mapper
```java
package com.lloyds.{servicename}.application.mapper;

import com.lloyds.{servicename}.application.dto.LoanApplicationResponse;
import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.infrastructure.persistence.entity.LoanApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoanApplicationMapper {

    LoanApplicationResponse toResponse(LoanApplication domain);

    @Mapping(target = "version", ignore = true)
    LoanApplicationEntity toEntity(LoanApplication domain);

    LoanApplication toDomain(LoanApplicationEntity entity);
}
```

### Paginated Response DTO
```java
package com.lloyds.{servicename}.application.dto;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> data,
    PaginationMeta pagination
) {
    public record PaginationMeta(
        int page,
        int size,
        long total,
        int totalPages
    ) {}

    public static <T> PaginatedResponse<T> of(
            List<T> data, int page, int size, long total, int totalPages) {
        return new PaginatedResponse<>(data, new PaginationMeta(page, size, total, totalPages));
    }
}
```

**RULES:**
- NEVER expose JPA entities in API responses — always map to response DTOs
- All DTOs are Java `record` types — immutable by design
- Response DTOs use `Response` suffix, request DTOs use `Request` suffix
- Use `@JsonProperty` only when JSON field name differs from Java field name
- Nested DTOs for complex responses (e.g., `ProductSummaryResponse` inside `LoanApplicationResponse`)
- MapStruct for all entity ↔ DTO mapping — no manual mapping code
- MapStruct mapper is a Spring bean (`componentModel = SPRING`)
- Null fields are omitted from JSON output (configured in `JacksonConfig`)
- `BigDecimal` for monetary values — serialized as numbers in JSON
- `Instant` for timestamps — serialized as ISO 8601 strings
- No business logic in DTOs — they are pure data carriers

---

## SB11: Configuration Pattern

### Type-Safe Configuration Properties
```java
package com.lloyds.{servicename}.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.loan")
public record LoanServiceProperties(
    @NotNull @Positive Integer maxTenureMonths,
    @NotNull @Positive java.math.BigDecimal maxLoanAmount,
    @NotNull @Positive java.math.BigDecimal minLoanAmount,
    @NotNull Duration offerExpiryDuration,
    @NotBlank String defaultCurrency
) {}
```

### Enabling Configuration Properties
```java
package com.lloyds.{servicename}.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoanServiceProperties.class)
public class AppConfig {
}
```

### application.yml (Base)
```yaml
spring:
  application:
    name: loan-service
  datasource:
    url: jdbc:postgresql://localhost:5432/loandb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        default_schema: public
        jdbc.time_zone: UTC
  flyway:
    enabled: true
    locations: classpath:db/migration
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI}
          audiences: ${JWT_AUDIENCE}

app:
  loan:
    max-tenure-months: 360
    max-loan-amount: 500000.00
    min-loan-amount: 1000.00
    offer-expiry-duration: P30D
    default-currency: GBP
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000}

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

server:
  port: 8080
  shutdown: graceful

logging:
  level:
    root: INFO
    com.lloyds: DEBUG
    org.springframework.security: WARN
```

### Profile-Specific Configuration (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql:///${DB_NAME}?cloudSqlInstance=${CLOUD_SQL_INSTANCE}&socketFactory=com.google.cloud.sql.postgres.SocketFactory
  jpa:
    show-sql: false

logging:
  level:
    root: WARN
    com.lloyds: INFO

management:
  endpoint:
    health:
      show-details: never
```

### Profile-Specific Beans
```java
package com.lloyds.{servicename}.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ExternalServiceConfig {

    @Bean
    @Profile("!prod")
    public CreditCheckClient mockCreditCheckClient() {
        return new MockCreditCheckClient();
    }

    @Bean
    @Profile("prod")
    public CreditCheckClient realCreditCheckClient(CreditCheckProperties properties) {
        return new HttpCreditCheckClient(properties);
    }
}
```

**RULES:**
- NO hardcoded values — all configuration externalized to YAML or environment variables
- Use `@ConfigurationProperties` for type-safe, validated configuration — never `@Value` for complex config
- Secrets via GCP Secret Manager — NEVER in YAML files or source code
- Environment variables for secrets: `${DB_PASSWORD}`, `${JWT_ISSUER_URI}`
- `@Validated` on configuration properties — fail fast on startup if config is invalid
- Use Java `record` for `@ConfigurationProperties` (immutable config)
- `@Profile` for environment-specific beans (mock vs real external services)
- `spring.jpa.open-in-view: false` — always disable OSIV (prevents lazy loading in controllers)
- `spring.jpa.hibernate.ddl-auto: validate` — Flyway manages schema, Hibernate only validates
- Graceful shutdown enabled (`server.shutdown: graceful`)
- Actuator health probes enabled for Kubernetes liveness/readiness

---

## SB12: Testing Standards

### Unit Test (JUnit 5 + Mockito + AssertJ)
```java
package com.lloyds.{servicename}.unit;

import com.lloyds.{servicename}.application.command.CreateLoanApplicationCommand;
import com.lloyds.{servicename}.application.command.CreateLoanApplicationCommandHandler;
import com.lloyds.{servicename}.application.mapper.LoanApplicationMapper;
import com.lloyds.{servicename}.domain.exception.EntityNotFoundException;
import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.domain.model.Product;
import com.lloyds.{servicename}.domain.port.LoanApplicationRepository;
import com.lloyds.{servicename}.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateLoanApplicationCommandHandler")
class CreateLoanApplicationCommandHandlerTest {

    @Mock private LoanApplicationRepository loanApplicationRepository;
    @Mock private ProductRepository productRepository;
    @Mock private LoanApplicationMapper mapper;

    private CreateLoanApplicationCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateLoanApplicationCommandHandler(
            loanApplicationRepository, productRepository, mapper);
    }

    @Test
    @DisplayName("should create loan application when valid command")
    void should_createLoanApplication_when_validCommand() {
        // Arrange
        var productId = UUID.randomUUID();
        var command = new CreateLoanApplicationCommand(
            UUID.randomUUID(), productId, new BigDecimal("50000"), 24, "John Smith");
        var product = TestDataBuilder.aProduct().withId(productId).build();
        var savedApplication = TestDataBuilder.aLoanApplication().build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(loanApplicationRepository.save(any())).thenReturn(savedApplication);
        when(mapper.toResponse(savedApplication)).thenReturn(TestDataBuilder.aLoanApplicationResponse());

        // Act
        var result = handler.handle(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedApplication.getId());
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when product not found")
    void should_throwEntityNotFoundException_when_productNotFound() {
        // Arrange
        var command = new CreateLoanApplicationCommand(
            UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("50000"), 24, "John Smith");
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> handler.handle(command))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Product");
    }
}
```

### Integration Test (@SpringBootTest + Testcontainers)
```java
package com.lloyds.{servicename}.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }
}
```

```java
package com.lloyds.{servicename}.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("Loan Application API Integration Tests")
class LoanApplicationApiIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("POST /api/v1/loan-applications - should create and return 201")
    void should_createLoanApplication_when_validRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loan-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "550e8400-e29b-41d4-a716-446655440000",
                        "productId": "660e8400-e29b-41d4-a716-446655440001",
                        "requestedAmount": 50000.00,
                        "requestedTenureMonths": 24,
                        "fullName": "John Smith"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    @DisplayName("POST /api/v1/loan-applications - should return 401 when unauthenticated")
    void should_return401_when_unauthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/loan-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }
}
```

### Slice Test (@WebMvcTest)
```java
package com.lloyds.{servicename}.unit;

import com.lloyds.{servicename}.api.controller.LoanApplicationController;
import com.lloyds.{servicename}.application.command.CreateLoanApplicationCommandHandler;
import com.lloyds.{servicename}.application.query.GetLoanApplicationQueryHandler;
import com.lloyds.{servicename}.application.query.ListLoanApplicationsQueryHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanApplicationController.class)
class LoanApplicationControllerSliceTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private CreateLoanApplicationCommandHandler createHandler;
    @MockBean private GetLoanApplicationQueryHandler getHandler;
    @MockBean private ListLoanApplicationsQueryHandler listHandler;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void should_return200_when_getById() throws Exception {
        // Mock handler response...
        mockMvc.perform(get("/api/v1/loan-applications/{id}", java.util.UUID.randomUUID()))
            .andExpect(status().isOk());
    }
}
```

### Slice Test (@DataJpaTest)
```java
package com.lloyds.{servicename}.unit;

import com.lloyds.{servicename}.infrastructure.persistence.entity.LoanApplicationEntity;
import com.lloyds.{servicename}.infrastructure.persistence.repository.LoanApplicationJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LoanApplicationJpaRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private LoanApplicationJpaRepository repository;

    @Test
    void should_findByCustomerId_when_applicationExists() {
        // Arrange
        var entity = TestDataBuilder.aLoanApplicationEntity().build();
        entityManager.persistAndFlush(entity);

        // Act
        var results = repository.findByCustomerIdAndIsDeletedFalse(entity.getCustomerId());

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCustomerId()).isEqualTo(entity.getCustomerId());
    }
}
```

### Test Data Builder
```java
package com.lloyds.{servicename}.unit;

import com.lloyds.{servicename}.domain.model.LoanApplication;
import com.lloyds.{servicename}.domain.model.Product;
import com.lloyds.{servicename}.infrastructure.persistence.entity.LoanApplicationEntity;

import java.math.BigDecimal;
import java.util.UUID;

public final class TestDataBuilder {

    private TestDataBuilder() {}

    public static LoanApplicationBuilder aLoanApplication() {
        return new LoanApplicationBuilder();
    }

    public static class LoanApplicationBuilder {
        private UUID id = UUID.randomUUID();
        private UUID customerId = UUID.randomUUID();
        private BigDecimal amount = new BigDecimal("50000.00");
        private String fullName = "John Smith";

        public LoanApplicationBuilder withId(UUID id) { this.id = id; return this; }
        public LoanApplicationBuilder withCustomerId(UUID id) { this.customerId = id; return this; }
        public LoanApplicationBuilder withAmount(BigDecimal amount) { this.amount = amount; return this; }

        public LoanApplication build() {
            var app = new LoanApplication();
            app.setId(id);
            app.setCustomerId(customerId);
            app.setRequestedAmount(amount);
            app.setFullName(fullName);
            return app;
        }
    }
}
```

**RULES:**
- 80% code coverage minimum (enforced by JaCoCo in CI)
- Test naming: `should_{expected}_when_{condition}` — descriptive, readable
- Use `@DisplayName` for human-readable test descriptions
- Unit tests: `@ExtendWith(MockitoExtension.class)` — mock all dependencies
- Integration tests: `@SpringBootTest` + Testcontainers — real database
- Slice tests: `@WebMvcTest` for controllers, `@DataJpaTest` for repositories
- Use `@WithMockUser` for security context in tests
- Test BEHAVIOUR not implementation — don't test private methods
- Use Builder pattern for test data — never hardcode UUIDs inline
- No `Thread.sleep()` — use `Awaitility` for async assertions
- AssertJ for all assertions — never raw JUnit `assertEquals`
- One assertion concept per test method (multiple `assertThat` calls OK if same concept)
- Synthetic test data only — NEVER use real customer PII

---

## SB13: Logging Standards

### logback-spring.xml (Structured JSON)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <!-- ── Console (dev) ─────────────────────────────────── -->
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{correlationId}] - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>

    <!-- ── JSON (staging, prod) ──────────────────────────── -->
    <springProfile name="staging,prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <includeMdcKeyName>correlationId</includeMdcKeyName>
                <includeMdcKeyName>userId</includeMdcKeyName>
                <fieldNames>
                    <timestamp>timestamp</timestamp>
                    <version>[ignore]</version>
                </fieldNames>
                <timeZone>UTC</timeZone>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="JSON"/>
        </root>
    </springProfile>

    <!-- ── Logger levels ─────────────────────────────────── -->
    <logger name="com.lloyds" level="INFO"/>
    <logger name="org.springframework.security" level="WARN"/>
    <logger name="org.hibernate.SQL" level="WARN"/>
    <logger name="org.hibernate.type.descriptor.sql" level="WARN"/>
</configuration>
```

### Correlation ID Filter (MDC)
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

### Logging Usage in Handlers
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CreateLoanApplicationCommandHandler implements CommandHandler<CreateLoanApplicationCommand, LoanApplicationResponse> {

    private static final Logger log = LoggerFactory.getLogger(CreateLoanApplicationCommandHandler.class);

    @Override
    @Transactional
    public LoanApplicationResponse handle(CreateLoanApplicationCommand command) {
        log.info("Creating loan application for customer: {}", command.customerId());

        // ... business logic ...

        log.info("Loan application created successfully: id={}, status={}", saved.getId(), saved.getStatus());
        return mapper.toResponse(saved);
    }
}
```

### Log Level Guidelines

| Level | Usage | Example |
|-------|-------|---------|
| ERROR | System failure requiring immediate attention | Database connection lost, external service unreachable after retries |
| WARN | Recoverable issue, degraded operation | Circuit breaker opened, cache miss fallback, retry attempt |
| INFO | Business events, request lifecycle | Application created, payment processed, user authenticated |
| DEBUG | Developer diagnostics (dev/staging only) | SQL queries, request/response bodies, intermediate calculations |
| TRACE | Never in production | Framework internals only |

**RULES:**
- NEVER log PII (names, emails, addresses, account numbers, NI numbers)
- ALWAYS include `correlationId` via MDC — automatically added to every log line
- Use parameterized messages: `log.info("Created: {}", id)` — NEVER string concatenation
- Log at method entry (INFO) and exit (INFO for success, WARN/ERROR for failure)
- Structured JSON in staging/prod via Logstash encoder — human-readable in dev
- Log the WHAT and WHY, not the HOW (business events, not implementation details)
- Exception logging: `log.error("Failed to process payment: orderId={}", orderId, exception)` — exception as last param
- No `System.out.println()` or `e.printStackTrace()` — always use SLF4J Logger
- MDC cleanup in `finally` block — prevent context leaking between requests
- Log level configuration per environment — DEBUG in dev, INFO in prod

---

## SB14: Error Response Format

### RFC 7807 ProblemDetail — Standard Error Response

All error responses MUST use Spring's `ProblemDetail` (RFC 7807 compliant).

### 404 Not Found
```json
{
    "type": "https://api.lloyds.com/errors/not-found",
    "title": "Resource Not Found",
    "status": 404,
    "detail": "LoanApplication with id '550e8400-e29b-41d4-a716-446655440000' not found",
    "instance": "/api/v1/loan-applications/550e8400-e29b-41d4-a716-446655440000",
    "correlationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "timestamp": "2024-01-15T10:30:00.000Z"
}
```

### 400 Validation Error
```json
{
    "type": "https://api.lloyds.com/errors/validation-failed",
    "title": "Validation Failed",
    "status": 400,
    "detail": "One or more fields failed validation",
    "instance": "/api/v1/loan-applications",
    "correlationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "timestamp": "2024-01-15T10:30:00.000Z",
    "violations": [
        {
            "field": "requestedAmount",
            "message": "Minimum loan amount is £1,000",
            "rejectedValue": "500.00"
        },
        {
            "field": "fullName",
            "message": "Full name is required",
            "rejectedValue": "null"
        }
    ]
}
```

### 422 Business Rule Violation
```json
{
    "type": "https://api.lloyds.com/errors/business-rule-violation",
    "title": "Business Rule Violation",
    "status": 422,
    "detail": "Customer has exceeded maximum number of active loan applications",
    "instance": "/api/v1/loan-applications",
    "correlationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "timestamp": "2024-01-15T10:30:00.000Z",
    "ruleCode": "LOAN_LIMIT_EXCEEDED"
}
```

### 403 Access Denied
```json
{
    "type": "https://api.lloyds.com/errors/access-denied",
    "title": "Access Denied",
    "status": 403,
    "detail": "Insufficient permissions",
    "instance": "/api/v1/loan-applications/550e8400-e29b-41d4-a716-446655440000",
    "correlationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "timestamp": "2024-01-15T10:30:00.000Z"
}
```

### 500 Internal Server Error
```json
{
    "type": "https://api.lloyds.com/errors/internal-error",
    "title": "Internal Server Error",
    "status": 500,
    "detail": "An unexpected error occurred",
    "instance": "/api/v1/loan-applications",
    "correlationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "timestamp": "2024-01-15T10:30:00.000Z"
}
```

### ProblemDetail Configuration
```java
// In application.yml — enable RFC 7807 globally
spring:
  mvc:
    problemdetail:
      enabled: true
```

**RULES:**
- ALL error responses use RFC 7807 `ProblemDetail` format — no custom error DTOs
- Standard fields: `type` (URI), `title`, `status`, `detail`, `instance`
- Extension fields: `correlationId` (always), `timestamp` (always), `violations` (for 400), `ruleCode` (for 422)
- `type` URI follows pattern: `https://api.lloyds.com/errors/{error-type}`
- `instance` is the request path that caused the error
- Content-Type: `application/problem+json`
- NEVER include stack traces in any environment
- NEVER include internal class names or package paths
- `detail` is human-readable — suitable for display to end users
- Consistent format across ALL microservices in the platform
- `correlationId` enables end-to-end request tracing across services

---

## SB15: Security Standards

### Security Configuration
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
            .csrf(csrf -> csrf.disable()) // Stateless API — CSRF not applicable
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

### Method-Level Authorization
```java
package com.lloyds.{servicename}.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/v1/loan-applications")
public class LoanApplicationController {

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanApplicationResponse>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        // Extract customer ID from JWT — enforce data ownership
        var customerId = UUID.fromString(jwt.getSubject());
        var result = getHandler.handle(new GetLoanApplicationQuery(id, customerId));
        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // Admin-only endpoint — no customer filter
        var result = listHandler.handle(new ListLoanApplicationsQuery(page, size));
        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteHandler.handle(new DeleteLoanApplicationCommand(id));
        return ResponseEntity.noContent().build();
    }
}
```

### Data Ownership Enforcement (in Handler)
```java
@Service
public class GetLoanApplicationQueryHandler implements QueryHandler<GetLoanApplicationQuery, LoanApplicationResponse> {

    @Override
    @Transactional(readOnly = true)
    public LoanApplicationResponse handle(GetLoanApplicationQuery query) {
        var application = repository.findById(query.applicationId())
            .orElseThrow(() -> new EntityNotFoundException("LoanApplication", query.applicationId()));

        // Enforce data ownership — customer can only access their own data
        if (query.customerId() != null && !application.getCustomerId().equals(query.customerId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                "Customer does not own this resource");
        }

        return mapper.toResponse(application);
    }
}
```

### JWT Configuration (application.yml)
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI}
          audiences: ${JWT_AUDIENCE}
          jwk-set-uri: ${JWK_SET_URI}
```

**RULES:**
- ALL endpoints authenticated by default — explicitly permit only health checks and API docs
- Stateless sessions (`SessionCreationPolicy.STATELESS`) — no server-side session state
- JWT validation: verify issuer, audience, expiry, and signature
- Method-level auth with `@PreAuthorize` — role-based access control
- Customer can ONLY access their own data — enforce in handler via JWT subject claim
- Admin endpoints require `ROLE_ADMIN` — never expose admin operations to customers
- No secrets in code — JWT issuer URI, audience, JWK set URI from environment variables
- CSRF disabled for stateless APIs — not applicable when using Bearer tokens
- `@AuthenticationPrincipal Jwt jwt` to extract claims in controllers
- Roles extracted from JWT `roles` claim with `ROLE_` prefix
- Security test: use `@WithMockUser(roles = "CUSTOMER")` in tests
- Swagger UI permitted without auth in non-prod — consider restricting in prod

---

## SB16: Resilience Standards

### Resilience4j Configuration (application.yml)
```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: 50
        slow-call-rate-threshold: 80
        slow-call-duration-threshold: 3s
        wait-duration-in-open-state: 30s
        sliding-window-size: 10
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 3
        record-exceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
          - org.springframework.web.client.HttpServerErrorException
    instances:
      creditCheckService:
        base-config: default
        wait-duration-in-open-state: 60s
      paymentService:
        base-config: default
        failure-rate-threshold: 30

  retry:
    configs:
      default:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
        retry-exceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
        ignore-exceptions:
          - com.lloyds.{servicename}.domain.exception.BusinessRuleException
    instances:
      creditCheckService:
        base-config: default
        max-attempts: 3
      paymentService:
        base-config: default
        max-attempts: 2

  timelimiter:
    configs:
      default:
        timeout-duration: 5s
        cancel-running-future: true
    instances:
      creditCheckService:
        timeout-duration: 10s
      paymentService:
        timeout-duration: 5s

  ratelimiter:
    configs:
      default:
        limit-for-period: 100
        limit-refresh-period: 1s
        timeout-duration: 0s
    instances:
      creditCheckService:
        limit-for-period: 50
```

### Resilient External Service Client
```java
package com.lloyds.{servicename}.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class CreditCheckClient {

    private static final Logger log = LoggerFactory.getLogger(CreditCheckClient.class);

    private final RestClient restClient;

    public CreditCheckClient(RestClient.Builder restClientBuilder, ExternalServiceProperties properties) {
        this.restClient = restClientBuilder
            .baseUrl(properties.creditCheck().baseUrl())
            .build();
    }

    @CircuitBreaker(name = "creditCheckService", fallbackMethod = "creditCheckFallback")
    @Retry(name = "creditCheckService")
    @TimeLimiter(name = "creditCheckService")
    public CreditCheckResponse checkCredit(UUID customerId, java.math.BigDecimal requestedAmount) {
        log.info("Calling credit check service for customer: {}", customerId);

        return restClient.post()
            .uri("/api/v1/credit-checks")
            .body(new CreditCheckRequest(customerId, requestedAmount))
            .retrieve()
            .body(CreditCheckResponse.class);
    }

    private CreditCheckResponse creditCheckFallback(UUID customerId, java.math.BigDecimal requestedAmount, Throwable t) {
        log.warn("Credit check service unavailable, using fallback. Reason: {}", t.getMessage());
        // Return a conservative default — do NOT approve without credit check
        return new CreditCheckResponse(customerId, 0, "UNAVAILABLE", "Service temporarily unavailable");
    }
}
```

### Resilience Configuration per External Service

| External Service | Timeout | Retries | Circuit Breaker | Rate Limit |
|-----------------|---------|---------|-----------------|------------|
| Credit Check (CRA) | 10s | 3 (1s, 2s, 4s) | 50% failures / 10 calls → 60s open | 50 req/s |
| Payment Service | 5s | 2 (1s, 2s) | 30% failures / 10 calls → 30s open | 100 req/s |
| Notification Service | 3s | 2 (1s, 2s) | 50% failures / 10 calls → 30s open | 200 req/s |
| Document Service | 15s | 1 | 50% failures / 5 calls → 60s open | 20 req/s |

**RULES:**
- EVERY external HTTP call MUST have circuit breaker + retry + timeout
- Annotation order: `@CircuitBreaker` → `@Retry` → `@TimeLimiter` (outermost to innermost)
- Fallback methods MUST be defined for every `@CircuitBreaker`
- Retry only on transient errors (IOException, TimeoutException, 5xx) — NEVER on 400, 404, 422
- Timeout MUST be less than the caller's timeout (prevent cascading)
- Metrics exposed to Prometheus via Micrometer (`/actuator/prometheus`)
- Circuit breaker state changes logged at WARN level
- Fallback for reads: return cached/default value. Fallback for writes: queue for retry or fail fast
- Configuration per service instance — not one-size-fits-all
- Use `RestClient` (Spring 6.1) — not `RestTemplate` (deprecated pattern)

---

## SB17: Event Publishing Standards

### Domain Event Schema
```java
package com.lloyds.{servicename}.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base domain event. All events published to Pub/Sub MUST extend this.
 */
public record DomainEvent<T>(
    UUID eventId,
    String eventType,
    Instant timestamp,
    String correlationId,
    String source,
    T data
) {
    public static <T> DomainEvent<T> create(String eventType, String correlationId, String source, T data) {
        return new DomainEvent<>(UUID.randomUUID(), eventType, Instant.now(), correlationId, source, data);
    }
}
```

### Event Data Records
```java
package com.lloyds.{servicename}.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanApplicationCreatedEvent(
    UUID applicationId,
    UUID customerId,
    UUID productId,
    BigDecimal requestedAmount,
    String status
) {}

public record LoanApplicationApprovedEvent(
    UUID applicationId,
    UUID customerId,
    BigDecimal approvedAmount,
    Integer tenureMonths,
    BigDecimal interestRate
) {}
```

### Pub/Sub Publisher
```java
package com.lloyds.{servicename}.infrastructure.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.pubsub.v1.PubsubMessage;
import com.lloyds.{servicename}.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisher(PubSubTemplate pubSubTemplate, ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void publish(String topic, DomainEvent<T> event, String orderingKey) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            Map<String, String> headers = Map.of(
                "eventType", event.eventType(),
                "correlationId", event.correlationId(),
                "source", event.source(),
                "eventId", event.eventId().toString()
            );

            pubSubTemplate.publish(topic, payload, headers);

            log.info("Published event: type={}, eventId={}, orderingKey={}",
                event.eventType(), event.eventId(), orderingKey);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: type={}", event.eventType(), e);
            throw new RuntimeException("Event serialization failed", e);
        }
    }
}
```

### Publishing from Command Handler
```java
@Service
public class ApproveLoanApplicationCommandHandler
        implements CommandHandler<ApproveLoanApplicationCommand, LoanApplicationResponse> {

    private final LoanApplicationRepository repository;
    private final EventPublisher eventPublisher;
    private final LoanApplicationMapper mapper;

    // Constructor injection...

    @Override
    @Transactional
    public LoanApplicationResponse handle(ApproveLoanApplicationCommand command) {
        var application = repository.findById(command.applicationId())
            .orElseThrow(() -> new EntityNotFoundException("LoanApplication", command.applicationId()));

        application.approve(command.approvedAmount(), command.tenureMonths(), command.interestRate());
        var saved = repository.save(application);

        // Publish event AFTER successful transaction
        var eventData = new LoanApplicationApprovedEvent(
            saved.getId(), saved.getCustomerId(),
            command.approvedAmount(), command.tenureMonths(), command.interestRate());

        var event = DomainEvent.create(
            "loan.application.approved",
            MDC.get("correlationId"),
            "loan-service",
            eventData);

        eventPublisher.publish("loan-events", event, saved.getId().toString());

        return mapper.toResponse(saved);
    }
}
```

### Pub/Sub Subscriber
```java
package com.lloyds.{servicename}.infrastructure.messaging.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.lloyds.{servicename}.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventSubscriber {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventSubscriber.class);

    private final ObjectMapper objectMapper;

    public PaymentEventSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ServiceActivator(inputChannel = "paymentEventsInputChannel")
    public void handleMessage(
            String payload,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
        String correlationId = message.getPubsubMessage()
            .getAttributesMap().getOrDefault("correlationId", "unknown");
        MDC.put("correlationId", correlationId);

        try {
            log.info("Received payment event");
            // Process idempotently — check if already processed by eventId
            // ... business logic ...
            message.ack();
            log.info("Payment event processed successfully");
        } catch (Exception e) {
            log.error("Failed to process payment event", e);
            message.nack(); // Will be retried
        } finally {
            MDC.remove("correlationId");
        }
    }
}
```

**RULES:**
- Events are past-tense facts: `loan.application.created`, `payment.processed`, `offer.expired`
- Event naming: `{domain}.{entity}.{past-tense-verb}` (dot-separated, lowercase)
- Every event includes: `eventId` (UUID), `eventType`, `timestamp`, `correlationId`, `source`, `data`
- Ordering key = aggregate ID (ensures events for same entity are processed in order)
- Subscribers MUST be idempotent — check `eventId` before processing (deduplication)
- Dead letter topic after 5 failed delivery attempts
- Publish AFTER successful database transaction — not inside `@Transactional` boundary
- Correlation ID propagated from HTTP request → event → subscriber
- Event payload is JSON — use Jackson ObjectMapper for serialization
- No sensitive data (PII) in events — use entity IDs, not customer details
- Subscriber acknowledges (`ack()`) only after successful processing
- Subscriber `nack()` on failure — message will be redelivered

---

## SB18: Caching Standards

### Cache Configuration
```java
package com.lloyds.{servicename}.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        var cacheConfigurations = Map.of(
            "products", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)),
            "loan-applications", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)),
            "customer-profiles", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15))
        );

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .transactionAware()
            .build();
    }
}
```

### Cacheable Query Handler
```java
package com.lloyds.{servicename}.application.query;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetProductQueryHandler implements QueryHandler<GetProductQuery, ProductResponse> {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public GetProductQueryHandler(ProductRepository productRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#query.productId()", unless = "#result == null")
    public ProductResponse handle(GetProductQuery query) {
        return productRepository.findById(query.productId())
            .map(mapper::toResponse)
            .orElseThrow(() -> new EntityNotFoundException("Product", query.productId()));
    }
}
```

### Cache Eviction on Write
```java
package com.lloyds.{servicename}.application.command;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProductCommandHandler implements CommandHandler<UpdateProductCommand, ProductResponse> {

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#command.productId()")
    public ProductResponse handle(UpdateProductCommand command) {
        // ... update logic ...
        return mapper.toResponse(saved);
    }
}
```

### Cache-Aside Pattern (Manual Control)
```java
package com.lloyds.{servicename}.infrastructure.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        return Optional.of(type.cast(value));
    }

    public <T> void put(String key, T value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }

    public void evictByPattern(String pattern) {
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
```

### Redis Configuration (application.yml)
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
```

**RULES:**
- Cache READS only — never cache write operations
- TTL on ALL cache keys — no indefinite caching
- Cache key MUST include version or entity ID — never generic keys
- Invalidate cache on write (`@CacheEvict`) — stale data is unacceptable for financial data
- Use `@Cacheable` for Spring-managed caching (simple cases)
- Use cache-aside pattern (`RedisCacheService`) for complex invalidation logic
- `unless = "#result == null"` — never cache null/empty results
- Redis (Memorystore) for distributed caching — not in-memory (JVM cache)
- Cache serialization: JSON (human-readable, debuggable)
- Connection pool configured — prevent connection exhaustion under load
- Cache miss is acceptable — always have a fallback to database
- Monitor cache hit/miss ratio via Micrometer metrics

---

## SB19: Database Migration Standards

### Flyway Configuration (application.yml)
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
    out-of-order: false
    table: flyway_schema_history
```

### Migration File Naming
```
src/main/resources/db/migration/
├── V1__create_products_table.sql
├── V2__create_loan_applications_table.sql
├── V3__create_offers_table.sql
├── V4__add_email_to_loan_applications.sql
├── V5__create_audit_log_table.sql
└── V6__add_index_customer_id_loan_applications.sql
```

### Initial Migration Example (V1__create_products_table.sql)
```sql
-- V1__create_products_table.sql
-- Description: Create products reference table
-- Author: loan-service-team
-- Date: 2024-01-15

CREATE TABLE products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version         BIGINT NOT NULL DEFAULT 0,
    name            VARCHAR(200) NOT NULL,
    type            VARCHAR(50) NOT NULL,
    min_amount      NUMERIC(18,2) NOT NULL,
    max_amount      NUMERIC(18,2) NOT NULL,
    min_tenure      INTEGER NOT NULL,
    max_tenure      INTEGER NOT NULL,
    interest_rate   NUMERIC(5,4) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_at      TIMESTAMPTZ,
    updated_by      VARCHAR(100),

    CONSTRAINT chk_products_amount CHECK (min_amount > 0 AND max_amount >= min_amount),
    CONSTRAINT chk_products_tenure CHECK (min_tenure > 0 AND max_tenure >= min_tenure)
);

CREATE INDEX idx_products_type ON products(type) WHERE is_deleted = FALSE;
CREATE INDEX idx_products_active ON products(is_active) WHERE is_deleted = FALSE;
```

### Adding a Column (V4__add_email_to_loan_applications.sql)
```sql
-- V4__add_email_to_loan_applications.sql
-- Description: Add email column to loan_applications (nullable — backward compatible)
-- Author: loan-service-team
-- Date: 2024-02-01

ALTER TABLE loan_applications
    ADD COLUMN email VARCHAR(255);

-- Backfill if needed (optional)
-- UPDATE loan_applications SET email = 'unknown@placeholder.com' WHERE email IS NULL;

CREATE INDEX idx_loan_applications_email ON loan_applications(email) WHERE email IS NOT NULL AND is_deleted = FALSE;
```

### Adding an Index (V6__add_index_customer_id_loan_applications.sql)
```sql
-- V6__add_index_customer_id_loan_applications.sql
-- Description: Add index on customer_id for faster lookups
-- Author: loan-service-team
-- Date: 2024-02-15

CREATE INDEX CONCURRENTLY idx_loan_applications_customer_id
    ON loan_applications(customer_id)
    WHERE is_deleted = FALSE;
```

### Destructive Change — 2-Release Process

**Release 1: Add new column, dual-write**
```sql
-- V7__add_sort_code_formatted.sql
ALTER TABLE bank_accounts ADD COLUMN sort_code_formatted VARCHAR(8);
-- Application writes to BOTH old and new columns
```

**Release 2: Migrate data, drop old column**
```sql
-- V8__migrate_sort_code_data.sql
UPDATE bank_accounts SET sort_code_formatted = sort_code WHERE sort_code_formatted IS NULL;

-- V9__drop_old_sort_code.sql (next release)
ALTER TABLE bank_accounts DROP COLUMN sort_code;
```

**RULES:**
- Naming: `V{version}__{description}.sql` — double underscore, lowercase_snake_case description
- Migrations are ADDITIVE only within a single release — never destructive
- NEVER `ALTER COLUMN` type in a single migration — use 2-release process
- New columns MUST be `NULL`able or have a `DEFAULT` value — backward compatible
- Destructive changes (drop column, rename column, change type) = 2-release process:
  1. Release 1: Add new structure, dual-write
  2. Release 2: Migrate data, remove old structure
- Always include `WHERE is_deleted = FALSE` in partial indexes
- Use `CREATE INDEX CONCURRENTLY` for large tables — prevents table locks
- Every table has: `id UUID PK`, `version BIGINT`, `created_at`, `created_by`, `updated_at`, `updated_by`, `is_deleted`
- Use `TIMESTAMPTZ` (not `TIMESTAMP`) — always store with timezone
- Use `NUMERIC(18,2)` for monetary values — never `FLOAT` or `DOUBLE`
- Test rollback: every migration should be reversible (keep rollback SQL in comments)
- No data manipulation (INSERT/UPDATE) in schema migrations — use separate seed scripts
- `validate-on-migrate: true` — fail fast if schema doesn't match migrations

---

## SB20: API Response Envelope

### Standard Response Wrapper
```java
package com.lloyds.{servicename}.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    T data,
    Meta meta
) {
    public record Meta(
        String requestId,
        Instant timestamp,
        Pagination pagination
    ) {
        public static Meta now(String requestId) {
            return new Meta(requestId, Instant.now(), null);
        }

        public static Meta withPagination(String requestId, Pagination pagination) {
            return new Meta(requestId, Instant.now(), pagination);
        }
    }

    public record Pagination(
        int page,
        int size,
        long total,
        int totalPages
    ) {}

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, Meta.now(
            org.slf4j.MDC.get("correlationId")));
    }

    public static <T> ApiResponse<java.util.List<T>> paginated(
            java.util.List<T> data, int page, int size, long total, int totalPages) {
        return new ApiResponse<>(data, Meta.withPagination(
            org.slf4j.MDC.get("correlationId"),
            new Pagination(page, size, total, totalPages)));
    }
}
```

### Single Resource Response
```json
{
    "data": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "customerId": "660e8400-e29b-41d4-a716-446655440001",
        "requestedAmount": 50000.00,
        "requestedTenureMonths": 24,
        "status": "DRAFT",
        "fullName": "John Smith",
        "createdAt": "2024-01-15T10:30:00.000Z"
    },
    "meta": {
        "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "timestamp": "2024-01-15T10:30:00.123Z"
    }
}
```

### Paginated Response
```json
{
    "data": [
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "customerId": "660e8400-e29b-41d4-a716-446655440001",
            "requestedAmount": 50000.00,
            "status": "DRAFT"
        },
        {
            "id": "770e8400-e29b-41d4-a716-446655440002",
            "customerId": "880e8400-e29b-41d4-a716-446655440003",
            "requestedAmount": 75000.00,
            "status": "APPROVED"
        }
    ],
    "meta": {
        "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "timestamp": "2024-01-15T10:30:00.123Z",
        "pagination": {
            "page": 0,
            "size": 20,
            "total": 142,
            "totalPages": 8
        }
    }
}
```

### Controller Usage
```java
@GetMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<List<LoanApplicationResponse>>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

    var result = listHandler.handle(new ListLoanApplicationsQuery(page, size));

    return ResponseEntity.ok(ApiResponse.paginated(
        result.getContent().stream().map(mapper::toResponse).toList(),
        page, size, result.getTotalElements(), result.getTotalPages()));
}

@GetMapping("/{id}")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public ResponseEntity<ApiResponse<LoanApplicationResponse>> getById(@PathVariable UUID id) {
    var result = getHandler.handle(new GetLoanApplicationQuery(id));
    return ResponseEntity.ok(ApiResponse.of(result));
}

@PostMapping
@PreAuthorize("hasRole('CUSTOMER')")
public ResponseEntity<ApiResponse<LoanApplicationResponse>> create(
        @Valid @RequestBody CreateLoanApplicationCommand command) {
    var result = createHandler.handle(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(result));
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> delete(@PathVariable UUID id) {
    deleteHandler.handle(new DeleteLoanApplicationCommand(id));
    return ResponseEntity.noContent().build();
}
```

### Error Response (ProblemDetail — NOT wrapped in ApiResponse)
```json
{
    "type": "https://api.lloyds.com/errors/not-found",
    "title": "Resource Not Found",
    "status": 404,
    "detail": "LoanApplication with id '550e8400-e29b-41d4-a716-446655440000' not found",
    "instance": "/api/v1/loan-applications/550e8400-e29b-41d4-a716-446655440000",
    "correlationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "timestamp": "2024-01-15T10:30:00.000Z"
}
```

**RULES:**
- ALL successful responses wrapped in `ApiResponse<T>` envelope with `data` + `meta`
- Error responses use RFC 7807 `ProblemDetail` — NOT wrapped in `ApiResponse`
- `meta.requestId` = correlation ID from MDC (same as `X-Correlation-Id` header)
- `meta.timestamp` = response generation time (ISO 8601, UTC)
- `meta.pagination` included ONLY for paginated list endpoints
- Pagination is zero-indexed (`page: 0` = first page)
- `null` fields omitted from JSON (configured in `JacksonConfig`)
- All properties in camelCase (configured in `JacksonConfig`)
- All dates/times in ISO 8601 format with UTC timezone
- `DELETE` returns `204 No Content` with empty body — no envelope
- Consistent envelope across ALL microservices in the platform
- `ApiResponse.of()` factory method for single resources
- `ApiResponse.paginated()` factory method for lists with pagination metadata

---

## Cross-References to Enterprise Architecture

| KB Section | EA Reference | Alignment |
|-----------|-------------|-----------|
| SB1 Tech Stack | EA1 | Java 21, Spring Boot 3.3.x, GCP stack per EA1 |
| SB2 Project Structure | EA2 | Hexagonal architecture with CQRS per EA2 |
| SB4 Entity Pattern | EA4 | UUID PKs, audit columns, soft delete, @Version per EA4 |
| SB6 Controller Pattern | EA3 | RESTful API standards (plural nouns, versioned routes) per EA3 |
| SB7 Exception Handling | EA3 | RFC 7807 ProblemDetail per EA3 |
| SB9 Repository Pattern | EA4 | Data architecture, JPA, Flyway per EA4 |
| SB11 Configuration | EA5 | No secrets in code, Secret Manager per EA5 |
| SB12 Testing | EA14 | Test pyramid, 80% coverage per EA14 |
| SB13 Logging | EA5 | Structured JSON, no PII, correlation ID per EA5 |
| SB15 Security | EA6 | OAuth2 JWT, method-level auth, data ownership per EA6 |
| SB16 Resilience | EA7 | Circuit breaker, retry, timeout per EA7 |
| SB17 Events | EA8 | Pub/Sub, event schema, idempotent subscribers per EA8 |
| SB18 Caching | EA9 | Redis Memorystore, TTL, cache-aside per EA9 |
| SB19 Migrations | EA4 | Flyway, additive-only, 2-release destructive per EA4 |
| SB20 Response Envelope | EA3 | Consistent envelope, pagination, camelCase per EA3 |

---

## SB21: Platform-Specific Standards (Lloyds GCP)

These standards apply specifically to the Lloyds Banking Group GCP platform:

### Encryption (Cloud KMS)

```java
// All Restricted-classified fields MUST use application-level encryption via Cloud KMS
@Service
public class EncryptionService {
    private final KeyManagementServiceClient kmsClient;
    private final String keyName; // projects/{project}/locations/{location}/keyRings/{ring}/cryptoKeys/{key}

    public byte[] encrypt(byte[] plaintext) {
        EncryptResponse response = kmsClient.encrypt(keyName, ByteString.copyFrom(plaintext));
        return response.getCiphertext().toByteArray();
    }

    public byte[] decrypt(byte[] ciphertext) {
        DecryptResponse response = kmsClient.decrypt(keyName, ByteString.copyFrom(ciphertext));
        return response.getPlaintext().toByteArray();
    }
}
```

**RULES:**
- Restricted data (score values, IP addresses, device fingerprints): Cloud KMS AES-256-GCM
- Confidential data (factor descriptions, consent records): Cloud SQL encryption at rest (Google-managed)
- Never store encryption keys in application config — always Cloud KMS or Secret Manager
- Key rotation: automatic via Cloud KMS (90-day rotation period)

### API Gateway (Apigee)

All services sit behind Apigee API Gateway. Standards:
- Rate limiting enforced at Apigee (100 read/min, 20 write/min per customer)
- JWT validation at Apigee (reject before reaching service)
- API versioning at Apigee (`/api/v1/` routing)
- Request/response logging at Apigee (metadata only, no PII)
- Services MUST still validate JWT internally (defence in depth)

### Multi-Brand Support

```java
// Brand context from JWT claims — used for analytics only, not business logic
@Component
public class BrandContext {
    public String getBrand(JwtAuthenticationToken jwt) {
        return jwt.getToken().getClaimAsString("brand"); // "lloyds" | "halifax" | "bos"
    }
}
```

**RULES:**
- Backend services are brand-agnostic — no `if (brand == "halifax")` logic
- Brand passed in JWT claims for analytics/reporting only
- Theming handled in mobile app shell (`:core:ui` design system tokens)
- Feature flags control per-brand rollout (not code branches)
- All services deployed once, serving all brands from same GKE namespace

### Virtual Threads (Java 21)

```yaml
spring:
  threads:
    virtual:
      enabled: true  # All request handling on virtual threads
```

**RULES:**
- Enable virtual threads for all services (Spring Boot 3.2+)
- Do NOT use `synchronized` blocks with virtual threads (use `ReentrantLock` instead)
- Do NOT pin virtual threads to platform threads (avoid `ThreadLocal` abuse)
- Ideal for I/O-bound operations: CRA API calls, database queries, Redis reads
