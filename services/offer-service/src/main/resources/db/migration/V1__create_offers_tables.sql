-- V1__create_offers_tables.sql
-- Description: Create pre-approved offers and audit tables
-- Author: offer-service-team

CREATE TABLE pre_approved_offers (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id              UUID                     NOT NULL,
    product_id               UUID                     NOT NULL,
    amount                   NUMERIC(18,2)            NOT NULL,
    rate                     NUMERIC(5,4)             NOT NULL,
    apr                      NUMERIC(5,2)             NOT NULL,
    term                     INTEGER                  NOT NULL,
    monthly_payment          NUMERIC(18,2)            NOT NULL,
    total_payable            NUMERIC(18,2)            NOT NULL,
    total_charge_for_credit  NUMERIC(18,2)            NOT NULL,
    status                   VARCHAR(20)              NOT NULL DEFAULT 'AVAILABLE',
    valid_until              TIMESTAMPTZ              NOT NULL,
    created_at               TIMESTAMPTZ              NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_offer_status CHECK (status IN ('AVAILABLE','ACCEPTED','EXPIRED','SUPPRESSED')),
    CONSTRAINT chk_offer_amount CHECK (amount > 0),
    CONSTRAINT chk_offer_term CHECK (term > 0)
);

CREATE INDEX idx_offers_customer_status ON pre_approved_offers(customer_id, status);
CREATE INDEX idx_offers_valid_until ON pre_approved_offers(valid_until) WHERE status = 'AVAILABLE';

CREATE TABLE offer_audit_entries (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    offer_id     UUID                     NOT NULL,
    customer_id  UUID                     NOT NULL,
    action       VARCHAR(20)              NOT NULL,
    timestamp    TIMESTAMPTZ              NOT NULL DEFAULT NOW(),
    reason       TEXT,

    CONSTRAINT chk_audit_action CHECK (action IN ('PRESENTED','VIEWED','ACCEPTED','DECLINED','EXPIRED','SUPPRESSED'))
);

CREATE INDEX idx_audit_customer ON offer_audit_entries(customer_id);
CREATE INDEX idx_audit_offer ON offer_audit_entries(offer_id);
CREATE INDEX idx_audit_timestamp ON offer_audit_entries(timestamp DESC);
