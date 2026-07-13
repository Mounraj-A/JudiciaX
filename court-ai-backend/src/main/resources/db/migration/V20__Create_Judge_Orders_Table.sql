-- ============================================================
-- V20 — Judge Module: judge_orders table
-- AI-Powered Judicial Case Management & Prioritization System
-- ============================================================

CREATE TABLE IF NOT EXISTS judge_orders (

    -- ── Primary Key ────────────────────────────────────────
    id              BIGINT        NOT NULL,
    uuid            VARCHAR(36)   NOT NULL,

    -- ── References ─────────────────────────────────────────
    case_id         BIGINT        NOT NULL
        CONSTRAINT fk_jorder_case
            REFERENCES case_files(id),

    judge_id        BIGINT        NOT NULL
        CONSTRAINT fk_jorder_judge
            REFERENCES judge_profiles(id),

    -- ── Order Classification ────────────────────────────────
    order_type      VARCHAR(30)   NOT NULL,   -- INTERIM_ORDER | FINAL_ORDER | JUDGMENT
    title           VARCHAR(300)  NOT NULL,
    order_text      TEXT,

    -- ── File Metadata ───────────────────────────────────────
    original_file_name  VARCHAR(500),
    mime_type           VARCHAR(100),
    storage_path        VARCHAR(1000),
    file_size_bytes     BIGINT,

    -- ── Dates ───────────────────────────────────────────────
    order_date      DATE,

    -- ── Status ──────────────────────────────────────────────
    is_signed       BOOLEAN       NOT NULL DEFAULT FALSE,
    remarks         VARCHAR(1000),

    -- ── Audit ───────────────────────────────────────────────
    created_at      TIMESTAMP     NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN       NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_judge_orders PRIMARY KEY (id),
    CONSTRAINT uq_judge_orders_uuid UNIQUE (uuid)
);

-- ── Indexes ────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_jorder_case_id
    ON judge_orders(case_id);

CREATE INDEX IF NOT EXISTS idx_jorder_judge_id
    ON judge_orders(judge_id);

CREATE INDEX IF NOT EXISTS idx_jorder_type
    ON judge_orders(order_type);

CREATE INDEX IF NOT EXISTS idx_jorder_is_deleted
    ON judge_orders(is_deleted);
