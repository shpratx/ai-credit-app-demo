# Low-Level Design — AI Credit Coach (Sprint 1)
### EP-01: Platform Foundation & EP-02: Credit Score Dashboard
### Version 1.0 · May 2026

---

## 1. Domain Model

### 1.1 Entity Relationship Diagram

```
┌─────────────────────────┐
│        Consent          │
├─────────────────────────┤
│ id (PK)                 │
│ customerId              │
│ craProvider             │
│ status                  │
│ consentTextVersion      │
│ consentTextHash         │
│ grantedAt               │
│ withdrawnAt             │
│ channel                 │
│ ipAddress [ENCRYPTED]   │
│ deviceFingerprint [ENC] │
│ createdAt               │
└─────────────────────────┘

┌─────────────────────────┐       ┌─────────────────────────┐
│      CreditScore        │       │      ScoreFactor        │
├─────────────────────────┤       ├─────────────────────────┤
│ id (PK)                 │──1:N──│ id (PK)                 │
│ customerId              │       │ scoreId (FK)            │
│ provider                │       │ category                │
│ scoreValue [ENCRYPTED]  │       │ impact                  │
│ maxScore                │       │ direction               │
│ band                    │       │ title                   │
│ previousScore           │       │ description             │
│ change                  │       │ weightingPercent        │
│ changeDirection         │       │ createdAt               │
│ retrievedAt             │       └─────────────────────────┘
│ isStale                 │
│ dataQualityScore        │
│ createdAt               │
└─────────────────────────┘

┌─────────────────────────┐       ┌─────────────────────────┐
│  ScoreRefreshSchedule   │       │    CraApiAuditLog       │
├─────────────────────────┤       ├─────────────────────────┤
│ id (PK)                 │       │ id (PK)                 │
│ customerId              │       │ customerId              │
│ provider                │       │ provider                │
│ frequencyDays           │       │ requestHash             │
│ lastRefreshedAt         │       │ responseStatus          │
│ nextRefreshAt           │       │ latencyMs               │
│ status                  │       │ circuitBreakerState     │
│ retryCount              │       │ correlationId           │
│ createdAt               │       │ createdAt               │
│ updatedAt               │       └─────────────────────────┘
└─────────────────────────┘
```

### 1.2 Entity Definitions

| Entity | Bounded Context | Owner Service | Relationships |
|--------|----------------|---------------|---------------|
| Consent | Consent Management | ConsentService | Standalone (linked by customerId) |
| CreditScore | Credit Score | CreditScoreService | Parent of ScoreFactor (1:N) |
| ScoreFactor | Credit Score | CreditScoreService | Child of CreditScore (N:1 via scoreId FK) |
| ScoreRefreshSchedule | Credit Score | CreditScoreService | Linked to customer (1:1 per provider) |
| CraApiAuditLog | Credit Score | CreditScoreService | Linked to customer (1:N) |

### 1.3 Entity Field Specifications

**Consent**

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Generated via `UUID.randomUUID()` |
| customerId | UUID | NOT NULL | From JWT `sub` claim |
| craProvider | String (enum) | NOT NULL | EXPERIAN, EQUIFAX, TRANSUNION |
| status | String (enum) | NOT NULL | GRANTED, WITHDRAWN |
| consentTextVersion | String | NOT NULL | Semantic version e.g. "1.0" |
| consentTextHash | String(64) | NOT NULL | SHA-256 of displayed consent text |
| grantedAt | OffsetDateTime | Nullable | Set on grant |
| withdrawnAt | OffsetDateTime | Nullable | Set on withdrawal |
| channel | String (enum) | NOT NULL | IOS, ANDROID, WEB |
| ipAddress | String(45) | NOT NULL | [ENCRYPTED] AES-256 via Cloud KMS |
| deviceFingerprint | String(255) | NOT NULL | [ENCRYPTED] AES-256 via Cloud KMS |
| createdAt | OffsetDateTime | NOT NULL | Immutable row creation timestamp |

**CreditScore**

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Generated via `UUID.randomUUID()` |
| customerId | UUID | NOT NULL | Partition key |
| provider | String (enum) | NOT NULL | EXPERIAN, EQUIFAX, TRANSUNION |
| scoreValue | Integer | NOT NULL | [ENCRYPTED] AES-256 via Cloud KMS |
| maxScore | Integer | NOT NULL | Experian: 999, Equifax: 1000, TransUnion: 710 |
| band | String (enum) | NOT NULL | poor, fair, good, very_good, excellent |
| previousScore | Integer | Nullable | Previous retrieval score (decrypted at read) |
| change | Integer | Nullable | Delta from previous |
| changeDirection | String (enum) | Nullable | up, down, unchanged |
| retrievedAt | OffsetDateTime | NOT NULL | CRA response timestamp |
| isStale | Boolean | NOT NULL | true if served from cache during outage |
| dataQualityScore | Integer | NOT NULL | 0–100, must be ≥95 |
| createdAt | OffsetDateTime | NOT NULL | Row creation timestamp |

**ScoreFactor**

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Generated |
| scoreId | UUID | FK NOT NULL | References CreditScore.id |
| category | String (enum) | NOT NULL | payment_history, utilisation, credit_age, credit_mix, new_credit |
| impact | String (enum) | NOT NULL | high, medium, low |
| direction | String (enum) | NOT NULL | positive, negative |
| title | String | NOT NULL | Short factor title |
| description | Text | NOT NULL | Plain English (Flesch ≥60) |
| weightingPercent | Integer | Nullable | Factor weighting 0–100 |
| createdAt | OffsetDateTime | NOT NULL | Row creation timestamp |

**ScoreRefreshSchedule**

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Generated |
| customerId | UUID | NOT NULL | UNIQUE with provider |
| provider | String (enum) | NOT NULL | EXPERIAN |
| frequencyDays | Integer | NOT NULL | Min 7, max 90 |
| lastRefreshedAt | OffsetDateTime | Nullable | Last successful refresh |
| nextRefreshAt | OffsetDateTime | NOT NULL | Next scheduled refresh |
| status | String (enum) | NOT NULL | ACTIVE, PAUSED, FAILED |
| retryCount | Integer | NOT NULL | 0–3, reset on success |
| createdAt | OffsetDateTime | NOT NULL | Row creation |
| updatedAt | OffsetDateTime | NOT NULL | Last modification |

**CraApiAuditLog**

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Generated |
| customerId | UUID | NOT NULL | Who the call was for |
| provider | String (enum) | NOT NULL | EXPERIAN |
| requestHash | String(64) | NOT NULL | SHA-256 of request payload |
| responseStatus | String (enum) | NOT NULL | SUCCESS, TIMEOUT, ERROR, RATE_LIMITED |
| latencyMs | Integer | NOT NULL | Response time in milliseconds |
| circuitBreakerState | String (enum) | NOT NULL | CLOSED, OPEN, HALF_OPEN |
| correlationId | UUID | NOT NULL | Request correlation ID |
| createdAt | OffsetDateTime | NOT NULL | Row creation timestamp |

---

## 2. Database Schemas

### 2.1 Schema: `consent` (ConsentService — separate Cloud SQL instance)

```sql
-- =============================================================
-- Table: credit_coach_consents
-- Purpose: Immutable audit trail of CRA consent lifecycle
-- Owner: ConsentService
-- Retention: Indefinite (GDPR Art. 7(1) demonstrability)
-- Classification: Confidential
-- =============================================================

CREATE TABLE credit_coach_consents (
    id                    UUID                     NOT NULL DEFAULT gen_random_uuid(),
    customer_id           UUID                     NOT NULL,
    cra_provider          VARCHAR(20)              NOT NULL,
    status                VARCHAR(20)              NOT NULL,
    consent_text_version  VARCHAR(10)              NOT NULL,
    consent_text_hash     VARCHAR(64)              NOT NULL,
    granted_at            TIMESTAMP WITH TIME ZONE,
    withdrawn_at          TIMESTAMP WITH TIME ZONE,
    channel               VARCHAR(10)              NOT NULL,
    ip_address            VARCHAR(512)             NOT NULL,  -- AES-256 encrypted (base64 encoded)
    device_fingerprint    VARCHAR(512)             NOT NULL,  -- AES-256 encrypted (base64 encoded)
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_consents PRIMARY KEY (id),
    CONSTRAINT chk_cra_provider CHECK (cra_provider IN ('EXPERIAN', 'EQUIFAX', 'TRANSUNION')),
    CONSTRAINT chk_status CHECK (status IN ('GRANTED', 'WITHDRAWN')),
    CONSTRAINT chk_channel CHECK (channel IN ('IOS', 'ANDROID', 'WEB'))
);

-- Indexes
CREATE INDEX idx_consents_customer_provider
    ON credit_coach_consents (customer_id, cra_provider, status);

CREATE INDEX idx_consents_status_provider
    ON credit_coach_consents (status, cra_provider);

CREATE INDEX idx_consents_created_at
    ON credit_coach_consents (created_at DESC);

-- No UPDATE/DELETE allowed (append-only). Enforced via application + DB role:
-- REVOKE UPDATE, DELETE ON credit_coach_consents FROM consent_service_role;
```

### 2.2 Schema: `scores` (CreditScoreService — separate Cloud SQL instance, CRA data domain)

```sql
-- =============================================================
-- Table: credit_scores
-- Purpose: Encrypted score history
-- Owner: CreditScoreService
-- Retention: 6 years post-service-closure (FCA SYSC 9.1.1R)
-- Classification: Restricted
-- Partitioning: Hash on customer_id (16 partitions)
-- =============================================================

CREATE TABLE credit_scores (
    id                  UUID                     NOT NULL DEFAULT gen_random_uuid(),
    customer_id         UUID                     NOT NULL,
    provider            VARCHAR(20)              NOT NULL,
    score_value         BYTEA                    NOT NULL,  -- AES-256 encrypted integer
    max_score           INTEGER                  NOT NULL,
    band                VARCHAR(20)              NOT NULL,
    previous_score      BYTEA,                              -- AES-256 encrypted integer (nullable)
    change              INTEGER,
    change_direction    VARCHAR(10),
    retrieved_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    is_stale            BOOLEAN                  NOT NULL DEFAULT FALSE,
    data_quality_score  INTEGER                  NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_credit_scores PRIMARY KEY (id, customer_id),
    CONSTRAINT chk_provider CHECK (provider IN ('EXPERIAN', 'EQUIFAX', 'TRANSUNION')),
    CONSTRAINT chk_band CHECK (band IN ('poor', 'fair', 'good', 'very_good', 'excellent')),
    CONSTRAINT chk_change_dir CHECK (change_direction IN ('up', 'down', 'unchanged') OR change_direction IS NULL),
    CONSTRAINT chk_quality CHECK (data_quality_score BETWEEN 0 AND 100)
) PARTITION BY HASH (customer_id);

-- Create 16 hash partitions
CREATE TABLE credit_scores_p0 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 0);
CREATE TABLE credit_scores_p1 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 1);
CREATE TABLE credit_scores_p2 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 2);
CREATE TABLE credit_scores_p3 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 3);
CREATE TABLE credit_scores_p4 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 4);
CREATE TABLE credit_scores_p5 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 5);
CREATE TABLE credit_scores_p6 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 6);
CREATE TABLE credit_scores_p7 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 7);
CREATE TABLE credit_scores_p8 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 8);
CREATE TABLE credit_scores_p9 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 9);
CREATE TABLE credit_scores_p10 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 10);
CREATE TABLE credit_scores_p11 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 11);
CREATE TABLE credit_scores_p12 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 12);
CREATE TABLE credit_scores_p13 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 13);
CREATE TABLE credit_scores_p14 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 14);
CREATE TABLE credit_scores_p15 PARTITION OF credit_scores FOR VALUES WITH (MODULUS 16, REMAINDER 15);

-- Indexes (created on parent, propagated to partitions)
CREATE INDEX idx_scores_customer_retrieved
    ON credit_scores (customer_id, retrieved_at DESC);

CREATE INDEX idx_scores_customer_provider
    ON credit_scores (customer_id, provider);

CREATE INDEX idx_scores_retrieved_at
    ON credit_scores (retrieved_at DESC);

-- =============================================================
-- Table: credit_score_factors
-- Purpose: Factor breakdown per score retrieval
-- Owner: CreditScoreService
-- Retention: Same as parent (6 years)
-- Classification: Restricted
-- =============================================================

CREATE TABLE credit_score_factors (
    id                    UUID                     NOT NULL DEFAULT gen_random_uuid(),
    score_id              UUID                     NOT NULL,
    category              VARCHAR(50)              NOT NULL,
    impact                VARCHAR(10)              NOT NULL,
    direction             VARCHAR(10)              NOT NULL,
    title                 VARCHAR(255)             NOT NULL,
    description           TEXT                     NOT NULL,
    weighting_percent     INTEGER,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_score_factors PRIMARY KEY (id),
    CONSTRAINT chk_factor_category CHECK (category IN ('payment_history', 'utilisation', 'credit_age', 'credit_mix', 'new_credit')),
    CONSTRAINT chk_factor_impact CHECK (impact IN ('high', 'medium', 'low')),
    CONSTRAINT chk_factor_direction CHECK (direction IN ('positive', 'negative')),
    CONSTRAINT chk_weighting CHECK (weighting_percent IS NULL OR weighting_percent BETWEEN 0 AND 100)
);

CREATE INDEX idx_factors_score_id
    ON credit_score_factors (score_id);

CREATE INDEX idx_factors_score_direction
    ON credit_score_factors (score_id, direction, impact);

-- Note: FK to credit_scores cannot use standard FK due to partitioning.
-- Referential integrity enforced at application level.

-- =============================================================
-- Table: score_refresh_schedules
-- Purpose: Per-customer refresh scheduling
-- Owner: CreditScoreService
-- Retention: Lifetime of enrolment + 90 days
-- Classification: Internal
-- =============================================================

CREATE TABLE score_refresh_schedules (
    id                UUID                     NOT NULL DEFAULT gen_random_uuid(),
    customer_id       UUID                     NOT NULL,
    provider          VARCHAR(20)              NOT NULL,
    frequency_days    INTEGER                  NOT NULL,
    last_refreshed_at TIMESTAMP WITH TIME ZONE,
    next_refresh_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    status            VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE',
    retry_count       INTEGER                  NOT NULL DEFAULT 0,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_refresh_schedules PRIMARY KEY (id),
    CONSTRAINT uq_customer_provider UNIQUE (customer_id, provider),
    CONSTRAINT chk_frequency CHECK (frequency_days BETWEEN 7 AND 90),
    CONSTRAINT chk_schedule_status CHECK (status IN ('ACTIVE', 'PAUSED', 'FAILED')),
    CONSTRAINT chk_retry CHECK (retry_count BETWEEN 0 AND 3)
);

CREATE INDEX idx_refresh_next_active
    ON score_refresh_schedules (next_refresh_at, status)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_refresh_customer
    ON score_refresh_schedules (customer_id, provider);

-- =============================================================
-- Table: cra_api_audit_log
-- Purpose: Audit trail of all CRA API interactions
-- Owner: CreditScoreService
-- Retention: 6 years (FCA SYSC 9.1.1R)
-- Classification: Internal
-- =============================================================

CREATE TABLE cra_api_audit_log (
    id                    UUID                     NOT NULL DEFAULT gen_random_uuid(),
    customer_id           UUID                     NOT NULL,
    provider              VARCHAR(20)              NOT NULL,
    request_hash          VARCHAR(64)              NOT NULL,
    response_status       VARCHAR(20)              NOT NULL,
    latency_ms            INTEGER                  NOT NULL,
    circuit_breaker_state VARCHAR(20)              NOT NULL,
    correlation_id        UUID                     NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_audit_log PRIMARY KEY (id),
    CONSTRAINT chk_audit_provider CHECK (provider IN ('EXPERIAN', 'EQUIFAX', 'TRANSUNION')),
    CONSTRAINT chk_response_status CHECK (response_status IN ('SUCCESS', 'TIMEOUT', 'ERROR', 'RATE_LIMITED')),
    CONSTRAINT chk_cb_state CHECK (circuit_breaker_state IN ('CLOSED', 'OPEN', 'HALF_OPEN'))
);

CREATE INDEX idx_audit_customer_timestamp
    ON cra_api_audit_log (customer_id, created_at DESC);

CREATE INDEX idx_audit_status_created
    ON cra_api_audit_log (response_status, created_at DESC);

CREATE INDEX idx_audit_correlation
    ON cra_api_audit_log (correlation_id);
```

### 2.3 Encryption Notes

| Column | Table | Encryption Method | Key Reference |
|--------|-------|-------------------|---------------|
| `ip_address` | credit_coach_consents | AES-256-GCM | `projects/lloyds-credit-coach/locations/europe-west2/keyRings/consent-keys/cryptoKeys/pii-key` |
| `device_fingerprint` | credit_coach_consents | AES-256-GCM | Same as above |
| `score_value` | credit_scores | AES-256-GCM | `projects/lloyds-cra-data/locations/europe-west2/keyRings/score-keys/cryptoKeys/score-value-key` |
| `previous_score` | credit_scores | AES-256-GCM | Same as above |

**Implementation:** Application-level encryption via Spring Cloud GCP KMS integration. Encrypted values stored as `BYTEA` (binary) or base64-encoded `VARCHAR`. Decryption occurs in the service layer, never in SQL queries.

### 2.4 Retention Policies

| Table | Retention | Archive Strategy | Deletion Trigger |
|-------|-----------|-----------------|-----------------|
| credit_coach_consents | Indefinite | No archive (small records) | Never (regulatory) |
| credit_scores | 6 years post-closure | Coldline after 2 years active | Automated lifecycle policy |
| credit_score_factors | 6 years (same as parent) | Coldline after 2 years | Same as parent |
| score_refresh_schedules | Enrolment + 90 days | Delete after retention | Consent withdrawal + 90d |
| cra_api_audit_log | 6 years | Coldline after 2 years | Automated lifecycle policy |


---

## 3. Command/Query Handlers

### 3.1 ConsentService Handlers

#### 3.1.1 GrantConsentCommandHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `POST /api/v1/credit-coach/consents` |
| **Pattern** | Command |
| **Input** | `GrantConsentCommand { craProvider, consentTextVersion, consentTextHash, channel, privacyNoticeAccepted }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID`, `Idempotency-Key: UUID` |

**Validation Rules (Jakarta Bean Validation):**
```java
public record GrantConsentCommand(
    @NotNull @Pattern(regexp = "EXPERIAN|EQUIFAX|TRANSUNION") String craProvider,
    @NotBlank @Size(max = 10) String consentTextVersion,
    @NotBlank @Size(min = 64, max = 64) String consentTextHash,
    @NotNull @Pattern(regexp = "IOS|ANDROID|WEB") String channel,
    @AssertTrue(message = "Privacy notice must be accepted") Boolean privacyNoticeAccepted
) {}
```

**Business Logic (Pseudocode):**
```
FUNCTION handle(command, customerId, correlationId, idempotencyKey):
    // 1. Idempotency check
    existing = idempotencyStore.get(idempotencyKey)
    IF existing != null THEN RETURN existing

    // 2. Check no active consent exists for this CRA
    activeConsent = consentRepository.findActiveByCustomerAndProvider(customerId, command.craProvider)
    IF activeConsent != null THEN THROW ConflictException(409, "Active consent already exists")

    // 3. Validate consent text hash matches current version
    expectedHash = consentTextRegistry.getHash(command.consentTextVersion)
    IF expectedHash != command.consentTextHash THEN THROW ValidationException(422, "Consent text hash mismatch")

    // 4. Encrypt PII fields
    encryptedIp = kmsEncryptionService.encrypt(requestContext.ipAddress)
    encryptedFingerprint = kmsEncryptionService.encrypt(requestContext.deviceFingerprint)

    // 5. Create consent record
    consent = Consent.builder()
        .id(UUID.randomUUID())
        .customerId(customerId)
        .craProvider(command.craProvider)
        .status(GRANTED)
        .consentTextVersion(command.consentTextVersion)
        .consentTextHash(command.consentTextHash)
        .grantedAt(Instant.now())
        .channel(command.channel)
        .ipAddress(encryptedIp)
        .deviceFingerprint(encryptedFingerprint)
        .createdAt(Instant.now())
        .build()

    // 6. Persist (append-only)
    consentRepository.save(consent)

    // 7. Publish event
    pubSubPublisher.publish("credit-coach.consent.granted", ConsentEvent {
        eventId: UUID.randomUUID(),
        eventType: "consent.granted",
        timestamp: Instant.now(),
        customerId: customerId,
        craProvider: command.craProvider,
        consentId: consent.id,
        correlationId: correlationId
    }, orderingKey = customerId.toString())

    // 8. Store idempotency result
    idempotencyStore.put(idempotencyKey, consent, TTL = 24h)

    RETURN ConsentResponse { consent.id, customerId, craProvider, GRANTED, grantedAt, null }
```

**Output DTO:**
```java
public record ConsentResponseDto(
    UUID consentId,
    UUID customerId,
    String craProvider,
    String status,
    OffsetDateTime grantedAt,
    OffsetDateTime withdrawnAt
) {}
```

**Events Published:** `credit-coach.consent.granted`

---

#### 3.1.2 WithdrawConsentCommandHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `POST /api/v1/credit-coach/consents/{consentId}/withdraw` |
| **Pattern** | Command |
| **Input** | `WithdrawConsentCommand { consentId (path), reason (optional) }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID`, `Idempotency-Key: UUID` |

**Validation Rules:**
```java
public record WithdrawConsentCommand(
    @NotNull UUID consentId,
    @Pattern(regexp = "customer_request|data_concern|no_longer_needed|other") String reason
) {}
```

**Business Logic (Pseudocode):**
```
FUNCTION handle(command, customerId, correlationId, idempotencyKey):
    // 1. Idempotency check
    existing = idempotencyStore.get(idempotencyKey)
    IF existing != null THEN RETURN existing

    // 2. Find consent
    consent = consentRepository.findById(command.consentId)
    IF consent == null THEN THROW NotFoundException(404)
    IF consent.customerId != customerId THEN THROW ForbiddenException(403)
    IF consent.status == WITHDRAWN THEN THROW ConflictException(409, "Already withdrawn")

    // 3. Create withdrawal record (append-only — new row)
    withdrawalRecord = Consent.builder()
        .id(UUID.randomUUID())
        .customerId(customerId)
        .craProvider(consent.craProvider)
        .status(WITHDRAWN)
        .consentTextVersion(consent.consentTextVersion)
        .consentTextHash(consent.consentTextHash)
        .grantedAt(consent.grantedAt)
        .withdrawnAt(Instant.now())
        .channel(consent.channel)
        .ipAddress(consent.ipAddress)  // retain encrypted
        .deviceFingerprint(consent.deviceFingerprint)
        .createdAt(Instant.now())
        .build()

    consentRepository.save(withdrawalRecord)

    // 4. Publish event
    pubSubPublisher.publish("credit-coach.consent.revoked", ConsentEvent {
        eventId: UUID.randomUUID(),
        eventType: "consent.revoked",
        timestamp: Instant.now(),
        customerId: customerId,
        craProvider: consent.craProvider,
        consentId: withdrawalRecord.id,
        correlationId: correlationId
    }, orderingKey = customerId.toString())

    // 5. Store idempotency
    idempotencyStore.put(idempotencyKey, withdrawalRecord, TTL = 24h)

    RETURN ConsentResponse { withdrawalRecord.id, customerId, craProvider, WITHDRAWN, grantedAt, withdrawnAt }
```

**Events Published:** `credit-coach.consent.revoked`

---

#### 3.1.3 GetConsentsQueryHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `GET /api/v1/credit-coach/consents/{customerId}` |
| **Pattern** | Query |
| **Input** | `GetConsentsQuery { customerId (path) }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID` |

**Validation Rules:**
```java
public record GetConsentsQuery(
    @NotNull UUID customerId
) {}
// Additional: JWT sub claim must match customerId (enforced by Spring Security)
```

**Business Logic (Pseudocode):**
```
FUNCTION handle(query, authenticatedCustomerId):
    // 1. Authorization check
    IF query.customerId != authenticatedCustomerId THEN THROW ForbiddenException(403)

    // 2. Query latest consent per provider
    consents = consentRepository.findLatestByCustomer(query.customerId)
    // SQL: SELECT DISTINCT ON (cra_provider) * FROM credit_coach_consents
    //      WHERE customer_id = ? ORDER BY cra_provider, created_at DESC

    RETURN ConsentsListResponse { consents.map(toDto) }
```

**Output DTO:**
```java
public record ConsentsListResponseDto(
    List<ConsentResponseDto> data,
    ResponseMeta meta
) {}
```

**Events Published:** None (read-only)

---

### 3.2 CreditScoreService Handlers

#### 3.2.1 RetrieveScoreCommandHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `GET /api/v1/credit-coach/scores/{customerId}` (triggers retrieval on cache miss) |
| **Pattern** | Command (side-effecting: calls CRA, stores score) |
| **Input** | `RetrieveScoreCommand { customerId }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID` |

**Business Logic (Pseudocode):**
```
FUNCTION handle(customerId, correlationId):
    // 1. Check consent
    consent = consentServiceClient.getActiveConsent(customerId, "EXPERIAN")
    IF consent == null THEN THROW NotFoundException(404, "No active consent")

    // 2. Check Redis cache
    cacheKey = "credit-coach:score:" + customerId + ":EXPERIAN"
    cached = redisTemplate.get(cacheKey)
    IF cached != null AND !cached.isExpired() THEN
        RETURN ScoreResponse(cached, source = "cache")

    // 3. Call CRA via circuit breaker (see Section 5 for full flow)
    TRY:
        craResponse = craClient.retrieveScore(customerId)  // circuit breaker wrapped
    CATCH CircuitBreakerOpenException:
        // Fallback: serve stale cache
        staleCache = redisTemplate.get(cacheKey)  // may be expired but still in Redis
        IF staleCache != null THEN
            staleCache.isStale = true
            RETURN ScoreResponse(staleCache, source = "cache_stale", httpStatus = 503)
        ELSE
            THROW ServiceUnavailableException(503, "CRA unavailable, no cached data")

    // 4. Validate data quality
    IF craResponse.dataQualityScore < 95 THEN
        logAudit(customerId, "EXPERIAN", "ERROR", correlationId, "quality_below_threshold")
        THROW UnprocessableException(422, "Data quality below threshold")

    // 5. Encrypt score value
    encryptedScore = kmsEncryptionService.encrypt(craResponse.score.toString())

    // 6. Compute change from previous
    previousScore = scoreRepository.findLatest(customerId, "EXPERIAN")
    change = previousScore != null ? craResponse.score - previousScore.decryptedValue : null
    changeDirection = change > 0 ? "up" : change < 0 ? "down" : "unchanged"

    // 7. Store in Cloud SQL
    score = CreditScore.builder()
        .customerId(customerId)
        .provider("EXPERIAN")
        .scoreValue(encryptedScore)
        .maxScore(999)
        .band(classifyBand(craResponse.score))
        .previousScore(previousScore?.encryptedValue)
        .change(change)
        .changeDirection(changeDirection)
        .retrievedAt(Instant.now())
        .isStale(false)
        .dataQualityScore(craResponse.dataQualityScore)
        .build()
    scoreRepository.save(score)

    // 8. Store factors
    factors = craResponse.factors.map(f -> ScoreFactor.builder()
        .scoreId(score.id)
        .category(f.category)
        .impact(f.impact)
        .direction(f.direction)
        .title(f.title)
        .description(translateToPlainEnglish(f))
        .weightingPercent(f.weighting)
        .build())
    factorRepository.saveAll(factors)

    // 9. Update Redis cache
    cachePayload = buildCachePayload(score, factors)
    redisTemplate.set(cacheKey, cachePayload, TTL = 24h)

    // 10. Publish events
    pubSubPublisher.publish("credit-coach.score.retrieved", ScoreEvent {...})
    IF change != null AND change != 0 THEN
        pubSubPublisher.publish("credit-coach.score.changed", ScoreChangedEvent {...})

    // 11. Log audit
    auditLogRepository.save(CraApiAuditLog {...})

    RETURN ScoreResponse(score, factors, source = "cra_live")
```

**Events Published:** `credit-coach.score.retrieved`, `credit-coach.score.changed` (conditional)

---

#### 3.2.2 RefreshScoreCommandHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `POST /api/v1/credit-coach/scores/{customerId}/refresh` |
| **Pattern** | Command |
| **Input** | `RefreshScoreCommand { customerId }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID`, `Idempotency-Key: UUID` |

**Validation Rules:**
- Customer must have active consent
- Max 1 refresh per 24 hours per customer (rate limit)

**Business Logic (Pseudocode):**
```
FUNCTION handle(customerId, correlationId, idempotencyKey):
    // 1. Idempotency check
    existing = idempotencyStore.get(idempotencyKey)
    IF existing != null THEN RETURN existing

    // 2. Check consent
    consent = consentServiceClient.getActiveConsent(customerId, "EXPERIAN")
    IF consent == null THEN THROW NotFoundException(404)

    // 3. Rate limit: max 1 refresh per 24hr
    lastRefresh = scoreRepository.findLatestRetrievedAt(customerId, "EXPERIAN")
    IF lastRefresh != null AND lastRefresh.isAfter(Instant.now().minus(24, HOURS)) THEN
        THROW RateLimitException(429, retryAfter = secondsUntil24hrExpiry)

    // 4. Trigger async retrieval (delegates to RetrieveScoreCommandHandler)
    asyncExecutor.submit(() -> retrieveScoreHandler.handle(customerId, correlationId))

    // 5. Return 202 Accepted
    RETURN ScoreRetrievingResponse { status: "retrieving", estimatedSeconds: 3 }
```

**Events Published:** Delegated to `RetrieveScoreCommandHandler`


---

#### 3.2.3 GetScoreQueryHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `GET /api/v1/credit-coach/scores/{customerId}` (cache hit path) |
| **Pattern** | Query |
| **Input** | `GetScoreQuery { customerId }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID` |

**Business Logic (Pseudocode):**
```
FUNCTION handle(customerId, correlationId):
    // 1. Check Redis cache first (cache-aside)
    cacheKey = "credit-coach:score:" + customerId + ":EXPERIAN"
    cached = redisTemplate.get(cacheKey)
    IF cached != null THEN
        RETURN ScoreResponse(cached, source = "cache", cacheExpiresAt = cached.ttlExpiry)

    // 2. Cache miss — check DB
    latestScore = scoreRepository.findLatestByCustomer(customerId, "EXPERIAN")
    IF latestScore == null THEN
        // No score exists — check if consent exists
        consent = consentServiceClient.getActiveConsent(customerId, "EXPERIAN")
        IF consent == null THEN THROW NotFoundException(404, "No active consent")
        ELSE RETURN ScoreRetrievingResponse(202, "retrieving", estimatedSeconds = 3)

    // 3. Decrypt score value
    decryptedScore = kmsEncryptionService.decrypt(latestScore.scoreValue)

    // 4. Warm cache
    cachePayload = buildCachePayload(latestScore)
    redisTemplate.set(cacheKey, cachePayload, TTL = 24h)

    RETURN ScoreResponse(decryptedScore, latestScore, source = "cra_live")
```

**Output DTO:**
```java
public record ScoreResponseDto(
    UUID customerId,
    String provider,
    Integer score,
    Integer maxScore,
    String band,
    String bandLabel,
    Integer previousScore,
    Integer change,
    String changeDirection,
    OffsetDateTime retrievedAt,
    Boolean isStale,
    Integer dataQualityScore,
    ResponseMeta meta  // includes source, cacheExpiresAt
) {}
```

**Events Published:** None (read-only)

---

#### 3.2.4 GetFactorsQueryHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `GET /api/v1/credit-coach/scores/{customerId}/factors` |
| **Pattern** | Query |
| **Input** | `GetFactorsQuery { customerId }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID` |

**Business Logic (Pseudocode):**
```
FUNCTION handle(customerId, correlationId):
    // 1. Get latest score for customer
    latestScore = scoreRepository.findLatestByCustomer(customerId, "EXPERIAN")
    IF latestScore == null THEN THROW NotFoundException(404)

    // 2. Get factors for that score
    factors = factorRepository.findByScoreId(latestScore.id)

    // 3. Sort: negative high-impact first, then positive
    sortedFactors = factors.sortBy(direction DESC, impact DESC)

    // 4. Count
    positiveCount = factors.count(f -> f.direction == "positive")
    negativeCount = factors.count(f -> f.direction == "negative")

    RETURN FactorsResponse {
        customerId,
        factors: sortedFactors.map(toDto),
        positiveCount,
        negativeCount,
        retrievedAt: latestScore.retrievedAt
    }
```

**Output DTO:**
```java
public record FactorsResponseDto(
    UUID customerId,
    List<ScoreFactorDto> factors,
    Integer positiveCount,
    Integer negativeCount,
    OffsetDateTime retrievedAt,
    ResponseMeta meta
) {}

public record ScoreFactorDto(
    String factorId,
    String category,
    String impact,
    String direction,
    String title,
    String description,
    Integer weightingPercent
) {}
```

**Events Published:** None (read-only)

---

#### 3.2.5 GetChangeExplanationQueryHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `GET /api/v1/credit-coach/scores/{customerId}/change-explanation` |
| **Pattern** | Query |
| **Input** | `GetChangeExplanationQuery { customerId }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID` |

**Business Logic (Pseudocode):**
```
FUNCTION handle(customerId, correlationId):
    // 1. Get two most recent scores
    scores = scoreRepository.findTopNByCustomer(customerId, "EXPERIAN", limit = 2)
    IF scores.size() < 2 THEN RETURN 204 No Content (no change to explain)

    currentScore = scores[0]
    previousScore = scores[1]

    // 2. Decrypt scores
    currentValue = kmsEncryptionService.decrypt(currentScore.scoreValue)
    previousValue = kmsEncryptionService.decrypt(previousScore.scoreValue)

    totalChange = currentValue - previousValue
    IF totalChange == 0 THEN RETURN 204 No Content

    // 3. Get factors for both scores
    currentFactors = factorRepository.findByScoreId(currentScore.id)
    previousFactors = factorRepository.findByScoreId(previousScore.id)

    // 4. Compute contributing factors (diff analysis)
    contributors = computeFactorDiff(currentFactors, previousFactors)

    RETURN ChangeExplanationResponse {
        customerId,
        previousScore: previousValue,
        currentScore: currentValue,
        totalChange,
        changeDirection: totalChange > 0 ? "up" : "down",
        contributors,
        periodStart: previousScore.retrievedAt.toLocalDate(),
        periodEnd: currentScore.retrievedAt.toLocalDate()
    }
```

**Output DTO:**
```java
public record ChangeExplanationResponseDto(
    UUID customerId,
    Integer previousScore,
    Integer currentScore,
    Integer totalChange,
    String changeDirection,
    List<ContributorDto> contributors,
    LocalDate periodStart,
    LocalDate periodEnd,
    ResponseMeta meta
) {}

public record ContributorDto(
    String factor,
    Integer pointImpact,
    String description
) {}
```

**Events Published:** None (read-only)

---

#### 3.2.6 GetScoreHistoryQueryHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `GET /api/v1/credit-coach/scores/{customerId}/history?months=12` |
| **Pattern** | Query |
| **Input** | `GetScoreHistoryQuery { customerId, months (default 12, max 24) }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID` |

**Validation Rules:**
```java
public record GetScoreHistoryQuery(
    @NotNull UUID customerId,
    @Min(1) @Max(24) Integer months
) {}
```

**Business Logic (Pseudocode):**
```
FUNCTION handle(query, correlationId):
    fromDate = LocalDate.now().minusMonths(query.months)

    // Query scores within date range
    scores = scoreRepository.findByCustomerAndDateRange(
        query.customerId, "EXPERIAN", fromDate, LocalDate.now()
    )

    // Decrypt score values and map to data points
    dataPoints = scores.map(s -> {
        decrypted = kmsEncryptionService.decrypt(s.scoreValue)
        return DataPoint { date: s.retrievedAt.toLocalDate(), score: decrypted, band: s.band }
    })

    RETURN ScoreHistoryResponse {
        customerId: query.customerId,
        provider: "EXPERIAN",
        dataPoints,
        totalMonths: query.months
    }
```

**Output DTO:**
```java
public record ScoreHistoryResponseDto(
    UUID customerId,
    String provider,
    List<DataPointDto> dataPoints,
    Integer totalMonths,
    ResponseMeta meta
) {}

public record DataPointDto(
    LocalDate date,
    Integer score,
    String band
) {}
```

**Events Published:** None (read-only)

---

#### 3.2.7 GetRefreshConfigQueryHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `GET /api/v1/credit-coach/admin/refresh-schedule` |
| **Pattern** | Query |
| **Input** | None (returns all configurations) |
| **Headers** | `Authorization: Bearer <JWT>` (requires `credit-coach-admin` role) |

**Business Logic (Pseudocode):**
```
FUNCTION handle(correlationId):
    // 1. Authorization: Spring Security @PreAuthorize("hasRole('credit-coach-admin')")
    configs = refreshScheduleRepository.findDistinctConfigurations()

    RETURN RefreshConfigResponse {
        configurations: configs.map(c -> {
            craProvider: c.provider,
            frequencyDays: c.frequencyDays,
            lastUpdated: c.updatedAt,
            updatedBy: c.lastUpdatedBy
        })
    }
```

**Events Published:** None (read-only)

---

#### 3.2.8 UpdateRefreshConfigCommandHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `PUT /api/v1/credit-coach/admin/refresh-schedule` |
| **Pattern** | Command |
| **Input** | `UpdateRefreshConfigCommand { craProvider, frequencyDays }` |
| **Headers** | `Authorization: Bearer <JWT>` (requires `credit-coach-admin` role), `Idempotency-Key: UUID` |

**Validation Rules:**
```java
public record UpdateRefreshConfigCommand(
    @NotNull @Pattern(regexp = "EXPERIAN|EQUIFAX|TRANSUNION") String craProvider,
    @NotNull @Min(7) @Max(90) Integer frequencyDays
) {}
```

**Business Logic (Pseudocode):**
```
FUNCTION handle(command, adminUserId, correlationId):
    // 1. Update all active schedules for this provider
    updatedCount = refreshScheduleRepository.updateFrequencyByProvider(
        command.craProvider, command.frequencyDays, Instant.now()
    )

    // 2. Recalculate next_refresh_at for affected schedules
    refreshScheduleRepository.recalculateNextRefresh(command.craProvider, command.frequencyDays)

    // 3. Audit log
    auditLogger.log("REFRESH_CONFIG_UPDATED", adminUserId, command)

    RETURN RefreshConfigResponse { updated config }
```

**Events Published:** None (admin operation, audit logged)

---

### 3.3 ConversationalAgentService Handlers

#### 3.3.1 SendConversationalQueryHandler

| Aspect | Detail |
|--------|--------|
| **API Endpoint** | `POST /api/v1/credit-coach/conversations` |
| **Pattern** | Command (side-effecting: calls Envoy + Vertex AI) |
| **Input** | `SendConversationalQuery { message, sessionId, context }` |
| **Headers** | `Authorization: Bearer <JWT>`, `X-Correlation-Id: UUID`, `Idempotency-Key: UUID` |

**Validation Rules:**
```java
public record SendConversationalQueryCommand(
    @NotBlank @Size(max = 500) String message,
    @NotBlank String sessionId,
    ConversationContext context  // optional
) {}

public record ConversationContext(
    String currentScreen
) {}
```

**Business Logic (Pseudocode):**
```
FUNCTION handle(command, customerId, correlationId):
    // 1. Send to Envoy orchestrator for intent classification
    TRY:
        intentResult = envoyClient.classifyIntent(
            message = command.message,
            sessionId = command.sessionId,
            agentId = "credit-coach",
            customerId = customerId
        )
    CATCH CircuitBreakerOpenException:
        THROW ServiceUnavailableException(503, "Orchestrator unavailable")

    // 2. Handle based on confidence
    IF intentResult.confidence < 0.85 THEN
        // Ambiguous — ask clarification
        RETURN ConversationResponse {
            responseText: intentResult.clarificationPrompt,
            intent: intentResult.intent,
            confidence: intentResult.confidence,
            clarificationNeeded: true,
            clarificationPrompt: intentResult.clarificationPrompt
        }

    // 3. Handle out-of-scope
    IF intentResult.intent == "out_of_scope" THEN
        RETURN ConversationResponse {
            responseText: "Let me connect you with the right team for that.",
            intent: "out_of_scope",
            routedToAgent: intentResult.targetAgent
        }

    // 4. For credit queries — fetch score data for context
    scoreData = null
    IF intentResult.intent IN ["score_query", "factor_query", "improvement_query"] THEN
        scoreData = creditScoreServiceClient.getCurrentScore(customerId)

    // 5. Generate response via Vertex AI
    TRY:
        aiResponse = vertexAiClient.generateResponse(
            intent = intentResult.intent,
            message = command.message,
            scoreContext = scoreData,
            sessionId = command.sessionId
        )
    CATCH CircuitBreakerOpenException:
        RETURN ConversationResponse {
            responseText: "I'm having trouble right now. Try again in a moment.",
            intent: intentResult.intent,
            confidence: intentResult.confidence
        }

    // 6. Add disclaimer if response contains estimates
    disclaimer = null
    IF aiResponse.containsEstimates THEN
        disclaimer = "This is an estimate based on general patterns. Your actual score may vary."

    // 7. Build suggested actions
    suggestedActions = buildSuggestedActions(intentResult.intent, scoreData)

    RETURN ConversationResponse {
        responseText: aiResponse.text,
        intent: intentResult.intent,
        confidence: intentResult.confidence,
        suggestedActions,
        disclaimer,
        clarificationNeeded: false
    }
```

**Output DTO:**
```java
public record ConversationResponseDto(
    String responseText,
    String intent,
    Double confidence,
    List<SuggestedActionDto> suggestedActions,
    String routedToAgent,
    String disclaimer,
    Boolean clarificationNeeded,
    String clarificationPrompt,
    ResponseMeta meta
) {}

public record SuggestedActionDto(
    String label,
    String action
) {}
```

**Events Published:** None (stateless service; session managed by Envoy)


---

## 4. Consent State Machine

### 4.1 State Diagram

```
                    ┌──────────────────────────────────────────────┐
                    │                                              │
                    ▼                                              │
┌──────┐    GrantConsent     ┌─────────┐    WithdrawConsent    ┌──────────┐
│ NONE │ ──────────────────► │ GRANTED │ ────────────────────► │WITHDRAWN │
└──────┘                     └─────────┘                       └──────────┘
                                  ▲                                 │
                                  │         Re-consent             │
                                  │    (GrantConsent again)        │
                                  └─────────────────────────────────┘
```

### 4.2 State Transitions

| From | To | Trigger | Validations | Side Effects |
|------|----|---------|-------------|--------------|
| NONE | GRANTED | `GrantConsentCommand` | privacyNoticeAccepted == true; consentTextHash matches current version; no active consent for this CRA | Publish `consent.granted`; triggers first score retrieval |
| GRANTED | WITHDRAWN | `WithdrawConsentCommand` | Consent exists; status == GRANTED; customerId matches JWT | Publish `consent.revoked`; deactivate refresh schedule; invalidate cache |
| WITHDRAWN | GRANTED | `GrantConsentCommand` (re-consent) | Same as NONE→GRANTED; previous consent was WITHDRAWN | Publish `consent.granted`; create new refresh schedule; trigger score retrieval |

### 4.3 State Machine Implementation

```java
@Component
public class ConsentStateMachine {

    public void validateTransition(ConsentStatus current, ConsentStatus target) {
        var allowed = TRANSITIONS.get(current);
        if (allowed == null || !allowed.contains(target)) {
            throw new InvalidStateTransitionException(
                "Cannot transition from %s to %s".formatted(current, target));
        }
    }

    private static final Map<ConsentStatus, Set<ConsentStatus>> TRANSITIONS = Map.of(
        ConsentStatus.NONE,      Set.of(ConsentStatus.GRANTED),
        ConsentStatus.GRANTED,   Set.of(ConsentStatus.WITHDRAWN),
        ConsentStatus.WITHDRAWN, Set.of(ConsentStatus.GRANTED)
    );
}
```

### 4.4 Invariants

- A customer can have at most ONE active (GRANTED) consent per CRA provider at any time
- Consent records are **immutable** — no UPDATE/DELETE operations
- Withdrawal creates a NEW row with status=WITHDRAWN (append-only audit trail)
- Re-consent after withdrawal creates a NEW row with status=GRANTED
- Consent text version changes require re-consent (old consent remains valid until withdrawn)

---

## 5. Score Retrieval Flow

### 5.1 Detailed Pseudocode

```
FUNCTION retrieveScore(customerId: UUID, correlationId: UUID):

    // ═══════════════════════════════════════════════════════════
    // STEP 1: CONSENT VERIFICATION
    // ═══════════════════════════════════════════════════════════
    consent = consentServiceClient.getActiveConsent(customerId, "EXPERIAN")
    IF consent == null OR consent.status != GRANTED THEN
        THROW ConsentRequiredException(404, "No active CRA consent")
    END IF

    // ═══════════════════════════════════════════════════════════
    // STEP 2: CACHE CHECK (Redis — cache-aside pattern)
    // ═══════════════════════════════════════════════════════════
    cacheKey = "credit-coach:score:" + customerId + ":EXPERIAN"
    cachedScore = redisTemplate.opsForValue().get(cacheKey)

    IF cachedScore != null THEN
        remainingTtl = redisTemplate.getExpire(cacheKey)
        IF remainingTtl > 0 THEN
            LOG.info("Cache hit for customer={}", customerId)  // no PII in logs
            RETURN ScoreResponse.fromCache(cachedScore, cacheExpiresAt)
        END IF
    END IF

    // ═══════════════════════════════════════════════════════════
    // STEP 3: CRA API CALL (Circuit Breaker wrapped)
    // ═══════════════════════════════════════════════════════════
    startTime = System.nanoTime()
    circuitBreakerState = circuitBreakerRegistry.circuitBreaker("experian").getState()

    TRY:
        craResponse = experianCraClient.softSearch(customerId)
        // ↑ Annotated with @CircuitBreaker, @Retry, @TimeLimiter
        latencyMs = (System.nanoTime() - startTime) / 1_000_000

    CATCH (CallNotPermittedException e):
        // Circuit breaker is OPEN — serve stale cache
        LOG.warn("Circuit breaker OPEN for Experian, serving stale cache")
        RETURN serveStaleCacheOrFail(customerId, cacheKey, correlationId)

    CATCH (TimeoutException | WebClientResponseException e):
        // Timeout or CRA error after retries exhausted
        latencyMs = (System.nanoTime() - startTime) / 1_000_000
        logAuditEntry(customerId, "EXPERIAN", "TIMEOUT", latencyMs, circuitBreakerState, correlationId)
        RETURN serveStaleCacheOrFail(customerId, cacheKey, correlationId)

    // ═══════════════════════════════════════════════════════════
    // STEP 4: DATA QUALITY VALIDATION
    // ═══════════════════════════════════════════════════════════
    IF craResponse.dataQualityScore < 95 THEN
        LOG.warn("Data quality below threshold: {}", craResponse.dataQualityScore)
        logAuditEntry(customerId, "EXPERIAN", "ERROR", latencyMs, circuitBreakerState, correlationId)
        THROW DataQualityException(422, "CRA data quality below 95% threshold")
    END IF

    // ═══════════════════════════════════════════════════════════
    // STEP 5: ENCRYPT SENSITIVE FIELDS
    // ═══════════════════════════════════════════════════════════
    encryptedScoreValue = cloudKmsService.encrypt(
        keyName = "score-value-key",
        plaintext = craResponse.score.toString().getBytes()
    )

    // ═══════════════════════════════════════════════════════════
    // STEP 6: COMPUTE CHANGE FROM PREVIOUS SCORE
    // ═══════════════════════════════════════════════════════════
    previousScoreEntity = scoreRepository.findTopByCustomerIdAndProviderOrderByRetrievedAtDesc(
        customerId, "EXPERIAN"
    )
    previousDecrypted = previousScoreEntity != null
        ? cloudKmsService.decryptToInt(previousScoreEntity.getScoreValue())
        : null
    change = previousDecrypted != null ? craResponse.score - previousDecrypted : null
    changeDirection = computeDirection(change)

    // ═══════════════════════════════════════════════════════════
    // STEP 7: PERSIST TO CLOUD SQL (transactional)
    // ═══════════════════════════════════════════════════════════
    @Transactional:
        scoreEntity = CreditScore.builder()
            .id(UUID.randomUUID())
            .customerId(customerId)
            .provider("EXPERIAN")
            .scoreValue(encryptedScoreValue)
            .maxScore(999)
            .band(classifyBand(craResponse.score))
            .previousScore(previousScoreEntity?.scoreValue)
            .change(change)
            .changeDirection(changeDirection)
            .retrievedAt(Instant.now())
            .isStale(false)
            .dataQualityScore(craResponse.dataQualityScore)
            .createdAt(Instant.now())
            .build()
        scoreRepository.save(scoreEntity)

        // Persist factors
        factorEntities = craResponse.factors.stream().map(f ->
            ScoreFactor.builder()
                .id(UUID.randomUUID())
                .scoreId(scoreEntity.getId())
                .category(f.getCategory())
                .impact(f.getImpact())
                .direction(f.getDirection())
                .title(f.getTitle())
                .description(factorTranslator.toPlainEnglish(f))
                .weightingPercent(f.getWeighting())
                .createdAt(Instant.now())
                .build()
        ).toList()
        factorRepository.saveAll(factorEntities)

    // ═══════════════════════════════════════════════════════════
    // STEP 8: UPDATE REDIS CACHE
    // ═══════════════════════════════════════════════════════════
    cachePayload = ScoreCachePayload.builder()
        .score(craResponse.score)
        .maxScore(999)
        .band(scoreEntity.getBand())
        .previousScore(previousDecrypted)
        .change(change)
        .changeDirection(changeDirection)
        .retrievedAt(scoreEntity.getRetrievedAt())
        .isStale(false)
        .dataQualityScore(craResponse.dataQualityScore)
        .factors(factorEntities.stream().map(ScoreFactorDto::from).toList())
        .build()

    redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(cachePayload),
        Duration.ofHours(24))

    // ═══════════════════════════════════════════════════════════
    // STEP 9: PUBLISH EVENTS
    // ═══════════════════════════════════════════════════════════
    pubSubTemplate.publish("credit-coach.score.retrieved", ScoreEvent.builder()
        .eventId(UUID.randomUUID())
        .eventType("score.retrieved")
        .timestamp(Instant.now())
        .customerId(customerId)
        .provider("EXPERIAN")
        .score(craResponse.score)
        .band(scoreEntity.getBand())
        .dataQualityScore(craResponse.dataQualityScore)
        .correlationId(correlationId)
        .build()
    ).withOrderingKey(customerId.toString())

    IF change != null AND change != 0 THEN
        pubSubTemplate.publish("credit-coach.score.changed", ScoreChangedEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType("score.changed")
            .timestamp(Instant.now())
            .customerId(customerId)
            .provider("EXPERIAN")
            .previousScore(previousDecrypted)
            .currentScore(craResponse.score)
            .change(change)
            .changeDirection(changeDirection)
            .correlationId(correlationId)
            .build()
        ).withOrderingKey(customerId.toString())
    END IF

    // ═══════════════════════════════════════════════════════════
    // STEP 10: AUDIT LOG
    // ═══════════════════════════════════════════════════════════
    logAuditEntry(customerId, "EXPERIAN", "SUCCESS", latencyMs, circuitBreakerState, correlationId)

    // ═══════════════════════════════════════════════════════════
    // STEP 11: RETURN RESPONSE
    // ═══════════════════════════════════════════════════════════
    RETURN ScoreResponse.fromLive(scoreEntity, factorEntities, source = "cra_live")

END FUNCTION


// Helper: serve stale cache or throw 503
FUNCTION serveStaleCacheOrFail(customerId, cacheKey, correlationId):
    staleData = redisTemplate.opsForValue().get(cacheKey)  // Redis may still have expired-but-present data
    IF staleData != null THEN
        parsed = objectMapper.readValue(staleData, ScoreCachePayload.class)
        parsed.setIsStale(true)
        RETURN ScoreResponse.fromStaleCache(parsed, httpStatus = 503,
            headers = { "X-Data-Stale": true, "Retry-After": 60 })
    ELSE
        THROW ServiceUnavailableException(503, "CRA unavailable and no cached data")
    END IF
END FUNCTION
```

### 5.2 Sequence Diagram

```
Mobile App    Apigee    CreditScoreService    ConsentService    Redis    Experian CRA    Cloud SQL    Pub/Sub
    │            │              │                    │             │           │              │           │
    │──GET /scores/{id}──►│    │                    │             │           │              │           │
    │            │──────────►│  │                    │             │           │              │           │
    │            │           │──gRPC: checkConsent──►│             │           │              │           │
    │            │           │◄─────── GRANTED ──────│             │           │              │           │
    │            │           │──GET cache key────────────────────►│           │              │           │
    │            │           │◄─────── MISS ─────────────────────│           │              │           │
    │            │           │──POST /soft-search (CB)───────────────────────►│              │           │
    │            │           │◄─────── 200 + score ──────────────────────────│              │           │
    │            │           │──validate quality ≥95%│             │           │              │           │
    │            │           │──encrypt(score_value) │             │           │              │           │
    │            │           │──INSERT score + factors──────────────────────────────────────►│           │
    │            │           │──SET cache (24hr TTL)─────────────►│           │              │           │
    │            │           │──publish score.retrieved──────────────────────────────────────────────────►│
    │            │           │──publish score.changed (if delta)─────────────────────────────────────────►│
    │            │◄──────────│  │                    │             │           │              │           │
    │◄───── 200 + ScoreResponse ──│                  │             │           │              │           │
```

---

## 6. Circuit Breaker Implementation

### 6.1 Resilience4j Configuration

```yaml
# application.yml — CreditScoreService
resilience4j:
  circuitbreaker:
    instances:
      experian:
        registerHealthIndicator: true
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        minimumNumberOfCalls: 3
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        recordExceptions:
          - java.util.concurrent.TimeoutException
          - org.springframework.web.reactive.function.client.WebClientResponseException.ServiceUnavailable
          - org.springframework.web.reactive.function.client.WebClientResponseException.InternalServerError
          - java.net.ConnectException
        ignoreExceptions:
          - org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest
          - org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized
          - org.springframework.web.reactive.function.client.WebClientResponseException.NotFound

  retry:
    instances:
      experian:
        maxAttempts: 2
        waitDuration: 1s
        exponentialBackoffMultiplier: 2
        retryExceptions:
          - java.util.concurrent.TimeoutException
          - org.springframework.web.reactive.function.client.WebClientResponseException.ServiceUnavailable
        ignoreExceptions:
          - org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest
          - org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests

  timelimiter:
    instances:
      experian:
        timeoutDuration: 2500ms
        cancelRunningFuture: true
```

### 6.2 Circuit Breaker States

```
┌────────────────────────────────────────────────────────────────────────┐
│                                                                        │
│   ┌────────┐     3 failures in     ┌────────┐     60s elapsed      ┌──────────┐
│   │ CLOSED │     sliding window     │  OPEN  │     (auto)          │HALF_OPEN │
│   │(normal)│ ─────────────────────► │(reject)│ ──────────────────► │ (probe)  │
│   └────────┘                        └────────┘                      └──────────┘
│       ▲                                  ▲                               │
│       │                                  │                               │
│       │    3 successful probes           │    Any probe failure           │
│       └──────────────────────────────────┼───────────────────────────────┘
│                                          │
│                                     (back to OPEN,
│                                      wait another 60s)
└────────────────────────────────────────────────────────────────────────┘
```

### 6.3 State Behaviour

| State | Behaviour | Fallback |
|-------|-----------|----------|
| **CLOSED** | All requests pass through to Experian CRA | N/A (normal operation) |
| **OPEN** | All requests immediately rejected (no CRA call) | Serve from Redis cache with `isStale: true` |
| **HALF_OPEN** | 3 probe requests allowed through | If probe fails → back to OPEN; if succeeds → CLOSED |

### 6.4 Java Implementation

```java
@Service
@Slf4j
public class ExperianCraClient {

    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;

    @CircuitBreaker(name = "experian", fallbackMethod = "getScoreFromCache")
    @Retry(name = "experian")
    @TimeLimiter(name = "experian")
    public CompletableFuture<CraScoreResponse> retrieveScore(UUID customerId) {
        return webClient.post()
            .uri("/credit-score/v2/soft-search")
            .header("X-API-Key", apiKey)
            .bodyValue(buildRequest(customerId))
            .retrieve()
            .bodyToMono(CraScoreResponse.class)
            .toFuture();
    }

    // Fallback: serve stale cache
    private CompletableFuture<CraScoreResponse> getScoreFromCache(
            UUID customerId, Throwable throwable) {
        log.warn("CRA fallback triggered for customer, reason: {}",
            throwable.getClass().getSimpleName());  // No PII in logs

        String cacheKey = "credit-coach:score:" + customerId + ":EXPERIAN";
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            CraScoreResponse stale = objectMapper.readValue(cached, CraScoreResponse.class);
            stale.setStale(true);
            return CompletableFuture.completedFuture(stale);
        }
        return CompletableFuture.failedFuture(
            new ServiceUnavailableException("CRA unavailable, no cached data"));
    }
}
```

### 6.5 Monitoring

| Metric | Prometheus Name | Alert Threshold |
|--------|----------------|-----------------|
| Circuit state | `resilience4j_circuitbreaker_state{name="experian"}` | state == 2 (OPEN) → P2 alert |
| Failure rate | `resilience4j_circuitbreaker_failure_rate{name="experian"}` | > 50% → P3 alert |
| Call duration | `resilience4j_circuitbreaker_calls_seconds{name="experian"}` | p95 > 2.5s → P3 alert |
| Not permitted | `resilience4j_circuitbreaker_not_permitted_calls_total{name="experian"}` | > 0 → log warning |


---

## 7. Caching Strategy

### 7.1 Cache Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Memorystore (Redis 7)                      │
│                    2 nodes (primary + replica)                │
│                    TLS enabled, IAM auth                      │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Key Pattern:                                                │
│  credit-coach:score:{customerId}:{provider}                  │
│                                                              │
│  Example:                                                    │
│  credit-coach:score:550e8400-e29b-41d4-a716-446655440000:EXPERIAN │
│                                                              │
│  Value: JSON payload (ScoreCachePayload)                     │
│  TTL: 24 hours (86400 seconds)                               │
│  Eviction: allkeys-lru when memory > 80%                     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 Cache Key Design

| Key Pattern | Purpose | TTL | Service |
|-------------|---------|-----|---------|
| `credit-coach:score:{customerId}:{provider}` | Current score + factors | 24 hours | CreditScoreService |
| `credit-coach:idempotency:{idempotencyKey}` | Idempotency deduplication | 24 hours | All services |
| `credit-coach:consent:{customerId}:{provider}` | Active consent status | 1 hour | ConsentService |

### 7.3 Cache Payload Schema

```json
{
  "score": 742,
  "maxScore": 999,
  "band": "good",
  "bandLabel": "Good (700–849)",
  "previousScore": 727,
  "change": 15,
  "changeDirection": "up",
  "retrievedAt": "2026-05-05T14:30:00Z",
  "isStale": false,
  "dataQualityScore": 98,
  "factors": [
    {
      "factorId": "f-001",
      "category": "payment_history",
      "impact": "high",
      "direction": "positive",
      "title": "Payment History",
      "description": "You've made all payments on time for the last 12 months.",
      "weightingPercent": 35
    }
  ],
  "cachedAt": "2026-05-05T14:30:00Z",
  "expiresAt": "2026-05-06T14:30:00Z"
}
```

### 7.4 Cache-Aside Pattern Implementation

```java
@Service
@Slf4j
public class ScoreCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Duration CACHE_TTL = Duration.ofHours(24);
    private static final String KEY_PREFIX = "credit-coach:score:";

    public Optional<ScoreCachePayload> get(UUID customerId, String provider) {
        String key = buildKey(customerId, provider);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return Optional.empty();
        return Optional.of(objectMapper.readValue(json, ScoreCachePayload.class));
    }

    public void put(UUID customerId, String provider, ScoreCachePayload payload) {
        String key = buildKey(customerId, provider);
        payload.setCachedAt(Instant.now());
        payload.setExpiresAt(Instant.now().plus(CACHE_TTL));
        String json = objectMapper.writeValueAsString(payload);
        redisTemplate.opsForValue().set(key, json, CACHE_TTL);
    }

    public void invalidate(UUID customerId, String provider) {
        String key = buildKey(customerId, provider);
        redisTemplate.delete(key);
    }

    private String buildKey(UUID customerId, String provider) {
        return KEY_PREFIX + customerId + ":" + provider;
    }
}
```

### 7.5 Cache Invalidation Rules

| Trigger | Action | Reason |
|---------|--------|--------|
| New score retrieved from CRA | Overwrite cache with fresh data | Score updated |
| Consent withdrawn | Delete cache entry | Customer revoked access |
| Manual refresh triggered | Delete cache → triggers CRA call | Force fresh data |
| TTL expiry (24hr) | Auto-evicted by Redis | Staleness threshold |

### 7.6 Stale Cache Fallback

When the circuit breaker is OPEN or CRA times out:

1. Attempt to read from Redis (even if TTL expired — Redis `PERSIST` not used; rely on `maxmemory-policy allkeys-lru`)
2. If data exists: return with `isStale: true` and `X-Data-Stale: true` header
3. If no data: return HTTP 503 with `Retry-After: 60` header
4. Mobile app displays "Last updated: {retrievedAt}" with stale indicator

---

## 8. Event Publishing

### 8.1 Pub/Sub Topic Configuration

| Topic | Publisher | Ordering Key | Dead Letter Topic | Retention |
|-------|-----------|-------------|-------------------|-----------|
| `credit-coach.consent.granted` | ConsentService | `customerId` | `credit-coach.consent.granted.dlq` | 7 days |
| `credit-coach.consent.revoked` | ConsentService | `customerId` | `credit-coach.consent.revoked.dlq` | 7 days |
| `credit-coach.score.retrieved` | CreditScoreService | `customerId` | `credit-coach.score.retrieved.dlq` | 7 days |
| `credit-coach.score.changed` | CreditScoreService | `customerId` | `credit-coach.score.changed.dlq` | 7 days |

### 8.2 Event Schemas

**ConsentEvent (consent.granted / consent.revoked):**
```java
public record ConsentEvent(
    @NotNull UUID eventId,
    @NotBlank String eventType,       // "consent.granted" | "consent.revoked"
    @NotNull Instant timestamp,
    @NotNull UUID customerId,
    @NotBlank String craProvider,     // "EXPERIAN"
    @NotNull UUID consentId,
    @NotNull UUID correlationId
) implements DomainEvent {}
```

```json
{
  "eventId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "eventType": "consent.granted",
  "timestamp": "2026-05-05T14:30:00.000Z",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "craProvider": "EXPERIAN",
  "consentId": "660e8400-e29b-41d4-a716-446655440001",
  "correlationId": "770e8400-e29b-41d4-a716-446655440002"
}
```

**ScoreEvent (score.retrieved):**
```java
public record ScoreEvent(
    @NotNull UUID eventId,
    @NotBlank String eventType,       // "score.retrieved"
    @NotNull Instant timestamp,
    @NotNull UUID customerId,
    @NotBlank String provider,
    @NotNull Integer score,
    @NotBlank String band,
    @NotNull Integer dataQualityScore,
    @NotNull UUID correlationId
) implements DomainEvent {}
```

```json
{
  "eventId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "eventType": "score.retrieved",
  "timestamp": "2026-05-05T14:30:05.000Z",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "provider": "EXPERIAN",
  "score": 742,
  "band": "good",
  "dataQualityScore": 98,
  "correlationId": "770e8400-e29b-41d4-a716-446655440002"
}
```

**ScoreChangedEvent (score.changed):**
```java
public record ScoreChangedEvent(
    @NotNull UUID eventId,
    @NotBlank String eventType,       // "score.changed"
    @NotNull Instant timestamp,
    @NotNull UUID customerId,
    @NotBlank String provider,
    @NotNull Integer previousScore,
    @NotNull Integer currentScore,
    @NotNull Integer change,
    @NotBlank String changeDirection,  // "up" | "down"
    @NotNull UUID correlationId
) implements DomainEvent {}
```

```json
{
  "eventId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
  "eventType": "score.changed",
  "timestamp": "2026-05-05T14:30:05.000Z",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "provider": "EXPERIAN",
  "previousScore": 727,
  "currentScore": 742,
  "change": 15,
  "changeDirection": "up",
  "correlationId": "770e8400-e29b-41d4-a716-446655440002"
}
```

### 8.3 Publisher Implementation

```java
@Service
@Slf4j
public class CreditCoachEventPublisher {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, DomainEvent event, String orderingKey) {
        String payload = objectMapper.writeValueAsString(event);

        PubsubMessage message = PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8(payload))
            .putAttributes("eventId", event.eventId().toString())
            .putAttributes("eventType", event.eventType())
            .putAttributes("correlationId", event.correlationId().toString())
            .setOrderingKey(orderingKey)
            .build();

        pubSubTemplate.publish(topic, message)
            .whenComplete((msgId, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event to {}: {}", topic, ex.getMessage());
                    // Dead letter handling via Pub/Sub retry policy
                } else {
                    log.info("Published event {} to topic {}", event.eventId(), topic);
                }
            });
    }
}
```

### 8.4 Subscriber Configuration (Sprint 1)

| Topic | Subscriber | Subscription Name | Ack Deadline | Max Delivery Attempts |
|-------|-----------|-------------------|-------------|----------------------|
| `consent.granted` | CreditScoreService | `consent-granted-score-retrieval` | 30s | 5 |
| `consent.revoked` | CreditScoreService | `consent-revoked-schedule-deactivation` | 30s | 5 |
| `score.retrieved` | BigQuery (Dataflow) | `score-retrieved-analytics` | 60s | 5 |
| `score.changed` | BigQuery (Dataflow) | `score-changed-analytics` | 60s | 5 |

### 8.5 Idempotent Subscriber Pattern

```java
@Service
public class ConsentGrantedEventSubscriber {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration DEDUP_TTL = Duration.ofHours(48);

    @PubSubSubscriber(subscription = "consent-granted-score-retrieval")
    public void handleConsentGranted(ConsentEvent event, AckReplyConsumer ackReply) {
        // 1. Idempotency check using eventId
        String dedupKey = "credit-coach:event-processed:" + event.eventId();
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(dedupKey, "1", DEDUP_TTL);

        if (Boolean.FALSE.equals(isNew)) {
            log.info("Duplicate event {}, skipping", event.eventId());
            ackReply.ack();
            return;
        }

        // 2. Process event
        try {
            retrieveScoreCommandHandler.handle(event.customerId(), event.correlationId());
            ackReply.ack();
        } catch (Exception e) {
            log.error("Failed to process consent.granted event: {}", e.getMessage());
            redisTemplate.delete(dedupKey);  // Allow retry
            ackReply.nack();
        }
    }
}
```

### 8.6 Dead Letter Queue Handling

- After 5 failed delivery attempts → message moves to DLQ topic
- Cloud Monitoring alert fires on DLQ message count > 0
- Ops team investigates via Cloud Console
- Messages retained 7 days in DLQ for replay

---

## 9. Security Implementation

### 9.1 JWT Validation (Spring Security)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/api/v1/credit-coach/*/health").permitAll()
                .requestMatchers("/api/v1/credit-coach/admin/**").hasRole("credit-coach-admin")
                .requestMatchers("/api/v1/credit-coach/**").authenticated()
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    .decoder(jwtDecoder())
                )
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .csrf(csrf -> csrf.disable())  // Stateless API — CSRF not applicable
            .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Validates: signature, expiry, issuer, audience
        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withJwkSetUri(jwkSetUri)
            .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(issuerUri),
            new JwtClaimValidator<>("aud", aud -> aud.contains("credit-coach-api"))
        ));
        return decoder;
    }

    // Extract customer_id from JWT 'sub' claim
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("sub");  // customer_id in sub claim
        converter.setJwtGrantedAuthoritiesConverter(new CustomRoleConverter());
        return converter;
    }
}
```

### 9.2 Customer ID Enforcement

```java
@Component
public class CustomerIdAuthorizationAspect {

    @Around("@annotation(CustomerAuthorized)")
    public Object enforceCustomerAccess(ProceedingJoinPoint joinPoint) {
        // Extract customerId from path variable
        UUID pathCustomerId = extractCustomerIdFromArgs(joinPoint);

        // Extract customerId from JWT sub claim
        UUID jwtCustomerId = UUID.fromString(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );

        if (!pathCustomerId.equals(jwtCustomerId)) {
            throw new AccessDeniedException("Customer ID mismatch");
        }

        return joinPoint.proceed();
    }
}
```

### 9.3 Field-Level Encryption (AES-256 via Cloud KMS)

```java
@Service
public class CloudKmsEncryptionService {

    private final KeyManagementServiceClient kmsClient;
    private final String keyName;  // projects/{project}/locations/{location}/keyRings/{ring}/cryptoKeys/{key}

    /**
     * Encrypts plaintext using Cloud KMS envelope encryption.
     * Uses AES-256-GCM with a data encryption key (DEK) wrapped by KMS.
     */
    public byte[] encrypt(String plaintext) {
        EncryptResponse response = kmsClient.encrypt(
            CryptoKeyName.parse(keyName),
            ByteString.copyFromUtf8(plaintext)
        );
        return response.getCiphertext().toByteArray();
    }

    public String decrypt(byte[] ciphertext) {
        DecryptResponse response = kmsClient.decrypt(
            CryptoKeyName.parse(keyName),
            ByteString.copyFrom(ciphertext)
        );
        return response.getPlaintext().toStringUtf8();
    }

    public int decryptToInt(byte[] ciphertext) {
        return Integer.parseInt(decrypt(ciphertext));
    }
}
```

**Key Hierarchy:**

| Key Ring | Key Name | Purpose | Rotation |
|----------|----------|---------|----------|
| `consent-keys` | `pii-key` | IP address, device fingerprint | 90 days |
| `score-keys` | `score-value-key` | Score values, previous scores | 90 days |

### 9.4 PII Redaction in Logs

```java
@Configuration
public class LoggingConfig {

    @Bean
    public MaskingPatternLayout maskingLayout() {
        // Patterns to redact from all log output
        return MaskingPatternLayout.builder()
            .addPattern("\"ip_address\"\\s*:\\s*\"[^\"]+\"", "\"ip_address\":\"[REDACTED]\"")
            .addPattern("\"device_fingerprint\"\\s*:\\s*\"[^\"]+\"", "\"device_fingerprint\":\"[REDACTED]\"")
            .addPattern("\"score_value\"\\s*:\\s*\\d+", "\"score_value\":[REDACTED]")
            .addPattern("\"score\"\\s*:\\s*\\d+", "\"score\":[REDACTED]")
            .addPattern("customer_id=([a-f0-9-]+)", "customer_id=[HASH:${hash($1)}]")
            .build();
    }
}
```

**Logging Rules:**
- NEVER log: score values, IP addresses, device fingerprints, raw CRA responses
- ALWAYS log: correlation ID, event IDs, operation names, latency, error codes
- Customer ID: logged as pseudonymised hash in production; full UUID in staging only

### 9.5 Request Validation & Sanitisation

```java
@RestController
@Validated
public class ConsentController {

    @PostMapping("/api/v1/credit-coach/consents")
    public ResponseEntity<ConsentResponseDto> grantConsent(
            @Valid @RequestBody GrantConsentCommand command,
            @RequestHeader("X-Correlation-Id") @NotNull UUID correlationId,
            @RequestHeader("Idempotency-Key") @NotNull UUID idempotencyKey,
            @AuthenticationPrincipal Jwt jwt) {

        // Input sanitisation handled by Jakarta Bean Validation annotations
        // SQL injection prevented by Spring Data JPA parameterised queries
        // XSS prevented by JSON-only responses (no HTML rendering)

        UUID customerId = UUID.fromString(jwt.getSubject());
        var result = grantConsentHandler.handle(command, customerId, correlationId, idempotencyKey);
        return ResponseEntity.status(201)
            .header("Location", "/api/v1/credit-coach/consents/" + result.consentId())
            .body(result);
    }
}
```


---

## 10. Testing Strategy

### 10.1 Unit Tests (≥80% Line Coverage)

**Framework:** JUnit 5 + Mockito + AssertJ

| Handler | Test Class | Key Scenarios |
|---------|-----------|---------------|
| GrantConsentCommandHandler | `GrantConsentCommandHandlerTest` | Happy path; duplicate consent (409); invalid hash (422); privacy notice not accepted; idempotency replay |
| WithdrawConsentCommandHandler | `WithdrawConsentCommandHandlerTest` | Happy path; already withdrawn (409); not found (404); wrong customer (403) |
| GetConsentsQueryHandler | `GetConsentsQueryHandlerTest` | Returns latest per provider; empty list; authorization check |
| RetrieveScoreCommandHandler | `RetrieveScoreCommandHandlerTest` | Happy path; no consent (404); cache hit; quality below threshold; encryption/decryption; change detection |
| RefreshScoreCommandHandler | `RefreshScoreCommandHandlerTest` | Happy path; rate limited (429); no consent; idempotency |
| GetScoreQueryHandler | `GetScoreQueryHandlerTest` | Cache hit; cache miss with DB fallback; no score exists (202); stale cache |
| GetFactorsQueryHandler | `GetFactorsQueryHandlerTest` | Factors sorted correctly; no score (404); positive/negative counts |
| GetChangeExplanationQueryHandler | `GetChangeExplanationQueryHandlerTest` | Change exists; no change (204); only one score (204); contributor computation |
| GetScoreHistoryQueryHandler | `GetScoreHistoryQueryHandlerTest` | Returns time-series; respects month filter; decrypts scores |
| SendConversationalQueryHandler | `SendConversationalQueryHandlerTest` | High confidence response; low confidence clarification; out-of-scope routing; Vertex AI fallback |
| GetRefreshConfigQueryHandler | `GetRefreshConfigQueryHandlerTest` | Returns configs; admin role required |
| UpdateRefreshConfigCommandHandler | `UpdateRefreshConfigCommandHandlerTest` | Valid update; invalid frequency (400); non-admin (403) |

**Example Unit Test:**
```java
@ExtendWith(MockitoExtension.class)
class GrantConsentCommandHandlerTest {

    @Mock private ConsentRepository consentRepository;
    @Mock private CloudKmsEncryptionService encryptionService;
    @Mock private CreditCoachEventPublisher eventPublisher;
    @Mock private IdempotencyStore idempotencyStore;
    @InjectMocks private GrantConsentCommandHandler handler;

    @Test
    void shouldGrantConsentSuccessfully() {
        // Given
        var command = new GrantConsentCommand("EXPERIAN", "1.0", "abc123...sha256", "IOS", true);
        var customerId = UUID.randomUUID();
        var correlationId = UUID.randomUUID();
        var idempotencyKey = UUID.randomUUID();

        when(consentRepository.findActiveByCustomerAndProvider(customerId, "EXPERIAN"))
            .thenReturn(Optional.empty());
        when(encryptionService.encrypt(anyString())).thenReturn("encrypted".getBytes());

        // When
        var result = handler.handle(command, customerId, correlationId, idempotencyKey);

        // Then
        assertThat(result.status()).isEqualTo("GRANTED");
        assertThat(result.customerId()).isEqualTo(customerId);
        verify(consentRepository).save(any(Consent.class));
        verify(eventPublisher).publish(eq("credit-coach.consent.granted"), any(), eq(customerId.toString()));
    }

    @Test
    void shouldReturn409WhenActiveConsentExists() {
        // Given
        var command = new GrantConsentCommand("EXPERIAN", "1.0", "abc123...sha256", "IOS", true);
        var customerId = UUID.randomUUID();
        when(consentRepository.findActiveByCustomerAndProvider(customerId, "EXPERIAN"))
            .thenReturn(Optional.of(existingConsent()));

        // When/Then
        assertThatThrownBy(() -> handler.handle(command, customerId, UUID.randomUUID(), UUID.randomUUID()))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Active consent already exists");
    }
}
```

### 10.2 Integration Tests (CRA Circuit Breaker)

**Framework:** Spring Boot Test + Testcontainers (Redis, PostgreSQL) + WireMock (CRA mock)

```java
@SpringBootTest
@Testcontainers
class ExperianCircuitBreakerIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired private ExperianCraClient craClient;
    @Autowired private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void shouldOpenCircuitAfterThreeFailures() {
        // Given: WireMock returns 503 for Experian
        wireMock.stubFor(post("/credit-score/v2/soft-search")
            .willReturn(serviceUnavailable()));

        var cb = circuitBreakerRegistry.circuitBreaker("experian");

        // When: 3 calls fail
        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(() -> craClient.retrieveScore(UUID.randomUUID()).get())
                .hasCauseInstanceOf(WebClientResponseException.class);
        }

        // Then: circuit is OPEN
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void shouldServeStaleCacheWhenCircuitOpen() {
        // Given: cached score exists, circuit is OPEN
        redisTemplate.opsForValue().set(
            "credit-coach:score:" + customerId + ":EXPERIAN",
            cachedScoreJson, Duration.ofHours(24));
        forceCircuitOpen();

        // When
        var result = craClient.retrieveScore(customerId).get();

        // Then
        assertThat(result.isStale()).isTrue();
        assertThat(result.getScore()).isEqualTo(742);
    }

    @Test
    void shouldTransitionToHalfOpenAfterWaitDuration() throws Exception {
        forceCircuitOpen();
        Thread.sleep(61_000);  // Wait > 60s configured wait duration

        var cb = circuitBreakerRegistry.circuitBreaker("experian");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
    }
}
```

### 10.3 Contract Tests (Pub/Sub Events)

**Framework:** Spring Cloud Contract + Pub/Sub emulator

```java
@SpringBootTest
@AutoConfigurePubSubEmulator
class ScoreEventContractTest {

    @Autowired private CreditCoachEventPublisher publisher;
    @Autowired private PubSubTemplate pubSubTemplate;

    @Test
    void scoreRetrievedEventMatchesContract() {
        // Given
        var event = ScoreEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType("score.retrieved")
            .timestamp(Instant.now())
            .customerId(UUID.randomUUID())
            .provider("EXPERIAN")
            .score(742)
            .band("good")
            .dataQualityScore(98)
            .correlationId(UUID.randomUUID())
            .build();

        // When
        publisher.publish("credit-coach.score.retrieved", event, event.customerId().toString());

        // Then: verify message received and schema matches
        var received = pubSubTemplate.pullNext("score-retrieved-test-sub");
        assertThat(received).isNotNull();

        var parsed = objectMapper.readValue(received.getData().toStringUtf8(), ScoreEvent.class);
        assertThat(parsed.eventType()).isEqualTo("score.retrieved");
        assertThat(parsed.score()).isBetween(0, 999);
        assertThat(parsed.band()).isIn("poor", "fair", "good", "very_good", "excellent");
        assertThat(parsed.eventId()).isNotNull();
        assertThat(parsed.correlationId()).isNotNull();
    }

    @Test
    void consentGrantedEventMatchesContract() {
        var event = new ConsentEvent(
            UUID.randomUUID(), "consent.granted", Instant.now(),
            UUID.randomUUID(), "EXPERIAN", UUID.randomUUID(), UUID.randomUUID()
        );

        publisher.publish("credit-coach.consent.granted", event, event.customerId().toString());

        var received = pubSubTemplate.pullNext("consent-granted-test-sub");
        var parsed = objectMapper.readValue(received.getData().toStringUtf8(), ConsentEvent.class);
        assertThat(parsed.eventType()).isEqualTo("consent.granted");
        assertThat(parsed.craProvider()).isIn("EXPERIAN", "EQUIFAX", "TRANSUNION");
    }

    @Test
    void subscriberHandlesDuplicateEventsIdempotently() {
        var event = new ConsentEvent(
            UUID.randomUUID(), "consent.granted", Instant.now(),
            UUID.randomUUID(), "EXPERIAN", UUID.randomUUID(), UUID.randomUUID()
        );

        // Publish same event twice
        publisher.publish("credit-coach.consent.granted", event, event.customerId().toString());
        publisher.publish("credit-coach.consent.granted", event, event.customerId().toString());

        // Verify handler processes only once (via idempotency key in Redis)
        await().atMost(5, SECONDS).untilAsserted(() -> {
            var processedCount = getProcessedCount(event.eventId());
            assertThat(processedCount).isEqualTo(1);
        });
    }
}
```

### 10.4 Test Coverage Targets

| Service | Unit Coverage | Integration Tests | Contract Tests |
|---------|-------------|-------------------|----------------|
| ConsentService | ≥80% line | DB operations, idempotency | Pub/Sub event schemas |
| CreditScoreService | ≥80% line | Circuit breaker, Redis cache, CRA mock | Pub/Sub event schemas |
| ConversationalAgentService | ≥80% line | Envoy mock, Vertex AI mock | N/A (stateless) |

### 10.5 Test Infrastructure

| Component | Test Replacement |
|-----------|-----------------|
| Cloud SQL (PostgreSQL) | Testcontainers `postgres:15-alpine` |
| Memorystore (Redis) | Testcontainers `redis:7-alpine` |
| Pub/Sub | GCP Pub/Sub emulator |
| Experian CRA | WireMock |
| Envoy Orchestrator | WireMock (gRPC) |
| Vertex AI | Mock (in-process) |
| Cloud KMS | Local mock (test keys) |

---

## 11. Handler-to-Endpoint Traceability Matrix

| API Endpoint | Method | Handler | Service | Events | Stories |
|-------------|--------|---------|---------|--------|---------|
| `/api/v1/credit-coach/consents` | POST | GrantConsentCommandHandler | ConsentService | `consent.granted` | US-003 |
| `/api/v1/credit-coach/consents/{customerId}` | GET | GetConsentsQueryHandler | ConsentService | — | US-003, US-004 |
| `/api/v1/credit-coach/consents/{consentId}/withdraw` | POST | WithdrawConsentCommandHandler | ConsentService | `consent.revoked` | US-004 |
| `/api/v1/credit-coach/scores/{customerId}` | GET | GetScoreQueryHandler + RetrieveScoreCommandHandler | CreditScoreService | `score.retrieved`, `score.changed` | US-008, US-009, US-010, US-057 |
| `/api/v1/credit-coach/scores/{customerId}/refresh` | POST | RefreshScoreCommandHandler | CreditScoreService | `score.retrieved`, `score.changed` | US-008, US-052 |
| `/api/v1/credit-coach/scores/{customerId}/factors` | GET | GetFactorsQueryHandler | CreditScoreService | — | US-011, US-053 |
| `/api/v1/credit-coach/scores/{customerId}/change-explanation` | GET | GetChangeExplanationQueryHandler | CreditScoreService | — | US-012 |
| `/api/v1/credit-coach/scores/{customerId}/history` | GET | GetScoreHistoryQueryHandler | CreditScoreService | — | US-013 |
| `/api/v1/credit-coach/conversations` | POST | SendConversationalQueryHandler | ConversationalAgentService | — | US-014, US-015 |
| `/api/v1/credit-coach/admin/refresh-schedule` | GET | GetRefreshConfigQueryHandler | CreditScoreService | — | US-017 |
| `/api/v1/credit-coach/admin/refresh-schedule` | PUT | UpdateRefreshConfigCommandHandler | CreditScoreService | — | US-017 |

---

## 12. Spring Boot Project Structure

```
credit-coach-services/
├── consent-service/
│   ├── src/main/java/com/lloyds/creditcoach/consent/
│   │   ├── ConsentServiceApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── PubSubConfig.java
│   │   ├── controller/
│   │   │   └── ConsentController.java
│   │   ├── command/
│   │   │   ├── GrantConsentCommandHandler.java
│   │   │   └── WithdrawConsentCommandHandler.java
│   │   ├── query/
│   │   │   └── GetConsentsQueryHandler.java
│   │   ├── domain/
│   │   │   ├── Consent.java                    (JPA Entity)
│   │   │   ├── ConsentStatus.java              (Enum)
│   │   │   └── ConsentStateMachine.java
│   │   ├── repository/
│   │   │   └── ConsentRepository.java          (Spring Data JPA)
│   │   ├── event/
│   │   │   ├── ConsentEvent.java
│   │   │   └── CreditCoachEventPublisher.java
│   │   ├── encryption/
│   │   │   └── CloudKmsEncryptionService.java
│   │   └── dto/
│   │       ├── ConsentResponseDto.java
│   │       └── GrantConsentCommand.java
│   └── src/test/java/...
│
├── credit-score-service/
│   ├── src/main/java/com/lloyds/creditcoach/score/
│   │   ├── CreditScoreServiceApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── Resilience4jConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   └── PubSubConfig.java
│   │   ├── controller/
│   │   │   ├── ScoreController.java
│   │   │   └── AdminController.java
│   │   ├── command/
│   │   │   ├── RetrieveScoreCommandHandler.java
│   │   │   ├── RefreshScoreCommandHandler.java
│   │   │   └── UpdateRefreshConfigCommandHandler.java
│   │   ├── query/
│   │   │   ├── GetScoreQueryHandler.java
│   │   │   ├── GetFactorsQueryHandler.java
│   │   │   ├── GetChangeExplanationQueryHandler.java
│   │   │   ├── GetScoreHistoryQueryHandler.java
│   │   │   └── GetRefreshConfigQueryHandler.java
│   │   ├── domain/
│   │   │   ├── CreditScore.java                (JPA Entity)
│   │   │   ├── ScoreFactor.java                (JPA Entity)
│   │   │   ├── ScoreRefreshSchedule.java       (JPA Entity)
│   │   │   └── CraApiAuditLog.java             (JPA Entity)
│   │   ├── repository/
│   │   │   ├── CreditScoreRepository.java
│   │   │   ├── ScoreFactorRepository.java
│   │   │   ├── RefreshScheduleRepository.java
│   │   │   └── CraApiAuditLogRepository.java
│   │   ├── integration/
│   │   │   ├── ExperianCraClient.java
│   │   │   └── ConsentServiceClient.java       (gRPC)
│   │   ├── cache/
│   │   │   └── ScoreCacheService.java
│   │   ├── event/
│   │   │   ├── ScoreEvent.java
│   │   │   ├── ScoreChangedEvent.java
│   │   │   ├── CreditCoachEventPublisher.java
│   │   │   └── ConsentGrantedEventSubscriber.java
│   │   ├── encryption/
│   │   │   └── CloudKmsEncryptionService.java
│   │   └── dto/
│   │       ├── ScoreResponseDto.java
│   │       ├── FactorsResponseDto.java
│   │       ├── ChangeExplanationResponseDto.java
│   │       └── ScoreHistoryResponseDto.java
│   └── src/test/java/...
│
├── conversational-agent-service/
│   ├── src/main/java/com/lloyds/creditcoach/conversation/
│   │   ├── ConversationalAgentServiceApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── Resilience4jConfig.java
│   │   ├── controller/
│   │   │   └── ConversationController.java
│   │   ├── command/
│   │   │   └── SendConversationalQueryHandler.java
│   │   ├── integration/
│   │   │   ├── EnvoyOrchestratorClient.java    (gRPC)
│   │   │   ├── VertexAiClient.java             (gRPC)
│   │   │   └── CreditScoreServiceClient.java   (gRPC)
│   │   └── dto/
│   │       ├── ConversationResponseDto.java
│   │       └── SendConversationalQueryCommand.java
│   └── src/test/java/...
│
└── shared-lib/
    └── src/main/java/com/lloyds/creditcoach/shared/
        ├── event/DomainEvent.java
        ├── dto/ResponseMeta.java
        ├── dto/ProblemDetails.java
        ├── exception/GlobalExceptionHandler.java
        └── logging/PiiRedactionFilter.java
```

---

## 13. Dependencies (Key Libraries)

| Dependency | Version | Purpose |
|-----------|---------|---------|
| `org.springframework.boot:spring-boot-starter-web` | 3.3.x | REST API framework |
| `org.springframework.boot:spring-boot-starter-data-jpa` | 3.3.x | ORM (Hibernate + Spring Data) |
| `org.springframework.boot:spring-boot-starter-security` | 3.3.x | OAuth2 resource server |
| `org.springframework.boot:spring-boot-starter-validation` | 3.3.x | Jakarta Bean Validation |
| `org.springframework.boot:spring-boot-starter-data-redis` | 3.3.x | Redis cache client |
| `com.google.cloud:spring-cloud-gcp-starter-pubsub` | 5.x | Pub/Sub integration |
| `com.google.cloud:google-cloud-kms` | 2.x | Cloud KMS encryption |
| `io.github.resilience4j:resilience4j-spring-boot3` | 2.2.x | Circuit breaker, retry, time limiter |
| `org.postgresql:postgresql` | 42.7.x | PostgreSQL JDBC driver |
| `org.flywaydb:flyway-core` | 10.x | Database migrations |
| `io.micrometer:micrometer-registry-prometheus` | 1.13.x | Metrics export |
| `net.devh:grpc-spring-boot-starter` | 3.1.x | gRPC client/server |

**Test Dependencies:**
| Dependency | Version | Purpose |
|-----------|---------|---------|
| `org.testcontainers:postgresql` | 1.19.x | PostgreSQL integration tests |
| `org.testcontainers:gcloud` | 1.19.x | Pub/Sub emulator |
| `org.wiremock:wiremock-standalone` | 3.x | HTTP mock (CRA, Envoy) |
| `org.springframework.boot:spring-boot-starter-test` | 3.3.x | JUnit 5, Mockito, AssertJ |

---

## Appendix A: Band Classification Logic

```java
public class BandClassifier {
    public static String classifyBand(int score, String provider) {
        return switch (provider) {
            case "EXPERIAN" -> classifyExperian(score);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    private static String classifyExperian(int score) {
        if (score >= 961) return "excellent";
        if (score >= 881) return "good";
        if (score >= 721) return "fair";
        if (score >= 561) return "poor";
        return "very_poor";
    }
}
```

## Appendix B: Glossary

| Term | Definition |
|------|-----------|
| CRA | Credit Reference Agency (Experian, Equifax, TransUnion) |
| CMEK | Customer-Managed Encryption Keys (Cloud KMS) |
| DEK | Data Encryption Key (wrapped by KMS master key) |
| CQRS | Command Query Responsibility Segregation |
| DLQ | Dead Letter Queue |
| BFF | Backend for Frontend |
| DPIA | Data Protection Impact Assessment |
| Soft Search | Credit check that doesn't leave a footprint on credit file |

## Appendix C: Configuration Reference

```yaml
# application.yml — CreditScoreService
spring:
  application:
    name: credit-score-service
  datasource:
    url: jdbc:postgresql://${CLOUD_SQL_HOST}:5432/scores
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 5000
  data:
    redis:
      host: ${REDIS_HOST}
      port: 6379
      ssl:
        enabled: true
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

credit-coach:
  cra:
    experian:
      base-url: https://api.experian.co.uk
      api-key: ${EXPERIAN_API_KEY}
      timeout-ms: 2500
  cache:
    score-ttl-hours: 24
  encryption:
    key-ring: score-keys
    key-name: score-value-key
    project: lloyds-cra-data
    location: europe-west2
  pubsub:
    project-id: ${GCP_PROJECT_ID}
    topics:
      score-retrieved: credit-coach.score.retrieved
      score-changed: credit-coach.score.changed
```

---

## 14. Mobile BFF Aggregation Handlers

Per the solution architecture §10.1 (MM5/MM6), the Mobile BFF provides screen-shaped responses by aggregating backend service calls. The BFF contains NO business logic — it orchestrates and shapes only.

### 14.1 Dashboard BFF Handler

**Endpoint:** `GET /mobile/v1/credit-coach/dashboard`

**Aggregates:** ConsentService + CreditScoreService (score + factors + change)

```java
@RestController
@RequestMapping("/mobile/v1/credit-coach")
public class CreditCoachBffController {

    private final ConsentServiceClient consentClient;      // gRPC
    private final CreditScoreServiceClient scoreClient;    // gRPC

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardBffResponse> getDashboard(
            @AuthenticationPrincipal JwtAuthenticationToken jwt) {

        String customerId = jwt.getToken().getSubject();

        // Parallel calls to backend services
        CompletableFuture<ConsentStatus> consentFuture =
            CompletableFuture.supplyAsync(() -> consentClient.getStatus(customerId));
        CompletableFuture<ScoreData> scoreFuture =
            CompletableFuture.supplyAsync(() -> scoreClient.getCurrentScore(customerId));
        CompletableFuture<List<Factor>> factorsFuture =
            CompletableFuture.supplyAsync(() -> scoreClient.getTopFactors(customerId, 4));

        // Aggregate into screen-shaped response
        DashboardBffResponse response = DashboardBffResponse.builder()
            .consentActive(consentFuture.join().isGranted())
            .score(scoreFuture.join().getScore())
            .maxScore(scoreFuture.join().getMaxScore())
            .band(scoreFuture.join().getBand())
            .bandLabel(scoreFuture.join().getBandLabel())
            .change(scoreFuture.join().getChange())
            .changeDirection(scoreFuture.join().getChangeDirection())
            .isStale(scoreFuture.join().isStale())
            .retrievedAt(scoreFuture.join().getRetrievedAt())
            .topFactors(factorsFuture.join())  // Only top 4 for mobile
            .build();

        // Strip internal fields (no dataQualityScore, no audit timestamps)
        return ResponseEntity.ok(response);
    }
}
```

**Response shape (mobile-only fields per MM6):**
```json
{
  "consentActive": true,
  "score": 742,
  "maxScore": 1250,
  "band": "good",
  "bandLabel": "Good (700–849)",
  "change": 15,
  "changeDirection": "up",
  "isStale": false,
  "retrievedAt": "2026-05-03T10:00:00Z",
  "topFactors": [
    { "title": "Payment history is strong", "direction": "positive", "impact": "high" },
    { "title": "Credit utilisation is 62%", "direction": "negative", "impact": "high" }
  ]
}
```

**Fields deliberately excluded from mobile response:**
- `dataQualityScore` (internal metric)
- `createdAt`, `updatedAt` (audit fields)
- `customerId` (client already knows)
- `provider` details beyond name
- Full factor descriptions (only title shown on dashboard; full list on factors screen)

### 14.2 BFF Endpoint Summary

| BFF Endpoint | Backend Calls | Response Shaping |
|-------------|---------------|-----------------|
| `GET /mobile/v1/credit-coach/dashboard` | ConsentService.getStatus + ScoreService.getScore + ScoreService.getTopFactors | Merge into single response; strip audit fields; limit factors to top 4 |
| `GET /mobile/v1/credit-coach/consent-status` | ConsentService.getConsents | Pass-through with field filtering |
| `POST /mobile/v1/credit-coach/consent` | ConsentService.grantConsent | Pass-through; add device context from mobile headers |
| `POST /mobile/v1/credit-coach/conversation` | ConversationalAgentService.sendQuery | Pass-through; add session context |
| `GET /mobile/v1/credit-coach/factors` | ScoreService.getFactors | Full factor list (no truncation) |
| `GET /mobile/v1/credit-coach/history?months=N` | ScoreService.getHistory | Pass-through |

### 14.3 BFF Design Rules

1. **No business logic** — BFF never makes credit decisions, validates consent, or computes scores
2. **Parallel calls** — Use `CompletableFuture.allOf()` to call services in parallel where possible
3. **Timeout** — BFF timeout (3s) > individual service timeout (2.5s) per MS4
4. **Circuit breaker** — BFF has its own circuit breaker per downstream service
5. **Caching** — BFF does NOT cache (caching is at service level in Redis)
6. **Error aggregation** — If one service fails, return partial response with degraded indicator

**Reference:** Mobile Microfrontend BFF KB MM5 ("aggregates microservice calls"), MM6 ("one screen, one call"), Solution Architecture §10.1 (ADR-10)
