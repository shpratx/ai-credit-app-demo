-- V2__add_debt_accounts_table.sql
-- Description: Cache table for CRA debt data (Sprint 2)
-- Owner: CreditScoreService

CREATE TABLE debt_accounts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID                     NOT NULL,
    account_type    VARCHAR(30)              NOT NULL,
    provider        VARCHAR(100)             NOT NULL,
    balance         NUMERIC(18,2)            NOT NULL,
    credit_limit    NUMERIC(18,2),
    status          VARCHAR(20)              NOT NULL DEFAULT 'active',
    retrieved_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_debt_account_type CHECK (account_type IN ('credit_card', 'personal_loan', 'mortgage', 'overdraft', 'store_card', 'other')),
    CONSTRAINT chk_debt_status CHECK (status IN ('active', 'closed', 'defaulted', 'settled'))
);

CREATE INDEX idx_debt_accounts_customer ON debt_accounts (customer_id, retrieved_at DESC);
CREATE INDEX idx_debt_accounts_type ON debt_accounts (customer_id, account_type);
