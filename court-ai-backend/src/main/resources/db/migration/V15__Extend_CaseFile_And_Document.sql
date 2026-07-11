-- ============================================================
-- V15__Extend_CaseFile_And_Document.sql
-- Flyway Migration: Additive extensions to existing tables
-- Only uses ADD COLUMN and relaxed CHECK constraints.
-- No columns dropped. Backward compatible.
-- ============================================================

-- ============================================================
-- EXTEND: case_files
-- Add new columns for court FK, category FK, priority,
-- CNR number, filing year, police station, and act section.
-- ============================================================
ALTER TABLE case_files
    ADD COLUMN IF NOT EXISTS court_id           BIGINT       REFERENCES courts(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS case_category_id   BIGINT       REFERENCES case_categories(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS priority           VARCHAR(20)  NOT NULL DEFAULT 'LOW',
    ADD COLUMN IF NOT EXISTS cnr_number         VARCHAR(50)  UNIQUE,
    ADD COLUMN IF NOT EXISTS filing_year        INT,
    ADD COLUMN IF NOT EXISTS police_station     VARCHAR(200),
    ADD COLUMN IF NOT EXISTS act_section        VARCHAR(500);

-- Index new FK columns
CREATE INDEX IF NOT EXISTS idx_case_court_id   ON case_files(court_id);
CREATE INDEX IF NOT EXISTS idx_case_priority   ON case_files(priority);
CREATE INDEX IF NOT EXISTS idx_case_cnr        ON case_files(cnr_number);

-- Add CHECK constraint for priority
ALTER TABLE case_files
    ADD CONSTRAINT chk_case_priority CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL'));

-- Relax CaseStatus constraint to include all new lifecycle values
-- while preserving all existing values for backward compatibility.
ALTER TABLE case_files DROP CONSTRAINT IF EXISTS chk_case_status;
ALTER TABLE case_files ADD CONSTRAINT chk_case_status CHECK (status IN (
    -- New lifecycle statuses (Phase 2)
    'DRAFT', 'SUBMITTED', 'UNDER_SCRUTINY', 'REGISTERED', 'UNDER_REVIEW',
    'AI_ANALYZED', 'JUDGE_ASSIGNED', 'HEARING_SCHEDULED', 'IN_PROGRESS', 'REJECTED',
    -- Existing statuses (preserved for backward compatibility)
    'FILED', 'ADMITTED', 'PENDING_HEARING', 'IN_HEARING',
    'ADJOURNED', 'JUDGEMENT_RESERVED', 'DISPOSED', 'CLOSED', 'APPEALED'
));

COMMENT ON COLUMN case_files.court_id         IS 'FK to courts table — the court where this case is filed';
COMMENT ON COLUMN case_files.case_category_id IS 'FK to case_categories — broad category grouping';
COMMENT ON COLUMN case_files.priority         IS 'CasePriority enum: LOW/MEDIUM/HIGH/CRITICAL';
COMMENT ON COLUMN case_files.cnr_number       IS 'Court Number Record — unique national case identifier';
COMMENT ON COLUMN case_files.act_section      IS 'Applicable acts and sections e.g. IPC 302, CrPC 154';

-- ============================================================
-- EXTEND: documents
-- Add new metadata columns for AI pipeline, versioning,
-- and enhanced storage tracking.
-- ============================================================
ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS original_file_name  VARCHAR(500),
    ADD COLUMN IF NOT EXISTS stored_file_name    VARCHAR(500),
    ADD COLUMN IF NOT EXISTS checksum            VARCHAR(128),
    ADD COLUMN IF NOT EXISTS page_count          INT,
    ADD COLUMN IF NOT EXISTS version             INT          NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS ocr_status          VARCHAR(20)  NOT NULL DEFAULT 'NOT_APPLICABLE',
    ADD COLUMN IF NOT EXISTS uploaded_by_uuid    VARCHAR(36),
    ADD COLUMN IF NOT EXISTS is_confidential     BOOLEAN      NOT NULL DEFAULT FALSE;

-- Index for OCR queue polling
CREATE INDEX IF NOT EXISTS idx_document_ocr_status ON documents(ocr_status);

-- Add OCR status constraint
ALTER TABLE documents
    ADD CONSTRAINT chk_document_ocr_status CHECK (ocr_status IN (
        'PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'NOT_APPLICABLE'
    ));

COMMENT ON COLUMN documents.original_file_name IS 'Filename as uploaded by the user';
COMMENT ON COLUMN documents.stored_file_name   IS 'System-generated filename used in cloud storage';
COMMENT ON COLUMN documents.checksum           IS 'SHA-256 hash for file integrity verification';
COMMENT ON COLUMN documents.ocr_status         IS 'OcrStatus enum — tracks AI text extraction pipeline';
COMMENT ON COLUMN documents.uploaded_by_uuid   IS 'UUID of uploading user — denormalised for performance';
COMMENT ON COLUMN documents.version            IS 'Current document version number (starts at 1)';
