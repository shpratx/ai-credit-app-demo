CREATE TABLE credit_scores (
    id                  UUID                     NOT NULL DEFAULT gen_random_uuid(),
    customer_id         UUID                     NOT NULL,
    provider            VARCHAR(20)              NOT NULL,
    score_value         BYTEA                    NOT NULL,
    max_score           INTEGER                  NOT NULL,
    band                VARCHAR(20)              NOT NULL,
    previous_score      BYTEA,
    change              INTEGER,
    change_direction    VARCHAR(10),
    retrieved_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    is_stale            BOOLEAN                  NOT NULL DEFAULT FALSE,
    data_quality_score  INTEGER                  NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_credit_scores PRIMARY KEY (id),
    CONSTRAINT chk_provider CHECK (provider IN ('EXPERIAN', 'EQUIFAX', 'TRANSUNION')),
    CONSTRAINT chk_band CHECK (band IN ('poor', 'fair', 'good', 'very_good', 'excellent')),
    CONSTRAINT chk_change_dir CHECK (change_direction IN ('up', 'down', 'unchanged') OR change_direction IS NULL),
    CONSTRAINT chk_quality CHECK (data_quality_score BETWEEN 0 AND 100)
);

CREATE INDEX idx_scores_customer_retrieved ON credit_scores (customer_id, retrieved_at DESC);
CREATE INDEX idx_scores_customer_provider ON credit_scores (customer_id, provider);

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
    CONSTRAINT chk_factor_direction CHECK (direction IN ('positive', 'negative'))
);

CREATE INDEX idx_factors_score_id ON credit_score_factors (score_id);

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

CREATE INDEX idx_audit_customer_timestamp ON cra_api_audit_log (customer_id, created_at DESC);
CREATE INDEX idx_audit_correlation ON cra_api_audit_log (correlation_id);
