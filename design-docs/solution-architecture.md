# Solution Architecture — AI Credit Coach (Sprint 1)
### EP-01: Platform Foundation & EP-02: Credit Score Dashboard
### Version 1.0 · May 2026

---

## 1. System Context

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         EXTERNAL ACTORS                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────┐    ┌──────────────┐    ┌──────────────┐                  │
│  │ Customer │    │ Ops Analyst  │    │ System Admin │                  │
│  │(iOS/And) │    │ (Internal)   │    │ (Internal)   │                  │
│  └────┬─────┘    └──────┬───────┘    └──────┬───────┘                  │
│       │                 │                   │                           │
└───────┼─────────────────┼───────────────────┼───────────────────────────┘
        │ HTTPS           │ HTTPS             │ HTTPS
        ▼                 ▼                   ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      LLOYDS PLATFORM (GKE)                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    Apigee API Gateway                            │   │
│  │         (Rate limiting, Auth validation, Routing)                │   │
│  └────────────────────────────┬────────────────────────────────────┘   │
│                               │                                         │
│  ┌────────────────────────────┼────────────────────────────────────┐   │
│  │                   Cloud Service Mesh (Istio)                     │   │
│  ├────────────────────────────┼────────────────────────────────────┤   │
│  │                            │                                     │   │
│  │  ┌──────────────┐  ┌──────┴───────┐  ┌───────────────────────┐ │   │
│  │  │   Consent    │  │ Credit Score │  │   Conversational      │ │   │
│  │  │   Service    │  │   Service    │  │   Agent Service       │ │   │
│  │  │              │  │              │  │                       │ │   │
│  │  │ • Grant      │  │ • Retrieve   │  │ • Intent classify     │ │   │
│  │  │ • Withdraw   │  │ • Cache      │  │ • Response generate   │ │   │
│  │  │ • Query      │  │ • Factors    │  │ • Route queries       │ │   │
│  │  │              │  │ • History    │  │                       │ │   │
│  │  └──────┬───────┘  └──┬───┬──────┘  └───────┬───────────────┘ │   │
│  │         │              │   │                  │                  │   │
│  └─────────┼──────────────┼───┼──────────────────┼──────────────────┘   │
│            │              │   │                  │                       │
│  ┌─────────┴──────────────┴───┴──────────────────┴──────────────────┐   │
│  │                    DATA & MESSAGING LAYER                         │   │
│  │                                                                   │   │
│  │  ┌──────────┐  ┌───────────┐  ┌─────────┐  ┌────────────────┐   │   │
│  │  │Cloud SQL │  │Memorystore│  │ Pub/Sub │  │   BigQuery     │   │   │
│  │  │(Postgres)│  │  (Redis)  │  │         │  │  (Analytics)   │   │   │
│  │  └──────────┘  └───────────┘  └─────────┘  └────────────────┘   │   │
│  └───────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
        │                                    │
        │ REST (circuit breaker)             │ gRPC
        ▼                                    ▼
┌──────────────┐                    ┌──────────────────┐
│   Experian   │                    │ Envoy Platform   │
│   CRA API    │                    │ (Orchestrator +  │
│              │                    │  Vertex AI)      │
└──────────────┘                    └──────────────────┘
```

### Actors

| Actor | Channel | Authentication | Stories |
|-------|---------|---------------|---------|
| Retail Customer | iOS / Android native app | OAuth 2.0 PKCE (existing Lloyds auth) | US-003–US-015 |
| Operations Analyst | Internal web (React) | OAuth 2.0 + Admin role | — |
| System Administrator | Internal web (React) | OAuth 2.0 + Admin role | US-017 |

---

## 2. Bounded Contexts

| Context | Service | Responsibility | Key Entities | Data Store |
|---------|---------|---------------|--------------|------------|
| Consent Management | ConsentService | Capture, store, withdraw CRA consent; publish consent events | Consent, ConsentAudit | Cloud SQL (own schema) |
| Credit Score | CreditScoreService | CRA integration, score caching, factor analysis, change explanation, history | Score, ScoreFactor, RefreshSchedule, CraAuditLog | Cloud SQL (own schema) + Redis |
| Conversational AI | ConversationalAgentService | Intent classification, response generation, query routing | None (stateless) | Envoy persistent memory |

**Bounded context rules** (per Microservices Architecture KB MS2):
- No shared database between contexts
- Cross-context communication via REST/gRPC (sync) or Pub/Sub (async)
- Each context independently deployable

---

## 3. Technology Decisions

| Layer | Choice | Rationale | KB Reference |
|-------|--------|-----------|--------------|
| Backend framework | Spring Boot 3.x (Java 21) | Confirmed Lloyds stack; mature ecosystem for financial services | Enterprise Architecture KB §1.1 |
| Container orchestration | GKE (Google Kubernetes Engine) | Confirmed Lloyds platform; auto-scaling, health probes | Enterprise Architecture KB §1.2 |
| Database | Cloud SQL (PostgreSQL 15) | ACID transactions for consent/score data; encryption at rest | Enterprise Architecture KB §1.2, ADR-03 |
| Cache | Memorystore (Redis 7) | Sub-ms latency for score reads; 24hr TTL | Enterprise Architecture KB §1.2, ADR-02 |
| Messaging | Google Cloud Pub/Sub | Event-driven score lifecycle; at-least-once delivery | Enterprise Architecture KB §1.2, ADR-05 |
| AI/ML | Vertex AI (Gemini) | Intent classification + response generation; Envoy integration | Enterprise Architecture KB §2.1 |
| API Gateway | Apigee | Rate limiting, auth validation, routing, analytics | Enterprise Architecture KB §3.2 |
| Service mesh | Cloud Service Mesh (Istio) | mTLS between services, traffic management, observability | Enterprise Architecture KB §3.2 |
| IaC | Terraform | All infrastructure defined as code; state in GCS | Enterprise Architecture KB §3.3 |
| CI/CD | Cloud Build → Cloud Deploy | Automated pipeline; canary deployments to GKE | Enterprise Architecture KB §3.3 |
| Monitoring | Cloud Monitoring + Prometheus | SLO alerting (99.9% uptime), latency tracking | Enterprise Architecture KB §3.3 |
| Mobile | Native (Swift iOS / Kotlin Android) | Confirmed Lloyds approach; within existing app shell | Enterprise Architecture KB §3.1 |

---

## 4. Security Architecture

### 4.1 Authentication & Authorization

| Flow | Mechanism | Details |
|------|-----------|---------|
| Customer → API | OAuth 2.0 Bearer JWT (PKCE) | Existing Lloyds auth; JWT contains customer_id in `sub` claim |
| Service → Service | Workload Identity Federation | GKE service accounts; no static credentials |
| Admin → API | OAuth 2.0 + RBAC | `credit-coach-admin` role required for /admin/* endpoints |
| Service → CRA | API Key + mTLS | Experian partner credentials in Secret Manager |

### 4.2 Encryption

| Data State | Mechanism | Key Management |
|------------|-----------|----------------|
| At rest (Cloud SQL) | AES-256 (Google-managed) | CMEK via Cloud KMS for Restricted data |
| At rest (Redis) | AES-256 (in-transit encryption enabled) | Google-managed |
| In transit (external) | TLS 1.3 | Certificate rotation via Certificate Manager |
| In transit (internal) | mTLS via Istio | Auto-rotated mesh certificates |
| PII fields (IP, device) | Application-level AES-256 | Dedicated KMS key per data domain |

### 4.3 Data Classification

| Classification | Examples | Controls |
|---------------|----------|----------|
| Restricted | CRA score values, IP addresses, device fingerprints | CMEK encryption, IAM isolation, no logging of values |
| Confidential | Factor descriptions, consent records, customer_id | Standard encryption, access logging |
| Internal | Refresh config, system health | Standard controls |
| Public | API documentation, health status | No controls |

### 4.4 Security Controls

- **FLAG_SECURE**: Set on all screens displaying score/factor data (prevents screenshots)
- **Certificate pinning**: Enabled for CRA API calls
- **Input validation**: All inputs validated server-side; reject SQL injection/XSS payloads
- **Rate limiting**: 100 read/min, 20 write/min per customer (Apigee)
- **Audit logging**: All consent changes, CRA calls, and admin actions logged immutably

---

## 5. Deployment Model

### 5.1 GKE Configuration

| Service | Namespace | Min Replicas | Max Replicas | CPU Request | Memory Request |
|---------|-----------|-------------|-------------|-------------|----------------|
| ConsentService | `credit-coach` | 2 | 10 | 250m | 512Mi |
| CreditScoreService | `credit-coach` | 3 | 20 | 500m | 1Gi |
| ConversationalAgentService | `credit-coach` | 2 | 15 | 500m | 1Gi |

### 5.2 Scaling Triggers

| Service | Metric | Threshold | Scale Action |
|---------|--------|-----------|--------------|
| CreditScoreService | CPU utilisation | > 70% | +1 replica |
| CreditScoreService | Request latency p95 | > 2s | +2 replicas |
| ConversationalAgentService | Queue depth | > 50 pending | +1 replica |

### 5.3 Deployment Strategy

- **Canary deployment**: 5% traffic → 25% → 100% over 30 minutes
- **Rollback**: Automatic if error rate > 1% or latency p95 > 3s
- **Blue-green**: Not used (canary preferred for gradual rollout)

---

## 6. Data Architecture

### 6.1 Schema Ownership

| Schema | Owner Service | Tables | Retention |
|--------|--------------|--------|-----------|
| `consent` | ConsentService | `credit_coach_consents` | Indefinite (audit) |
| `scores` | CreditScoreService | `credit_scores`, `credit_score_factors`, `score_refresh_schedules`, `cra_api_audit_log` | 6 years (FCA SYSC 9.1.1R) |

### 6.2 Key Tables (from Baseline BL5)

**credit_coach_consents** — Immutable audit trail
- PK: `id` (UUID)
- Encrypted: `ip_address`, `device_fingerprint` (AES-256)
- Indexes: `(customer_id, cra_provider, status)`
- Retention: Indefinite

**credit_scores** — Score history
- PK: `id` (UUID)
- Encrypted: `score_value` (AES-256 via CMEK)
- Indexes: `(customer_id, retrieved_at DESC)`, `(customer_id, provider)`
- Retention: 6 years post-service-closure
- Partitioned: by `customer_id` hash (16 partitions)

### 6.3 Analytics Pipeline

```
credit_scores (Cloud SQL) → Dataflow (CDC) → BigQuery (analytics)
```

- Real-time CDC via Datastream to BigQuery for reporting
- No PII in BigQuery (score values only, no customer identifiers without pseudonymisation)

---

## 7. Cross-Cutting Concerns

### 7.1 Observability

| Concern | Tool | Configuration |
|---------|------|---------------|
| Metrics | Cloud Monitoring + Prometheus | Custom metrics: `score_retrieval_latency_ms`, `cra_circuit_state`, `consent_grants_total` |
| Logging | Cloud Logging (structured JSON) | Correlation ID in every log line; PII redacted |
| Tracing | Cloud Trace | Distributed tracing across all 3 services + CRA calls |
| Alerting | Cloud Monitoring | SLO: 99.9% availability, p95 latency < 3s |
| Dashboards | Grafana (via Prometheus) | Service health, CRA availability, consent funnel |

### 7.2 Feature Flags

| Flag | Purpose | Default | Stories |
|------|---------|---------|---------|
| `credit-coach-enabled` | Master kill switch for Credit Coach | true | All |
| `cra-experian-enabled` | Enable/disable Experian integration | true | US-008 |
| `conversation-enabled` | Enable/disable conversational UI | true | US-014 |
| `multi-brand-rollout` | Control brand-by-brand rollout | lloyds-only | US-051 |

### 7.3 Audit Trail

All auditable events published to `audit.credit-coach` Pub/Sub topic:
- Consent granted/withdrawn
- CRA API called (request hash, response status, latency)
- Score retrieved/changed
- Admin config changes
- Data access by operations staff

---

## 8. Architecture Decision Records

### ADR-01: Microservice Boundaries
- **Status:** Accepted
- **Context:** Sprint 1 delivers consent, score retrieval, and conversation with different data sensitivity and scaling profiles.
- **Decision:** Three microservices: ConsentService, CreditScoreService, ConversationalAgentService.
- **Consequences:** Independent deployment/scaling. Inter-service overhead mitigated by gRPC + service mesh. AlertService deferred to S4.
- **Reference:** Microservices Architecture KB MS2 (service sizing), Enterprise Architecture KB §2.2 (Envoy)

### ADR-02: CRA Integration — Circuit Breaker + Cache
- **Status:** Accepted
- **Context:** External CRA APIs have variable latency/availability. NFR-01 requires <3s end-to-end.
- **Decision:** Synchronous REST with Resilience4j circuit breaker (2.5s timeout, 3 failures/30s → OPEN, 60s recovery). Fallback to Redis cache (24hr TTL).
- **Consequences:** Stale data served during outages (with `isStale: true` flag). Customer sees "Last updated [date]" instead of error.
- **Reference:** Enterprise Architecture KB §3.2 (circuit breakers), Microservices Architecture KB MS3 (design for failure)

### ADR-03: Score History in Cloud SQL (not BigQuery)
- **Status:** Accepted
- **Context:** Score history needs transactional writes (each refresh), point queries (customer dashboard), and time-range queries (trend chart).
- **Decision:** Cloud SQL (PostgreSQL) with partitioning by customer_id. BigQuery for analytics only (via CDC).
- **Consequences:** Simpler transactional model. Partition strategy handles 10M+ customers. Read replicas for history queries.
- **Reference:** Enterprise Architecture KB §1.2 (Cloud SQL), NFR-05 (10M+ scalability)

### ADR-04: Envoy Agent Registration
- **Status:** Accepted
- **Context:** Credit Coach must integrate with existing FA orchestrator as a specialist agent.
- **Decision:** Register via Envoy platform's standardised agent template. Intent classification at 0.85 confidence threshold.
- **Consequences:** Queries below threshold trigger clarification (not misroute). Reusable across Lloyds/Halifax/BoS brands.
- **Reference:** Enterprise Architecture KB §2.2 (Envoy platform), §2.3 (Agentic AI Architecture)

### ADR-05: Event-Driven Architecture (Pub/Sub)
- **Status:** Accepted
- **Context:** Score changes need to trigger downstream actions (alerts in S4, plan updates in S2) without coupling services.
- **Decision:** Pub/Sub topics: `consent.granted`, `consent.revoked`, `score.retrieved`, `score.changed`. Subscribers added in later sprints.
- **Consequences:** Loose coupling. New subscribers (AlertService, ImprovementPlanService) can be added without modifying publishers.
- **Reference:** Enterprise Architecture KB §1.2 (Pub/Sub), Microservices Architecture KB MS3 (smart endpoints, dumb pipes)

### ADR-06: Multi-Brand Support
- **Status:** Accepted
- **Context:** Credit Coach must serve Lloyds Bank, Halifax, and Bank of Scotland from shared infrastructure.
- **Decision:** Brand-agnostic backend (no brand logic in services). Brand-specific theming in mobile app shell via design system tokens. Feature flag controls rollout per brand.
- **Consequences:** Single deployment serves all brands. Brand context passed in JWT claims for analytics only.
- **Reference:** Enterprise Architecture KB §6 (Multi-Brand Architecture)

### ADR-07: Consent Management — Immutable Table
- **Status:** Accepted
- **Context:** GDPR Art. 7(1) requires ability to demonstrate consent was given. Audit trail must be tamper-proof.
- **Decision:** Append-only `credit_coach_consents` table. No UPDATE/DELETE operations. Withdrawal creates new row with `status: WITHDRAWN`. SHA-256 hash of consent text stored.
- **Consequences:** Complete audit trail. Slightly higher storage (no in-place updates). Consent history queryable for compliance.
- **Reference:** Domain KB PD14 (GDPR in Lending — consent records), UK GDPR Art. 7(1)

### ADR-08: CRA Data Domain Separation
- **Status:** Accepted
- **Context:** CRA data is Restricted classification. Must be isolated from general banking data per data protection principles.
- **Decision:** Separate Cloud SQL instance for CRA data (scores schema). Dedicated IAM service account. No cross-schema joins. Presentation layer combines data only at API response level.
- **Consequences:** Stronger isolation. Slightly more complex deployment. Clear data ownership boundary.
- **Reference:** Enterprise Architecture KB §5.1 (Security Layers), Product Vision §9.2 (Data Domains — "never co-mingled in storage")

### ADR-09: Data Classification — Score Values vs Metadata
- **Status:** Accepted
- **Context:** Not all columns in score tables are PII. Need clear boundary for encryption overhead.
- **Decision:** Encrypt: `score_value`, `ip_address`, `device_fingerprint`. Do NOT encrypt: `data_quality_score` (internal metric), foreign keys, plain English factor descriptions (not PII).
- **Consequences:** Reduced encryption overhead. Clear classification boundary. Factor descriptions cacheable in Redis.
- **Reference:** Enterprise Architecture KB §5.1 (data classification), Domain KB PD14 (GDPR data minimisation)

---

## 9. Non-Functional Requirements Mapping

| NFR | Target | Implementation |
|-----|--------|---------------|
| NFR-01 (Score retrieval <3s) | p95 < 3s | Redis cache (24hr TTL) + circuit breaker (2.5s timeout) |
| NFR-03 (99.9% uptime) | 99.9% | Min 2 replicas, health probes, auto-scaling, canary deployment |
| NFR-05 (10M+ customers) | 10M enrolled | Partitioned tables, Redis cluster, horizontal pod scaling |
| NFR-06 (AES-256 at rest) | All Restricted data | CMEK via Cloud KMS, application-level for PII fields |
| NFR-07 (TLS 1.3 in transit) | All connections | Istio mTLS internal, TLS 1.3 external |
| NFR-11 (WCAG 2.1 AA) | All screens | Design system compliance, axe-core in CI |
| NFR-24 (DPIA before launch) | Gate | DPIA completed by DPO before production deployment |
| NFR-25 (Data quality ≥95%) | Reject < 95% | Validation on CRA response; reject and retry if below threshold |

---

## 10. Mobile Architecture (BFF + Feature Modules)

### 10.1 BFF Layer (per MM5)

Per the Mobile Microfrontend BFF pattern (MM5), a **single Mobile BFF** sits between the native app and backend microservices. It aggregates, shapes, and optimises responses for mobile screens.

```
┌─────────────────────────────┐
│     Lloyds Mobile App       │
│  ┌───────────────────────┐  │
│  │  :feature:creditcoach │  │  (Credit Coach feature module)
│  └───────────┬───────────┘  │
│              │              │
│  ┌───────────┴───────────┐  │
│  │    :core:network      │  │  (Shared API client)
│  └───────────┬───────────┘  │
└──────────────┼──────────────┘
               │ HTTPS (TLS 1.3)
               ▼
┌──────────────────────────────┐
│     Apigee API Gateway       │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│     Credit Coach Mobile BFF  │  ← Screen-shaped responses
│     (Spring Boot on GKE)     │
│                              │
│  • Aggregates Consent +      │
│    Score + Factors into      │
│    single dashboard response │
│  • Strips internal fields    │
│  • Formats for mobile        │
│  • No business logic         │
└──────────┬───────────────────┘
           │ gRPC (internal)
     ┌─────┼─────────┐
     ▼     ▼         ▼
┌────────┐┌────────┐┌──────────────┐
│Consent ││ Score  ││Conversational│
│Service ││Service ││Agent Service │
└────────┘└────────┘└──────────────┘
```

**BFF Responsibilities (per MM5):**
- Aggregates microservice calls into screen-shaped responses (one screen = one BFF call)
- Auth token exchange (mobile OAuth → internal service tokens)
- Response shaping (strips `createdAt`, `updatedBy`, audit fields — mobile gets only display fields)
- Push notification device registration
- No business logic — orchestrates and shapes only

**BFF Endpoints (per MM6 — screen-oriented):**

| Endpoint | Aggregates From | Screen |
|----------|----------------|--------|
| `GET /mobile/v1/credit-coach/dashboard` | ScoreService (score + factors + change) | Dashboard |
| `GET /mobile/v1/credit-coach/consent-status` | ConsentService | Consent screen |
| `POST /mobile/v1/credit-coach/consent` | ConsentService | Consent capture |
| `POST /mobile/v1/credit-coach/conversation` | ConversationalAgentService | Chat UI |
| `GET /mobile/v1/credit-coach/factors` | ScoreService (full factor list) | Factors screen |
| `GET /mobile/v1/credit-coach/history` | ScoreService (history) | History screen |

**ADR-10: Mobile BFF for Credit Coach**
- **Status:** Accepted
- **Context:** MM5 mandates a single BFF per mobile app. Credit Coach needs screen-shaped responses aggregating consent + score + factors.
- **Decision:** Add Credit Coach routes to the existing Lloyds Mobile BFF. BFF calls backend services via gRPC internally.
- **Consequences:** Single deployment for all mobile API needs. Response shaping keeps mobile client thin. Internal service APIs remain clean.
- **Reference:** Mobile Microfrontend BFF KB MM5, MM6

### 10.2 Mobile Feature Module Structure (per MM3/MM4)

The Credit Coach is a **feature module** within the existing Lloyds mobile app:

**Android (Kotlin):**
```
:feature:creditcoach/
├── navigation/
│   └── CreditCoachNavigation.kt    // Only public API (FeatureNavigation impl)
├── ui/                              // All internal
│   ├── ConsentScreen.kt
│   ├── DashboardScreen.kt
│   ├── FactorsScreen.kt
│   ├── ConversationScreen.kt
│   └── SettingsScreen.kt
├── viewmodel/
│   ├── ConsentViewModel.kt
│   ├── DashboardViewModel.kt
│   └── ConversationViewModel.kt
├── data/
│   ├── CreditCoachRepository.kt
│   └── CreditCoachBffClient.kt     // Calls BFF, not services directly
└── di/
    └── CreditCoachModule.kt         // Hilt DI module
```

**Module dependency rules (per MM4 — enforced via Gradle/SPM):**
```
:app                   → :feature:creditcoach  (composes feature)
:feature:creditcoach   → :core:ui             (design system)
:feature:creditcoach   → :core:network        (BFF API client)
:feature:creditcoach   → :core:domain         (shared models)
:feature:creditcoach   → :core:auth           (auth state)
:feature:creditcoach   ✗ :feature:*           (NEVER — hard rule)
```

**Key rules (per MM3):**
- Feature module exposes ONLY `CreditCoachNavigation` (public entry point)
- All screens, ViewModels, repositories are `internal`
- No direct dependency on other feature modules
- Data flows through BFF only — never directly to backend microservices
- Uses `:core:ui` design system tokens (Lloyds Green, GT Ultra, spacing grid)

### 10.3 Single Responsibility Clarification (per MS1)

| Service | Single Responsibility | Does NOT Do |
|---------|----------------------|-------------|
| ConsentService | Consent lifecycle (grant, withdraw, query, audit) | Score retrieval, conversation |
| CreditScoreService | CRA integration, score storage, factor analysis, change explanation | Consent management, conversation |
| ConversationalAgentService | Intent classification, response generation, query routing | Score storage, consent, CRA calls |
| Mobile BFF | Response aggregation and shaping for mobile screens | Business logic, data storage, CRA calls |

### 10.4 API Versioning (per MS4)

- External (mobile → BFF): `/mobile/v1/credit-coach/*` — versioned at gateway
- Internal (BFF → services): `/api/v1/credit-coach/*` — versioned per service
- Breaking changes: new version path (`/v2/`), old version supported for 2 app release cycles
- Gateway enforces version routing (Apigee)
