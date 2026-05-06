# Integration Architecture — AI Credit Coach (Sprint 1)
### EP-01: Platform Foundation & EP-02: Credit Score Dashboard
### Version 1.0 · May 2026

---

## 1. Integration Landscape

```
                    ┌─────────────────────────────┐
                    │     Lloyds Mobile App        │
                    │   (iOS Swift / Android Kotlin)│
                    └──────────────┬──────────────┘
                                   │ HTTPS (TLS 1.3)
                                   ▼
                    ┌─────────────────────────────┐
                    │       Apigee Gateway         │
                    │  (Rate limit, Auth, Route)   │
                    └──────────────┬──────────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              │                    │                    │
              ▼                    ▼                    ▼
    ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐
    │   Consent    │    │ Credit Score │    │  Conversational  │
    │   Service    │    │   Service    │    │  Agent Service   │
    └──────┬───────┘    └───┬──┬──┬───┘    └────────┬─────────┘
           │                │  │  │                  │
           │ Pub/Sub        │  │  │ Pub/Sub          │ gRPC
           ▼                │  │  ▼                  ▼
    ┌──────────────┐        │  │  ┌──────────┐  ┌──────────────┐
    │   Pub/Sub    │◄───────┘  │  │ Pub/Sub  │  │    Envoy     │
    │  (Events)    │           │  │ (Events) │  │ Orchestrator │
    └──────────────┘           │  └──────────┘  └──────┬───────┘
                               │                       │
              ┌────────────────┼───────────────┐       │ gRPC
              │                │               │       ▼
              ▼                ▼               ▼  ┌──────────┐
    ┌──────────────┐  ┌──────────────┐  ┌─────┐  │Vertex AI │
    │  Cloud SQL   │  │ Memorystore  │  │Cloud│  │ (Gemini) │
    │ (PostgreSQL) │  │   (Redis)    │  │ KMS │  └──────────┘
    └──────────────┘  └──────────────┘  └─────┘
              │
              │ REST (circuit breaker)
              ▼
    ┌──────────────────┐
    │   Experian CRA   │
    │   (External)     │
    └──────────────────┘
```

---

## 2. Integration Inventory

| # | System | Direction | Protocol | Auth | Service Owner | Stories |
|---|--------|-----------|----------|------|---------------|---------|
| INT-01 | Experian CRA | Outbound | REST/HTTPS | API Key + mTLS | CreditScoreService | US-008, US-052, US-057 |
| INT-02 | Envoy Orchestrator | Bidirectional | gRPC | Workload Identity | ConversationalAgentService | US-001, US-002, US-014, US-015 |
| INT-03 | Vertex AI (Gemini) | Outbound | gRPC | Workload Identity | ConversationalAgentService | US-014, US-015 |
| INT-04 | Cloud Pub/Sub | Publish | gRPC | Workload Identity | ConsentService, CreditScoreService | US-003, US-004, US-008 |
| INT-05 | Memorystore (Redis) | Read/Write | Redis protocol | IAM auth | CreditScoreService | US-008, US-057 |
| INT-06 | Cloud KMS | Outbound | gRPC | Workload Identity | All services | US-006 |
| INT-07 | Cloud SQL | Read/Write | PostgreSQL wire | IAM auth + SSL | ConsentService, CreditScoreService | All |
| INT-08 | Apigee Gateway | Inbound | HTTPS | OAuth 2.0 JWT | All (fronted) | All |

---

## 3. Integration Details

### INT-01: Experian CRA API

| Aspect | Detail |
|--------|--------|
| **Endpoint** | `https://api.experian.co.uk/credit-score/v2/soft-search` |
| **Protocol** | REST/HTTPS (TLS 1.3) |
| **Authentication** | API Key (header) + mutual TLS (client certificate) |
| **Request** | Customer identifiers (name, DOB, address — encrypted in transit) |
| **Response** | Score (integer), band, factors array, data quality indicator |
| **SLA** | 99.5% availability, p95 latency < 2s (per CRA partnership SLA) |
| **Rate limit** | TBD per commercial agreement (estimated 100 req/s) |
| **Data classification** | Restricted — response encrypted before storage |
| **Regulatory** | CONC 5.2A (soft search only — no credit footprint) |
| **Error codes** | 400 (invalid request), 401 (auth failure), 404 (customer not found at CRA), 429 (rate limited), 500/503 (CRA system error) |

**Request flow:**
```
CreditScoreService
  → Check consent (gRPC to ConsentService)
  → Check Redis cache (hit? return cached)
  → Call Experian API (circuit breaker wrapped)
  → Validate data quality (≥95%)
  → Encrypt score_value
  → Store in Cloud SQL
  → Update Redis cache (24hr TTL)
  → Publish score.retrieved event
  → If score changed: publish score.changed event
  → Return to caller
```

### INT-02: Envoy Orchestrator

| Aspect | Detail |
|--------|--------|
| **Protocol** | gRPC (HTTP/2 + Protobuf) |
| **Authentication** | Workload Identity Federation (no static credentials) |
| **Purpose** | Register Credit Coach as specialist agent; receive routed queries |
| **Registration** | Agent template with intent patterns, confidence threshold (0.85) |
| **Session** | Envoy manages persistent memory across conversations |
| **SLA** | Internal — 99.9% availability (same GKE cluster) |
| **Latency** | < 50ms for routing decision |

### INT-03: Vertex AI (Gemini)

| Aspect | Detail |
|--------|--------|
| **Protocol** | gRPC via Vertex AI SDK |
| **Authentication** | Workload Identity → Vertex AI service account |
| **Purpose** | Intent classification + natural language response generation |
| **Model** | Gemini (fine-tuned for credit domain) |
| **Latency** | p95 < 2s for response generation |
| **Fallback** | If Vertex AI unavailable: return "I'm having trouble right now. Try again in a moment." |
| **Cost** | Per-token pricing; budget alert at 80% monthly allocation |

### INT-05: Memorystore (Redis)

| Aspect | Detail |
|--------|--------|
| **Protocol** | Redis 7 protocol (TLS enabled) |
| **Authentication** | IAM-based auth (no password) |
| **Purpose** | Score cache (24hr TTL), circuit breaker state |
| **Key pattern** | `score:{customerId}:{provider}` → JSON payload |
| **TTL** | 24 hours (configurable per ADR-02) |
| **Eviction** | LRU when memory > 80% |
| **Cluster** | 2 nodes (primary + replica) for HA |

---

## 4. Circuit Breaker Configuration

| Integration | Timeout | Failure Threshold | Break Duration | Fallback | KB Reference |
|-------------|---------|-------------------|----------------|----------|--------------|
| Experian CRA (INT-01) | 2.5s | 3 failures in 30s | 60s (OPEN state) | Serve Redis cache with `isStale: true` | Enterprise Architecture KB §3.2, ADR-02 |
| Envoy Orchestrator (INT-02) | 1s | 5 failures in 30s | 30s | Return "Service temporarily unavailable" | Microservices Architecture KB MS3 |
| Vertex AI (INT-03) | 4s | 3 failures in 60s | 120s | Return canned "try again" response | Enterprise Architecture KB §2.1 |

**Implementation:** Resilience4j (Spring Boot integration)

```java
@CircuitBreaker(name = "experian", fallbackMethod = "getScoreFromCache")
@Retry(name = "experian", maxAttempts = 2)
@TimeLimiter(name = "experian", timeoutDuration = "2500ms")
public ScoreResponse retrieveFromCra(String customerId) { ... }
```

**Circuit breaker states:**
```
CLOSED (normal) → OPEN (failures exceeded) → HALF_OPEN (after break duration)
                                                    ↓
                                              Test request
                                                    ↓
                                    Success → CLOSED | Failure → OPEN
```

---

## 5. Event-Driven Architecture

### 5.1 Pub/Sub Topics

| Topic | Publisher | Subscribers (S1) | Future Subscribers | Schema |
|-------|-----------|-------------------|-------------------|--------|
| `credit-coach.consent.granted` | ConsentService | CreditScoreService (trigger first retrieval) | — | ConsentEvent |
| `credit-coach.consent.revoked` | ConsentService | CreditScoreService (stop refresh scheduling) | — | ConsentEvent |
| `credit-coach.score.retrieved` | CreditScoreService | BigQuery (analytics via Dataflow) | — | ScoreEvent |
| `credit-coach.score.changed` | CreditScoreService | BigQuery (analytics) | AlertService (S4), ImprovementPlanService (S2) | ScoreChangedEvent |

### 5.2 Event Schemas

**ConsentEvent:**
```json
{
  "eventId": "uuid",
  "eventType": "consent.granted | consent.revoked",
  "timestamp": "ISO-8601",
  "customerId": "uuid",
  "craProvider": "EXPERIAN | EQUIFAX | TRANSUNION",
  "consentId": "uuid",
  "correlationId": "uuid"
}
```

**ScoreEvent:**
```json
{
  "eventId": "uuid",
  "eventType": "score.retrieved",
  "timestamp": "ISO-8601",
  "customerId": "uuid",
  "provider": "EXPERIAN",
  "score": 742,
  "band": "good",
  "dataQualityScore": 98,
  "correlationId": "uuid"
}
```

**ScoreChangedEvent:**
```json
{
  "eventId": "uuid",
  "eventType": "score.changed",
  "timestamp": "ISO-8601",
  "customerId": "uuid",
  "provider": "EXPERIAN",
  "previousScore": 727,
  "currentScore": 742,
  "change": 15,
  "changeDirection": "up",
  "correlationId": "uuid"
}
```

### 5.3 Delivery Guarantees

| Aspect | Configuration |
|--------|---------------|
| Delivery | At-least-once (Pub/Sub default) |
| Ordering | Per-customer ordering via ordering key = `customerId` |
| Deduplication | Subscribers use `eventId` for idempotent processing |
| Dead letter | After 5 failed deliveries → dead letter topic → alert ops team |
| Retention | 7 days message retention (for replay if subscriber fails) |

---

## 6. Retry Policies

| Integration | Max Retries | Backoff Strategy | Retry On | Don't Retry On |
|-------------|-------------|------------------|----------|----------------|
| Experian CRA | 2 | Exponential (1s, 2s) | 500, 503, timeout | 400, 401, 404, 429 |
| Envoy Orchestrator | 1 | Immediate | 503, timeout | All others |
| Vertex AI | 2 | Exponential (500ms, 1s) | 503, timeout, 429 | 400, 401 |
| Cloud SQL | 3 | Exponential (100ms, 200ms, 400ms) | Connection timeout | Query errors |
| Redis | 1 | Immediate | Connection timeout | All others |

---

## 7. Data Flow Diagrams

### 7.1 Consent Grant Flow

```
Customer → [Consent Screen] → Apigee → ConsentService
                                              │
                                    ┌─────────┼─────────┐
                                    │         │         │
                                    ▼         ▼         ▼
                              Cloud SQL   Pub/Sub    Response
                              (INSERT)   (consent    (201)
                                         .granted)
                                              │
                                              ▼
                                    CreditScoreService
                                    (subscribes → triggers
                                     first score retrieval)
```

### 7.2 Score Retrieval Flow

```
Customer → [Dashboard] → Apigee → CreditScoreService
                                         │
                                    ┌────┴────┐
                                    │ Redis?  │
                                    └────┬────┘
                                   hit/  │  \miss
                                  /      │    \
                          Return    Check     Call Experian
                          cached    consent   (circuit breaker)
                                    (gRPC)         │
                                         │    ┌────┴────┐
                                         │    │Validate │
                                         │    │quality  │
                                         │    └────┬────┘
                                         │         │ ≥95%
                                         │    ┌────┴────┐
                                         │    │Encrypt  │
                                         │    │& Store  │
                                         │    └────┬────┘
                                         │         │
                                         │    ┌────┴────┐
                                         │    │Update   │
                                         │    │Redis    │
                                         │    └────┬────┘
                                         │         │
                                         │    ┌────┴────┐
                                         │    │Publish  │
                                         │    │events   │
                                         │    └────┬────┘
                                         │         │
                                         └────┬────┘
                                              │
                                         Return score
```

### 7.3 Conversational Query Flow

```
Customer → [Chat UI] → Apigee → ConversationalAgentService
                                         │
                                    ┌────┴────┐
                                    │ Envoy   │ (intent classification)
                                    │Orchestr.│
                                    └────┬────┘
                                         │
                              ┌──────────┼──────────┐
                              │          │          │
                         credit_query  out_of_scope  ambiguous
                              │          │          │
                              ▼          ▼          ▼
                         Vertex AI   Route to    Return
                         (generate   other FA    clarification
                          response)  agent       prompt
                              │
                         ┌────┴────┐
                         │ Fetch   │ (gRPC to CreditScoreService)
                         │ score   │
                         │ data    │
                         └────┬────┘
                              │
                         Compose response
                         + suggested actions
                         + disclaimer (if estimates)
```

---

## 8. Security Integration Controls

| Control | Implementation | Applies To |
|---------|---------------|------------|
| API Key rotation | Quarterly rotation via Secret Manager | INT-01 (Experian) |
| Certificate rotation | Auto-rotation via Certificate Manager | INT-01 (mTLS) |
| Token validation | Apigee validates JWT signature + expiry + audience | All inbound |
| IP allowlisting | Experian API calls from fixed egress IPs (Cloud NAT) | INT-01 |
| Request signing | HMAC-SHA256 on CRA request body for integrity | INT-01 |
| PII redaction | Structured logging with PII fields masked | All services |
| Audit trail | All CRA calls logged to `cra_api_audit_log` (request hash, status, latency) | INT-01 |

---

## 9. Monitoring & Alerting

| Metric | Threshold | Alert | Action |
|--------|-----------|-------|--------|
| CRA circuit breaker OPEN | State = OPEN | P2 (15 min) | Ops investigates; customers see cached data |
| Score retrieval latency p95 | > 3s | P3 (30 min) | Check CRA latency, Redis health |
| Consent grant error rate | > 5% | P2 (15 min) | Check Cloud SQL connectivity |
| Vertex AI latency p95 | > 4s | P3 (30 min) | Check model serving, consider scaling |
| Redis hit rate | < 80% | P4 (1 hour) | Check TTL config, cache warming |
| Pub/Sub dead letter messages | > 0 | P3 (30 min) | Investigate failed subscriber |
| Data quality rejections | > 5% of retrievals | P3 (30 min) | Contact CRA partner |

---

## 10. Compliance Traceability

| Regulation | Integration Impact | Control |
|-----------|-------------------|---------|
| UK GDPR Art. 6/7 | Consent must be verified before CRA call (INT-01) | ConsentService gRPC check |
| UK GDPR Art. 13 | Privacy notice shown before consent capture | UI enforcement (Screen 1) |
| CONC 5.2A | Only soft search — no credit footprint | Experian API endpoint is soft-search specific |
| FCA SYSC 9.1.1R | CRA call audit logs retained 6 years | `cra_api_audit_log` table, retention policy |
| PRA FS2/23 | AI responses must be explainable | Vertex AI response includes reasoning; disclaimer on estimates |
| NFR-25 | Data quality ≥ 95% | Validation gate before storage; reject and retry below threshold |

---

## 11. Operational Procedures

### 11.1 Runbooks (to be created before go-live)

| Runbook | Trigger | Owner |
|---------|---------|-------|
| CRA Circuit Breaker Open | Alert: `cra_circuit_state = OPEN` | Platform Ops |
| Score Retrieval Latency Degradation | Alert: p95 > 3s for 5 min | Platform Ops |
| Consent Service Database Failover | Alert: Cloud SQL primary unhealthy | DBA Team |
| Pub/Sub Dead Letter Processing | Alert: DLQ messages > 0 | Platform Ops |
| Redis Cache Eviction Spike | Alert: eviction rate > 100/min | Platform Ops |
| Vertex AI Model Degradation | Alert: intent confidence avg < 0.7 | ML Ops |

### 11.2 Pre-Launch Gates

| Gate | Status | Blocker For | Owner |
|------|--------|-------------|-------|
| DPIA completion | Required | Production deployment | Data Protection Office |
| CRA rate limit agreement | Required | >1000 customers enrolled | Commercial |
| Security penetration test | Required | Production deployment | InfoSec |
| Load test (10M simulated) | Required | Production deployment | Performance Team |
| Accessibility audit | Required | Production deployment | UX/Accessibility |

### 11.3 Capacity Planning

| Resource | Current Sizing | 10M Customer Target | Scale Trigger |
|----------|---------------|--------------------|----|
| Cloud SQL (scores) | 4 vCPU, 16GB RAM | 16 vCPU, 64GB RAM + read replicas | Connection pool > 80% |
| Redis (cache) | 2 nodes, 6GB each | 4 nodes, 13GB each (cluster mode) | Memory > 80% |
| GKE pods (Score Service) | 3–20 replicas | 20–50 replicas | CPU > 70% |
| Pub/Sub throughput | 1000 msg/s | 10,000 msg/s | Backlog > 1000 |
| CRA API calls | ~33/s (1M monthly) | ~330/s (10M monthly) | Per CRA agreement |

---

## 12. API Endpoint Reference

Full OpenAPI specifications maintained per microservice:

| Service | Spec Location | Endpoints |
|---------|--------------|-----------|
| ConsentService | `/api/consent-service-api.yaml` | POST /consents, GET /consents/{id}, POST /consents/{id}/withdraw, GET /consents/health |
| CreditScoreService | `/api/credit-score-service-api.yaml` | GET /scores/{id}, POST /scores/{id}/refresh, GET /scores/{id}/factors, GET /scores/{id}/change-explanation, GET /scores/{id}/history, GET+PUT /admin/refresh-schedule, GET /scores/health |
| ConversationalAgentService | `/api/conversational-agent-service-api.yaml` | POST /conversations, GET /conversations/health |
