# Application Baseline — AI Credit Coach
### kb-L3-ai-credit-coach-baseline v1.0.0 (Sprint 1)

---

## BL1: Product Inventory

| Product | Type | Status | Sprint | Description |
|---------|------|--------|--------|-------------|
| AI Credit Coach | Credit Monitoring & Coaching | Active (S1) | S1–S4 | Specialist agent on Envoy platform providing credit score monitoring, personalised improvement plans, what-if simulation, pre-approved offers, and proactive alerts within the existing AI Financial Assistant |

**Sprint 1 Scope:** Credit score monitoring and dashboard only. No lending products, no offers, no simulation.

**Revenue Model:** Free to customers (competitive parity with ClearScore/Credit Karma). Revenue via pre-approved offer conversion (Sprint 3+).

**Brands Served:** Lloyds Bank, Halifax, Bank of Scotland (shared backend, brand-specific theming).

---

## BL2: Feature Inventory

| Feature ID | Title | Epic | Status | Stories | Points |
|-----------|-------|------|--------|---------|--------|
| F-01.1 | Envoy Agent Registration & Orchestrator Integration | EP-01 | Planned (S1) | US-001, US-002 | 8 |
| F-01.2 | Explicit CRA Consent Capture & Management | EP-01 | Planned (S1) | US-003, US-004 | 8 |
| F-01.3 | Credit Coach Design System Components | EP-01 | Planned (S1) | US-005, US-051 | 11 |
| F-01.4 | CRA Data Domain & Storage Infrastructure | EP-01 | Planned (S1) | US-006, US-007 | 8 |
| F-02.1 | CRA Score Retrieval Service | EP-02 | Planned (S1) | US-008, US-052, US-057 | 11 |
| F-02.2 | Credit Score Display & Band Classification | EP-02 | Planned (S1) | US-009, US-010 | 5 |
| F-02.3 | Score Factors in Plain English | EP-02 | Planned (S1) | US-011, US-053 | 5 |
| F-02.4 | Score Change Explanation | EP-02 | Planned (S1) | US-012 | 3 |
| F-02.5 | Score History Storage & Retrieval | EP-02 | Planned (S1) | US-013 | 3 |
| F-02.6 | Conversational Credit Queries | EP-02 | Planned (S1) | US-014, US-015 | 8 |
| F-02.7 | Score Refresh Scheduling & Configuration | EP-02 | Planned (S1) | US-016, US-017 | 7 |

**Total Sprint 1:** 11 features | 20 stories | 77 story points

---

## BL3: Screen Inventory

| Route | Screen Name | Platform | Stories | Status | Wireframe |
|-------|-------------|----------|---------|--------|-----------|
| `/credit-coach/consent` | CRA Consent Capture | iOS / Android | US-003, US-004 | Designed | Screen 1 |
| `/credit-coach/dashboard` | Credit Score Dashboard | iOS / Android | US-009, US-010, US-011, US-012 | Designed | Screen 2 |
| `/credit-coach/factors` | Score Factors (Full List) | iOS / Android | US-011, US-053 | Designed | Screen 3 |
| `/credit-coach/conversation` | Conversational Credit UI | iOS / Android (within FA) | US-014, US-015 | Designed | Screen 4 |
| `/credit-coach/settings/consent` | Consent Management | iOS / Android | US-004, US-016, US-017 | Designed | Screen 5 |
| `/credit-coach/dashboard` (error) | CRA Unavailable State | iOS / Android | US-057 | Designed | Screen 6 |
| `/credit-coach/dashboard` (loading) | Loading Skeleton | iOS / Android | US-008 | Designed | Screen 7 |
| `/admin/credit-coach/refresh-config` | Refresh Frequency Admin | Internal Web (React) | US-017 | Planned |  |

### Design Artifacts

| Artifact | Path | Status |
|----------|------|--------|
| S1 Wireframes (HTML) | `/ui/credit-coach-wireframes.html` | ✅ Complete (9 screens) |
| S2 Wireframes (HTML) | `/ui/credit-coach-wireframes-s2.html` | ✅ Complete (7 screens) |
| S3 Wireframes (HTML) | `/ui/credit-coach-wireframes-s3.html` | ✅ Complete (8 screens) |
| S4 Wireframes (HTML) | `/ui/credit-coach-wireframes-s4.html` | ✅ Complete (8 screens) |
| Design System | `/ui/lloyds-design-system.md` | ✅ Reference (Lloyds official) |

**Accessibility:** All screens WCAG 2.1 AA compliant, VoiceOver (iOS) and TalkBack (Android) compatible, colour-blind safe palettes, 4.5:1 text contrast, 3:1 UI component contrast.

---

## BL4: API Inventory

| Endpoint | Method | Handler | Service | Status |
|----------|--------|---------|---------|--------|
| `/api/v1/credit-coach/consent` | POST | ConsentGrantCommandHandler | ConsentService | Planned |
| `/api/v1/credit-coach/consent/{customer_id}` | GET | ConsentQueryHandler | ConsentService | Planned |
| `/api/v1/credit-coach/consent/{consent_id}/withdraw` | POST | ConsentWithdrawCommandHandler | ConsentService | Planned |
| `/api/v1/credit-coach/score/{customer_id}` | GET | ScoreQueryHandler | CreditScoreService | Planned |
| `/api/v1/credit-coach/score/{customer_id}/refresh` | POST | ScoreRetrievalCommandHandler | CreditScoreService | Planned |
| `/api/v1/credit-coach/score/{customer_id}/history` | GET | ScoreHistoryQueryHandler | CreditScoreService | Planned |
| `/api/v1/credit-coach/score/{customer_id}/change-explanation` | GET | ScoreChangeExplanationQueryHandler | CreditScoreService | Planned |
| `/api/v1/credit-coach/conversation` | POST | ConversationalQueryHandler | ConversationalAgentService | Planned |
| `/api/v1/credit-coach/admin/refresh-schedule` | PUT | RefreshConfigCommandHandler | CreditScoreService | Planned |

### Sprint 2 APIs (EP-03, EP-04)

| Endpoint | Method | Handler | Service | Status |
|----------|--------|---------|---------|--------|
| `/api/v1/credit-coach/improvement-plans/{customerId}` | GET | GetImprovementPlanQueryHandler | ImprovementPlanService | Planned (S2) |
| `/api/v1/credit-coach/improvement-plans/{customerId}` | POST | RefreshPlanCommandHandler | ImprovementPlanService | Planned (S2) |
| `/api/v1/credit-coach/improvement-plans/{customerId}/actions/{actionId}/complete` | POST | CompleteActionCommandHandler | ImprovementPlanService | Planned (S2) |
| `/api/v1/credit-coach/improvement-plans/{customerId}/actions` | GET | GetActionHistoryQueryHandler | ImprovementPlanService | Planned (S2) |
| `/api/v1/credit-coach/milestones/{customerId}` | GET | GetMilestonesQueryHandler | ImprovementPlanService | Planned (S2) |
| `/api/v1/credit-coach/spending-impact/{customerId}` | GET | GetSpendingImpactQueryHandler | ImprovementPlanService | Planned (S2) |
| `/api/v1/credit-coach/score-history/{customerId}/trend` | GET | GetScoreTrendQueryHandler | CreditScoreService | Planned (S2) |
| `/api/v1/credit-coach/debt-overview/{customerId}` | GET | GetDebtOverviewQueryHandler | DebtVisibilityService | Planned (S2) |

### API Specification Artifacts

| Artifact | Service | Path | Endpoints | Status |
|----------|---------|------|-----------|--------|
| Consent Service API | ConsentService | `/api/consent-service-api.yaml` | 4 | ✅ Complete |
| Credit Score Service API | CreditScoreService | `/api/credit-score-service-api.yaml` | 8 | ✅ Complete |
| Conversational Agent Service API | ConversationalAgentService | `/api/conversational-agent-service-api.yaml` | 2 | ✅ Complete |
| S2 API Spec (OpenAPI 3.0) | ImprovementPlanService + DebtVisibilityService | `/api/credit-coach-s2-api-spec.yaml` | S2 | Planned |

**API Standards:**
- RESTful, versioned (`/api/v1/credit-coach/...`)
- JSON request/response
- Standard error format: `{error: {code, message, details}}`
- Pagination: cursor-based for lists
- Auth: OAuth 2.0 Bearer token (existing Lloyds auth)
- Gateway: Apigee

---

## BL5: Data Model

### Table: `credit_coach_consents`
**Purpose:** Immutable audit trail of customer consent grants and withdrawals. Retained indefinitely.

| Column | Type | Nullable | Encrypted | Notes |
|--------|------|----------|-----------|-------|
| id | UUID | No | No | PK |
| customer_id | UUID | No | No | FK to customer |
| cra_provider | VARCHAR(20) | No | No | EXPERIAN, EQUIFAX, TRANSUNION |
| status | VARCHAR(20) | No | No | GRANTED, WITHDRAWN |
| consent_text_version | VARCHAR(10) | No | No | e.g., "1.0", "1.1" |
| consent_text_hash | VARCHAR(64) | No | No | SHA-256 of consent text |
| granted_at | TIMESTAMP TZ | Yes | No | NULL if never granted |
| withdrawn_at | TIMESTAMP TZ | Yes | No | NULL if active |
| channel | VARCHAR(10) | No | No | IOS, ANDROID |
| ip_address | VARCHAR(45) | No | Yes | AES-256 encrypted |
| device_fingerprint | VARCHAR(255) | No | Yes | AES-256 encrypted |
| created_at | TIMESTAMP TZ | No | No | Row creation time |

**Indexes:** `(customer_id, cra_provider)`, `(status, cra_provider)`
**Retention:** Indefinite (NFR-10)
**Classification:** Confidential

---

### Table: `credit_scores`
**Purpose:** Encrypted score history. Retained 6 years per FCA SYSC 9.1.1R.

| Column | Type | Nullable | Encrypted | Notes |
|--------|------|----------|-----------|-------|
| id | UUID | No | No | PK |
| customer_id | UUID | No | No | Partition key |
| score_value | INTEGER | No | Yes | AES-256 (pgcrypto) |
| band | VARCHAR(20) | No | No | POOR, FAIR, GOOD, EXCELLENT |
| cra_source | VARCHAR(20) | No | No | EXPERIAN, EQUIFAX, TRANSUNION |
| scale_min | INTEGER | No | No | e.g., 0 |
| scale_max | INTEGER | No | No | e.g., 999 (Experian) |
| data_quality_score | DECIMAL(5,2) | No | No | Must be >= 95.00 |
| retrieved_at | TIMESTAMP TZ | No | No | When CRA returned data |
| refresh_id | UUID | No | No | Links to refresh batch |
| created_at | TIMESTAMP TZ | No | No | Row creation time |

**Indexes:** `(customer_id, retrieved_at DESC)`, `(customer_id, cra_source)`
**Retention:** 6 years post-service-closure; archive to Coldline after 2 years active
**Classification:** Restricted

---

### Table: `credit_score_factors`
**Purpose:** Factor breakdown per score retrieval.

| Column | Type | Nullable | Encrypted | Notes |
|--------|------|----------|-----------|-------|
| id | UUID | No | No | PK |
| score_id | UUID | No | No | FK to credit_scores |
| factor_code | VARCHAR(50) | No | No | CRA factor code |
| category | VARCHAR(50) | No | No | PAYMENT_HISTORY, UTILISATION, etc. |
| direction | VARCHAR(10) | No | No | POSITIVE, NEGATIVE |
| impact_magnitude | INTEGER | No | No | 1-10 scale |
| raw_value | VARCHAR(255) | Yes | Yes | AES-256 encrypted |
| plain_english_description | TEXT | Yes | No | Translated description |
| created_at | TIMESTAMP TZ | No | No | Row creation time |

**Indexes:** `(score_id)`, `(score_id, direction, impact_magnitude DESC)`
**Retention:** Same as parent credit_scores (6 years)
**Classification:** Restricted

---

### Table: `score_refresh_schedules`
**Purpose:** Per-customer refresh scheduling with retry tracking.

| Column | Type | Nullable | Encrypted | Notes |
|--------|------|----------|-----------|-------|
| id | UUID | No | No | PK |
| customer_id | UUID | No | No | One per customer per CRA |
| cra_provider | VARCHAR(20) | No | No | EXPERIAN |
| frequency_days | INTEGER | No | No | Min 7, max 30 |
| next_refresh_at | TIMESTAMP TZ | No | No | Next scheduled refresh |
| last_refresh_at | TIMESTAMP TZ | Yes | No | Last successful refresh |
| last_refresh_status | VARCHAR(20) | Yes | No | SUCCESS, FAILED, PENDING |
| retry_count | INTEGER | No | No | 0-3, reset on success |
| is_active | BOOLEAN | No | No | FALSE on consent withdrawal |
| created_at | TIMESTAMP TZ | No | No | Row creation time |
| updated_at | TIMESTAMP TZ | No | No | Last modification |

**Indexes:** `(next_refresh_at, is_active)` for batch scheduler, `(customer_id, cra_provider)` UNIQUE
**Retention:** Lifetime of enrolment + 90 days
**Classification:** Internal

---

### Table: `cra_api_audit_log`
**Purpose:** Audit trail of all CRA API interactions.

| Column | Type | Nullable | Encrypted | Notes |
|--------|------|----------|-----------|-------|
| id | UUID | No | No | PK |
| customer_id | UUID | No | No | Who the call was for |
| cra_provider | VARCHAR(20) | No | No | EXPERIAN |
| request_type | VARCHAR(20) | No | No | SOFT_SEARCH, SCORE_REFRESH |
| request_timestamp | TIMESTAMP TZ | No | No | When call was made |
| response_status | VARCHAR(20) | No | No | SUCCESS, TIMEOUT, ERROR |
| response_time_ms | INTEGER | No | No | Latency in milliseconds |
| data_quality_score | DECIMAL(5,2) | Yes | No | NULL on failure |
| error_code | VARCHAR(50) | Yes | No | CRA error code if failed |
| circuit_breaker_state | VARCHAR(20) | No | No | CLOSED, OPEN, HALF_OPEN |
| created_at | TIMESTAMP TZ | No | No | Row creation time |

**Indexes:** `(customer_id, request_timestamp DESC)`, `(response_status, created_at)`
**Retention:** 6 years (FCA SYSC 9.1.1R)
**Classification:** Internal

---

## BL6: Integration Inventory

| System | Protocol | Purpose | Circuit Breaker | Cache | Stories |
|--------|----------|---------|-----------------|-------|---------|
| Experian CRA API | REST (HTTPS/TLS 1.3) | Soft search score retrieval (score, band, factors) | 3 failures/30s → OPEN; 60s recovery; 2.5s timeout | Memorystore Redis (24hr TTL) | US-008, US-016, US-052, US-057 |
| Envoy Orchestrator | gRPC (mTLS) | Agent registration, intent routing, conversation context, human handoff | 5 failures/60s → OPEN; 30s recovery | N/A | US-001, US-002, US-014, US-015 |
| Google Cloud Pub/Sub | gRPC (GCP native) | Async events: score.retrieved, score.changed, consent.granted, consent.revoked | N/A (managed service) | N/A | US-007, US-008, US-016 |
| Memorystore (Redis) | Redis protocol (TLS) | Score cache for dashboard loads and CRA unavailability fallback | N/A (cache miss → Cloud SQL) | Self (24hr TTL) | US-008, US-009, US-057 |
| Vertex AI | gRPC (Vertex AI SDK) | Intent classification model for credit query routing | 2.5s timeout; fallback to rule-based matching | N/A | US-002, US-014 |
| Cloud SQL (PostgreSQL) | PostgreSQL wire protocol (TLS) | Transactional data store for scores, consents, schedules, audit logs | Connection pool (PgBouncer); retry on transient failures | N/A | US-006, US-013 |
| BigQuery | BigQuery API | Analytics pipeline destination (via Dataflow from Pub/Sub) | N/A (async pipeline) | N/A | US-007 |
| Dataflow | GCP native | Stream processing: Pub/Sub events → BigQuery | N/A (managed service) | N/A | US-007 |

### Pub/Sub Topic Design

| Topic | Publisher | Subscribers | Message Schema |
|-------|-----------|-------------|----------------|
| `credit-coach.score.retrieved` | CreditScoreService | ScoreHistoryService, CacheUpdateService, AnalyticsPipeline | `{customer_id, score_value, band, cra_source, retrieved_at, refresh_id}` |
| `credit-coach.score.changed` | ScoreChangeDetectionService | ChangeExplanationService, AnalyticsPipeline | `{customer_id, previous_score, new_score, delta, cra_source, changed_at}` |
| `credit-coach.consent.granted` | ConsentService | ScoreRetrievalService, RefreshSchedulerService | `{customer_id, cra_provider, consent_id, granted_at}` |
| `credit-coach.consent.revoked` | ConsentService | RefreshSchedulerService, DataDeletionService, CacheInvalidationService | `{customer_id, cra_provider, consent_id, withdrawn_at}` |

---

## BL6a: Source Control & CI/CD

| Aspect | Tool | Notes |
|--------|------|-------|
| Source control | GitHub | Monorepo per service. Branch protection on main. |
| CI | Cloud Build | Triggered on PR merge. Runs: lint, test, SAST, container build. |
| CD | Cloud Deploy | Canary deployment to GKE. Auto-promote after 30min if no alerts. |
| IaC | Terraform | All infrastructure defined in `/infra/terraform/`. State in GCS. |
| Container registry | Artifact Registry | Vulnerability scanning enabled. |
| Secrets | Secret Manager | All API keys, CRA credentials, encryption keys. Never in code. |

## BL6b: Compliance Gates (Sprint 1)

| Gate | Owner | Status | Blocker For |
|------|-------|--------|-------------|
| DPIA completion | Data Protection Office | Required before production deployment | Go-live |
| Security Architecture Review | InfoSec | Required before CRA integration | CRA API calls |
| Consent mechanism legal review | Legal | Required before consent screen | Consent feature |
| CRA partnership agreement | Commercial & Legal | Required before CRA API access | Score retrieval |
| Accessibility audit | UX/Accessibility team | Required before go-live | Go-live |

## BL7: Known Limitations (Sprint 1)

| Limitation | Deferred To | Rationale |
|-----------|-------------|-----------|
| Multi-bureau comparison (Equifax, TransUnion) | Sprint 4 (EP-08) | Single CRA partnership (Experian) sufficient for MVP; additional CRAs require separate commercial agreements |
| Score trend visualisation charts | Sprint 2 (EP-04) | Score history storage begins in S1; visual rendering in S2 once sufficient data accumulated |
| Personalised improvement action plans | Sprint 2 (EP-03) | Requires Vertex AI recommendation model training; S1 accumulates training data |
| Credit score simulator (what-if) | Sprint 3 (EP-05) | Requires simulation model development and PRA SS1/23 validation |
| Pre-approved offers engine | Sprint 3 (EP-06) | Requires FCA regulatory review (CON-07); affordability integration |
| Proactive credit health alerts | Sprint 4 (EP-07) | AlertService deferred; Pub/Sub infrastructure ready in S1 for S4 subscribers |
| Vulnerability detection & offer suppression | Sprint 4 (EP-08) | Complex detection logic; S1 focuses on core monitoring |
| Right to erasure (full deletion workflow) | Sprint 4 (EP-08) | Consent withdrawal stops retrieval in S1; full data purge in S4 |
| DSAR export | Sprint 4 (EP-08) | Data accumulation begins S1; export tooling built when data model stabilises |
| Human escalation path | Sprint 4 (EP-08) | Requires integration with existing Lloyds contact centre systems |
| Breathing Space (Debt Respite Scheme) compliance | Sprint 4 (EP-08) | Requires integration with Insolvency Service portal or internal collections system notification; auto-freeze logic complex |
| Cross-lender debt visibility | Sprint 2 (EP-04) | Requires additional CRA data fields beyond score/factors |
| Multi-brand theming validation | Sprint 1 (included) | US-051 validates theming; full brand rollout is operational, not code |
| Offline mode / cached dashboard | Future | Not specified in requirements; requires product decision |
| Marketing consent (separate from CRA consent) | Out of scope | Explicitly excluded from Credit Coach scope |

---

## BL8: Architecture Decisions

### Architecture Documents

| Document | Path | Status |
|----------|------|--------|
| Solution Architecture | `/design-docs/solution-architecture.md` | ✅ Complete |
| Integration Architecture | `/design-docs/integration-architecture.md` | ✅ Complete |
| Low-Level Design (LLD) | `/design-docs/lld.md` | ✅ Complete (2531 lines) |

### Test Artifacts

| Artifact | Path | Status |
|----------|------|--------|
| S1 Integration Test Pack (JSON) | `/tests/credit-coach-s1-test-pack.json` | ✅ Complete |
| BDD Feature Files (8) | `/tests/bdd/features/EP-01/`, `/tests/bdd/features/EP-02/` | ✅ Complete |
| Step Definitions (3) | `/tests/bdd/steps/` | ✅ Complete |
| Page Objects (4) | `/tests/bdd/pages/` | ✅ Complete |

**Test Coverage Summary (S1):**
- 66 test scenarios (24 Functional, 12 Negative, 11 Accessibility, 8 Boundary, 7 Security, 4 Integration)
- 20 manual test cases (Critical/High paths)
- 8 automated feature files (Playwright + Cucumber)
- 19/19 user-facing stories covered
- All 66 scenarios tagged `@regression` for ongoing regression pack
- 12 scenarios tagged `@smoke` for quick validation

### Source Code

| Service | Path | Java Files | Tests | Status |
|---------|------|-----------|-------|--------|
| ConsentService | `/services/consent-service/` | 18 | 3 (2 unit + 1 contract) | ✅ Implemented |
| CreditScoreService | `/services/credit-score-service/` | 49 | 4 (3 unit + 1 integration) | ✅ Implemented (S1 + S2 extensions) |
| ConversationalAgentService | `/services/conversational-agent-service/` | 15 | 1 (unit) | ✅ Implemented |
| ImprovementPlanService | `/services/improvement-plan-service/` | 34 | 3 (unit) | ✅ Implemented (S2) |
| SimulationService | `/services/simulation-service/` | 22 | 3 (unit) | ✅ Implemented (S3) |
| OfferService | `/services/offer-service/` | 30 | 4 (unit) | ✅ Implemented (S3) |
| AlertService | `/services/alert-service/` | 29 | 3 (unit) | ✅ Implemented (S4) |
| ComplianceService | `/services/compliance-service/` | 33 | 5 (unit) | ✅ Implemented (S4) |

### Frontend (React Web)

| App | Path | Source Files | Tests | Status |
|-----|------|-------------|-------|--------|
| Credit Coach Web | `/services/credit-coach-web/` | 55 TSX/TS | 45 (10 test files, Vitest) | ✅ Implemented (S1–S4) |

**Frontend stack:** React 18.3, TypeScript 5.5, Vite 5.4, TanStack React Query 5, Zustand 4.5, Tailwind CSS 3.4 (Lloyds tokens), Vitest + Testing Library

**Pages:** 17 (ConsentPage, DashboardPage, FactorsPage, SettingsPage, ImprovementPlanPage, SpendingImpactPage, ScoreHistoryPage, DebtOverviewPage, SimulatorPage, OffersPage, SecciPage, AlertsPage, AlertPreferencesPage, DeleteDataPage, DecisionExplanationPage, DsarExportPage, MultiBureauPage)

**Total: 230 Java files + 55 TypeScript files = 285 source files across 9 services**

### ADR-01: Microservice Boundaries
**Context:** Sprint 1 delivers consent, score retrieval, display, conversation, and scheduling with different data sensitivity and scaling profiles.
**Decision:** Three microservices: ConsentService, CreditScoreService, ConversationalAgentService. AlertService deferred to S4.
**Consequences:** Independent deployment and scaling. Inter-service overhead mitigated by gRPC + service mesh.

### ADR-02: CRA Integration Pattern
**Context:** External CRA APIs have variable latency/availability. NFR-01 requires <3s end-to-end for 10M+ customers.
**Decision:** Synchronous REST with Resilience4j circuit breaker (2.5s timeout, 3 failures/30s → OPEN, 60s recovery). Fallback to Redis cache (24hr TTL) showing stale data with timestamp.
**Consequences:** Customers always see a score. Worst-case staleness: 24hrs + refresh interval.

### ADR-03: Score History Data Model
**Context:** 6-year retention, encrypted, queryable by date range. Cloud SQL vs BigQuery.
**Decision:** Cloud SQL (PostgreSQL) for transactional storage with customer_id partitioning. BigQuery for analytics only (via Dataflow pipeline). Archive to Coldline after 2 years.
**Consequences:** ACID transactions, low-latency queries (<1s), row-level encryption. Analytics isolated from transactional load.

### ADR-04: Envoy Agent Registration
**Context:** Credit Coach must integrate with existing FA agentic orchestrator.
**Decision:** Register via Envoy standardised agent template. Intent classification via Vertex AI (>0.85 confidence for direct routing, 0.6-0.85 for clarification). CRA data not persisted in Envoy memory (session-scoped only).
**Consequences:** Leverages existing infrastructure. Multi-brand deployment automatic. Requires intent model training data.

### ADR-05: Event-Driven Architecture
**Context:** Score retrieval triggers multiple downstream actions; tight coupling creates fragility.
**Decision:** Four Pub/Sub topics (score.retrieved, score.changed, consent.granted, consent.revoked). Dead-letter topics for failed processing.
**Consequences:** Loose coupling, independent scaling. New subscribers (AlertService S4) added without publisher changes.

### ADR-06: Multi-Brand Support
**Context:** Three brands (Lloyds, Halifax, BoS) from shared infrastructure.
**Decision:** Brand-agnostic backend. Brand context via X-Brand-Id header. Frontend uses brand-specific theme tokens. Accessibility validated per-brand in CI.
**Consequences:** Single deployment serves all brands. New brands = new theme tokens only.

### ADR-07: Consent Management
**Context:** GDPR Art. 6 requires demonstrable consent. Indefinite retention. Re-consent on text version changes.
**Decision:** Dedicated table with append-only semantics. No UPDATE/DELETE. Withdrawal creates new row with WITHDRAWN status. ConsentService has exclusive write access.
**Consequences:** Immutable audit trail. Table grows indefinitely (acceptable — small records).

### ADR-08: CRA Data Encryption and Domain Separation
**Context:** FR-42 mandates CRA and bank data never co-mingled in storage. Restricted classification requires AES-256.
**Decision:** Separate Cloud SQL instance with CMEK (Cloud KMS). Separate GCP project with dedicated service accounts. IAM denies cross-domain access. Column-level encryption for sensitive fields. Data combined only in presentation layer at runtime.
**Consequences:** Strong isolation. Key rotation without downtime. Additional operational overhead justified by regulatory requirement.

---

## Appendix: Story-to-Component Traceability

| Story | API | Handler | Table | Integration | Screen |
|-------|-----|---------|-------|-------------|--------|
| US-001 | — | AgentRegistrationCommandHandler | — | Envoy Orchestrator | — |
| US-002 | — | AgentRegistrationCommandHandler | — | Envoy Orchestrator, Vertex AI | — |
| US-003 | POST /consent | ConsentGrantCommandHandler | credit_coach_consents | — | /consent |
| US-004 | POST /withdraw | ConsentWithdrawCommandHandler | credit_coach_consents | — | /settings/consent |
| US-005 | — | — | — | — | All screens (component library) |
| US-006 | — | — | All tables (infrastructure) | Cloud SQL, Cloud KMS | — |
| US-007 | — | ScoreRetrievedEventHandler | — | Pub/Sub, Dataflow, BigQuery | — |
| US-008 | POST /refresh, GET /score | ScoreRetrievalCommandHandler | credit_scores, cra_api_audit_log | Experian CRA, Memorystore | — |
| US-009 | GET /score | ScoreQueryHandler | credit_scores | Memorystore | /dashboard |
| US-010 | GET /score | ScoreQueryHandler | — | — | /dashboard |
| US-011 | GET /score | FactorTranslationQueryHandler | credit_score_factors | — | /factors |
| US-012 | GET /change-explanation | ScoreChangeExplanationQueryHandler | credit_scores, credit_score_factors | — | /score-change |
| US-013 | GET /history | ScoreHistoryQueryHandler | credit_scores | — | — |
| US-014 | POST /conversation | ConversationalQueryHandler | — | Envoy Orchestrator | /conversation |
| US-015 | POST /conversation | ConversationalQueryHandler | — | Envoy Orchestrator, Vertex AI | /conversation |
| US-016 | — | ScoreRefreshSchedulerHandler | score_refresh_schedules, cra_api_audit_log | Experian CRA, Pub/Sub | — |
| US-017 | PUT /admin/refresh-schedule | RefreshConfigCommandHandler | score_refresh_schedules | — | /admin/refresh-config |
| US-051 | — | — | — | — | All screens (theme validation) |
| US-052 | — | DataQualityValidationHandler | cra_api_audit_log | — | — |
| US-053 | GET /score | FactorTranslationQueryHandler | credit_score_factors | — | /factors |
| US-057 | GET /score | ScoreQueryHandler | — | Memorystore, Experian CRA | /dashboard |

---

## Tech Stack Summary (Sprint 1)

| Layer | Technology | Service |
|-------|-----------|---------|
| Backend | Spring Boot (Java 17+) | GKE (min 2 replicas, auto-scaling) |
| Frontend | Swift (iOS) / Kotlin (Android) | Within existing mobile app shell |
| AI/ML | Vertex AI (intent classification) | Vertex AI Endpoints |
| Database | Cloud SQL (PostgreSQL 15) | Separate instance for CRA domain |
| Cache | Memorystore (Redis 7) | 24hr TTL for score data |
| Messaging | Pub/Sub | 4 topics (score + consent lifecycle) |
| API Gateway | Apigee | Rate limiting, auth, versioning |
| Monitoring | Cloud Monitoring, Prometheus | 99.9% SLO alerting |
| IaC | Terraform | All infrastructure as code |
| CI/CD | Cloud Build → Cloud Deploy → GKE | Automated with accessibility checks |
| Encryption | Cloud KMS (CMEK) + pgcrypto | AES-256 at rest, TLS 1.3 in transit |
| Service Mesh | Cloud Service Mesh (Istio) | mTLS between services |
