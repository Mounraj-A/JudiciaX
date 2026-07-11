-- ============================================================
-- V17__Clerk_Module_Schema.sql
-- Flyway Migration: Clerk Portal schema extension
-- All changes are additive (ADD COLUMN IF NOT EXISTS)
-- ============================================================

-- ============================================================
-- 1. EXTEND case_files WITH CLERK SCRUTINY FIELDS
-- ============================================================
ALTER TABLE case_files
    ADD COLUMN IF NOT EXISTS registered_at            TIMESTAMP,
    ADD COLUMN IF NOT EXISTS registered_by_uuid       VARCHAR(36),
    ADD COLUMN IF NOT EXISTS scrutiny_clerk_uuid      VARCHAR(36),
    ADD COLUMN IF NOT EXISTS verification_remarks     TEXT,
    ADD COLUMN IF NOT EXISTS official_case_number     VARCHAR(80),
    ADD COLUMN IF NOT EXISTS duplicate_case_uuids     TEXT,
    ADD COLUMN IF NOT EXISTS is_duplicate_checked     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS is_jurisdiction_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS judge_queue_position     INTEGER,
    ADD COLUMN IF NOT EXISTS judge_queued_at          TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_case_official_number
    ON case_files(official_case_number)
    WHERE official_case_number IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_case_scrutiny_clerk
    ON case_files(scrutiny_clerk_uuid);

COMMENT ON COLUMN case_files.official_case_number  IS 'Clerk-generated official registration number, e.g. TN-COIMBATORE-DC-2026-000001';
COMMENT ON COLUMN case_files.registered_by_uuid    IS 'UUID of the clerk who registered the case';
COMMENT ON COLUMN case_files.judge_queue_position  IS 'Position in the pending judge assignment queue';

-- ============================================================
-- 2. EXTEND documents WITH CLERK VERIFICATION FIELDS
-- ============================================================
ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS verification_remarks  VARCHAR(500),
    ADD COLUMN IF NOT EXISTS verified_by_uuid      VARCHAR(36),
    ADD COLUMN IF NOT EXISTS verified_at           TIMESTAMP,
    ADD COLUMN IF NOT EXISTS rejection_reason      VARCHAR(500);

-- ============================================================
-- 3. EXTEND evidence WITH CLERK VERIFICATION FIELDS
-- ============================================================
ALTER TABLE evidence
    ADD COLUMN IF NOT EXISTS is_verified          BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS verified_by_uuid     VARCHAR(36),
    ADD COLUMN IF NOT EXISTS verified_at          TIMESTAMP,
    ADD COLUMN IF NOT EXISTS verification_remarks VARCHAR(500),
    ADD COLUMN IF NOT EXISTS rejection_reason     VARCHAR(500);

-- ============================================================
-- 4. EXTEND clerk_profiles WITH COURT ASSIGNMENT
-- ============================================================
ALTER TABLE clerk_profiles
    ADD COLUMN IF NOT EXISTS court_id BIGINT REFERENCES courts(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_clerk_court_id ON clerk_profiles(court_id);

COMMENT ON COLUMN clerk_profiles.court_id IS 'Court the clerk is assigned to for jurisdiction scoping';

-- ============================================================
-- 5. CREATE case_objections TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS case_objections (
    id                    BIGSERIAL     PRIMARY KEY,
    uuid                  VARCHAR(36)   NOT NULL UNIQUE,
    case_id               BIGINT        NOT NULL REFERENCES case_files(id) ON DELETE CASCADE,
    raised_by_clerk_uuid  VARCHAR(36)   NOT NULL,
    objection_type        VARCHAR(50)   NOT NULL,
    reason                TEXT          NOT NULL,
    missing_documents     TEXT,
    correction_required   TEXT,
    is_resolved           BOOLEAN       NOT NULL DEFAULT FALSE,
    resolved_at           TIMESTAMP,
    resolved_by_uuid      VARCHAR(36),
    resolution_notes      TEXT,
    is_deleted            BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_objection_case_id    ON case_objections(case_id);
CREATE INDEX IF NOT EXISTS idx_objection_clerk_uuid ON case_objections(raised_by_clerk_uuid);
CREATE INDEX IF NOT EXISTS idx_objection_is_resolved ON case_objections(is_resolved);

COMMENT ON TABLE  case_objections IS 'Clerk objections raised during case scrutiny process';
COMMENT ON COLUMN case_objections.objection_type IS 'MISSING_DOCUMENT, INVALID_DOCUMENT, JURISDICTION_MISMATCH, DUPLICATE_CASE, INCOMPLETE_INFORMATION, OTHER';

-- ============================================================
-- 6. CASE NUMBER SEQUENCE TABLE — one counter per court per year
-- ============================================================
CREATE TABLE IF NOT EXISTS case_number_sequences (
    id         BIGSERIAL   PRIMARY KEY,
    court_id   BIGINT      NOT NULL REFERENCES courts(id),
    year       INTEGER     NOT NULL,
    last_seq   INTEGER     NOT NULL DEFAULT 0,
    UNIQUE (court_id, year)
);

COMMENT ON TABLE case_number_sequences IS 'Monotonically increasing sequence counter for official case number generation';
