CREATE TABLE credit_coach_consents (
    id                    UUID                     NOT NULL DEFAULT gen_random_uuid(),
    customer_id           UUID                     NOT NULL,
    cra_provider          VARCHAR(20)              NOT NULL,
    status                VARCHAR(20)              NOT NULL,
    consent_text_version  VARCHAR(10)              NOT NULL,
    consent_text_hash     VARCHAR(64)              NOT NULL,
    granted_at            TIMESTAMP WITH TIME ZONE,
    withdrawn_at          TIMESTAMP WITH TIME ZONE,
    channel               VARCHAR(10)              NOT NULL,
    ip_address            VARCHAR(512)             NOT NULL,
    device_fingerprint    VARCHAR(512)             NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_consents PRIMARY KEY (id),
    CONSTRAINT chk_cra_provider CHECK (cra_provider IN ('EXPERIAN', 'EQUIFAX', 'TRANSUNION')),
    CONSTRAINT chk_status CHECK (status IN ('GRANTED', 'WITHDRAWN')),
    CONSTRAINT chk_channel CHECK (channel IN ('IOS', 'ANDROID', 'WEB'))
);

CREATE INDEX idx_consents_customer_provider ON credit_coach_consents (customer_id, cra_provider, status);
CREATE INDEX idx_consents_status_provider ON credit_coach_consents (status, cra_provider);
CREATE INDEX idx_consents_created_at ON credit_coach_consents (created_at DESC);
