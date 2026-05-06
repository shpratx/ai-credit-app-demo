-- V1__create_simulations_table.sql
-- Description: Create simulations table for credit score simulation results
-- Author: simulation-service-team

CREATE TABLE simulations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID                     NOT NULL,
    scenario_type   VARCHAR(30)              NOT NULL,
    current_score   INTEGER                  NOT NULL,
    estimated_score INTEGER                  NOT NULL,
    point_impact    INTEGER                  NOT NULL,
    confidence      VARCHAR(10)              NOT NULL,
    factors_changed TEXT,
    disclaimer      TEXT,
    created_at      TIMESTAMPTZ              NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_scenario_type CHECK (scenario_type IN ('PAY_DEBT','CLOSE_ACCOUNT','OPEN_CREDIT','MISS_PAYMENT','REDUCE_UTILISATION')),
    CONSTRAINT chk_confidence CHECK (confidence IN ('HIGH','MEDIUM','LOW'))
);

CREATE INDEX idx_simulations_customer_id ON simulations(customer_id);
CREATE INDEX idx_simulations_created_at ON simulations(created_at DESC);
