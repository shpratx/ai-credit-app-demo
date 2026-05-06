-- V1__create_compliance_tables.sql
-- Description: Create DSAR exports and breathing spaces tables
-- Author: credit-coach-team

CREATE TABLE dsar_exports (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version         BIGINT NOT NULL DEFAULT 0,
    customer_id     UUID NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    requested_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMPTZ,
    download_url    VARCHAR(500)
);

CREATE INDEX idx_dsar_exports_customer ON dsar_exports(customer_id);

CREATE TABLE breathing_spaces (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version         BIGINT NOT NULL DEFAULT 0,
    customer_id     UUID NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    notified_at     TIMESTAMPTZ
);

CREATE INDEX idx_breathing_spaces_customer_active ON breathing_spaces(customer_id, status) WHERE status = 'ACTIVE';
