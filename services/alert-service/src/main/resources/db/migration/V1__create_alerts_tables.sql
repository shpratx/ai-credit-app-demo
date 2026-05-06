-- V1__create_alerts_tables.sql
-- Description: Create alerts and alert_preferences tables
-- Author: credit-coach-team

CREATE TABLE alerts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version         BIGINT NOT NULL DEFAULT 0,
    customer_id     UUID NOT NULL,
    type            VARCHAR(30) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    message         VARCHAR(1000) NOT NULL,
    severity        VARCHAR(10) NOT NULL,
    status          VARCHAR(10) NOT NULL DEFAULT 'UNREAD',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_alerts_customer_status ON alerts(customer_id, status) WHERE is_deleted = FALSE;

CREATE TABLE alert_preferences (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version                 BIGINT NOT NULL DEFAULT 0,
    customer_id             UUID NOT NULL UNIQUE,
    utilisation_enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    utilisation_threshold   INTEGER NOT NULL DEFAULT 75,
    payment_enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    eligibility_enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    score_change_enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    all_disabled            BOOLEAN NOT NULL DEFAULT FALSE
);
