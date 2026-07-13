-- ============================================================
-- V22 — Case Management Module: Timeline & Transfer Tables
-- AI-Powered Judicial Case Management & Prioritization System
-- ============================================================

-- ── case_timelines ─────────────────────────────────────────

CREATE TABLE IF NOT EXISTS case_timelines (
    id              BIGINT        NOT NULL,
    uuid            VARCHAR(36)   NOT NULL,
    case_id         BIGINT        NOT NULL,
    event_type      VARCHAR(60)   NOT NULL,
    event_label     VARCHAR(200),
    description     TEXT,
    actor_uuid      VARCHAR(36),
    actor_role      VARCHAR(30),
    actor_name      VARCHAR(200),
    reason          VARCHAR(500),
    event_time      TIMESTAMP     NOT NULL,
    created_at      TIMESTAMP     NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_case_timeline      PRIMARY KEY (id),
    CONSTRAINT uq_case_timeline_uuid UNIQUE (uuid),
    CONSTRAINT fk_timeline_case      FOREIGN KEY (case_id) REFERENCES case_files(id)
);

CREATE INDEX IF NOT EXISTS idx_ct_case_id    ON case_timelines(case_id);
CREATE INDEX IF NOT EXISTS idx_ct_event_type ON case_timelines(event_type);
CREATE INDEX IF NOT EXISTS idx_ct_event_time ON case_timelines(event_time);

-- ── case_transfers ──────────────────────────────────────────

CREATE TABLE IF NOT EXISTS case_transfers (
    id                   BIGINT        NOT NULL,
    uuid                 VARCHAR(36)   NOT NULL,
    case_id              BIGINT        NOT NULL,
    transfer_type        VARCHAR(20)   NOT NULL,
    from_entity_uuid     VARCHAR(36),
    from_entity_name     VARCHAR(300),
    to_entity_uuid       VARCHAR(36),
    to_entity_name       VARCHAR(300),
    reason               VARCHAR(500)  NOT NULL,
    transferred_by_uuid  VARCHAR(36),
    transferred_by_role  VARCHAR(30),
    transferred_at       TIMESTAMP     NOT NULL,
    created_at           TIMESTAMP     NOT NULL,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),
    is_deleted           BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_case_transfer      PRIMARY KEY (id),
    CONSTRAINT uq_case_transfer_uuid UNIQUE (uuid),
    CONSTRAINT fk_transfer_case      FOREIGN KEY (case_id) REFERENCES case_files(id)
);

CREATE INDEX IF NOT EXISTS idx_transfer_case_id        ON case_transfers(case_id);
CREATE INDEX IF NOT EXISTS idx_transfer_type           ON case_transfers(transfer_type);
CREATE INDEX IF NOT EXISTS idx_transfer_transferred_at ON case_transfers(transferred_at);
