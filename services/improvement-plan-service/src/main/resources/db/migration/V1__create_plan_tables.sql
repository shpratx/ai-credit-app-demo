-- V1__create_plan_tables.sql
-- Description: Create improvement plan, actions, and milestones tables
-- Owner: improvement-plan-service

CREATE TABLE improvement_plans (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id         UUID                     NOT NULL,
    status              VARCHAR(30)              NOT NULL,
    confidence          VARCHAR(10),
    score_at_generation INTEGER,
    generated_at        TIMESTAMP WITH TIME ZONE,
    disclaimer          VARCHAR(500),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_plan_status CHECK (status IN ('ACTIVE', 'GENERATING', 'EXPIRED', 'NO_ACTIONS_NEEDED')),
    CONSTRAINT chk_plan_confidence CHECK (confidence IN ('HIGH', 'MEDIUM', 'LOW') OR confidence IS NULL)
);

CREATE INDEX idx_plans_customer_status ON improvement_plans (customer_id, status);
CREATE INDEX idx_plans_generated_at ON improvement_plans (generated_at DESC);

CREATE TABLE improvement_actions (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id                UUID                     NOT NULL REFERENCES improvement_plans(id),
    rank                   INTEGER                  NOT NULL,
    title                  VARCHAR(255)             NOT NULL,
    description            TEXT                     NOT NULL,
    estimated_point_impact INTEGER,
    estimated_timeframe    VARCHAR(50),
    category               VARCHAR(30)              NOT NULL,
    status                 VARCHAR(20)              NOT NULL DEFAULT 'NOT_STARTED',
    completed_at           TIMESTAMP WITH TIME ZONE,
    explanation            TEXT,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_action_category CHECK (category IN ('UTILISATION', 'PAYMENT_HISTORY', 'CREDIT_AGE', 'CREDIT_MIX', 'NEW_CREDIT', 'CREDIT_BUILDING')),
    CONSTRAINT chk_action_status CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'DISMISSED'))
);

CREATE INDEX idx_actions_plan_id ON improvement_actions (plan_id);
CREATE INDEX idx_actions_status ON improvement_actions (plan_id, status);

CREATE TABLE milestones (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id          UUID                     NOT NULL,
    type                 VARCHAR(30)              NOT NULL,
    title                VARCHAR(255)             NOT NULL,
    description          TEXT,
    achieved_at          TIMESTAMP WITH TIME ZONE,
    score_at_achievement INTEGER,
    target_score         INTEGER,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_milestone_type CHECK (type IN ('SCORE_THRESHOLD', 'ACTIONS_COMPLETED', 'STREAK', 'FIRST_IMPROVEMENT'))
);

CREATE INDEX idx_milestones_customer ON milestones (customer_id, achieved_at DESC);
