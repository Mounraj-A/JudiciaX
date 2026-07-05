-- ============================================================
-- V3__Create_Case_And_Document_Tables.sql
-- Flyway Migration: Core judicial case and document management tables
-- ============================================================

-- ============================================================
-- CASE FILES TABLE
-- ============================================================
CREATE TABLE case_files (
    id                      BIGSERIAL       PRIMARY KEY,
    uuid                    VARCHAR(36)     NOT NULL UNIQUE,
    case_number             VARCHAR(50)     NOT NULL UNIQUE,
    case_title              VARCHAR(500)    NOT NULL,
    case_description        TEXT,
    case_type               VARCHAR(50)     NOT NULL,
    status                  VARCHAR(30)     NOT NULL DEFAULT 'FILED',
    filing_date             DATE,
    hearing_date            DATE,
    priority_score          DOUBLE PRECISION,
    assigned_judge_id       BIGINT          REFERENCES judge_profiles(id) ON DELETE SET NULL,
    petitioner_advocate_id  BIGINT          REFERENCES advocate_profiles(id) ON DELETE SET NULL,
    respondent_advocate_id  BIGINT          REFERENCES advocate_profiles(id) ON DELETE SET NULL,
    petitioner_name         VARCHAR(200),
    respondent_name         VARCHAR(200),
    court_name              VARCHAR(200),
    is_deleted              BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),

    CONSTRAINT chk_case_type CHECK (case_type IN (
        'CIVIL', 'CRIMINAL', 'FAMILY', 'COMMERCIAL', 'CONSTITUTIONAL',
        'LABOUR', 'TAX', 'CONSUMER', 'ENVIRONMENTAL', 'MOTOR_ACCIDENT', 'OTHER'
    )),
    CONSTRAINT chk_case_status CHECK (status IN (
        'FILED', 'ADMITTED', 'PENDING_HEARING', 'IN_HEARING',
        'ADJOURNED', 'JUDGEMENT_RESERVED', 'DISPOSED', 'CLOSED', 'APPEALED'
    ))
);

CREATE INDEX idx_case_number     ON case_files(case_number);
CREATE INDEX idx_case_status     ON case_files(status);
CREATE INDEX idx_case_judge_id   ON case_files(assigned_judge_id);
CREATE INDEX idx_case_is_deleted ON case_files(is_deleted);
CREATE INDEX idx_case_type       ON case_files(case_type);
CREATE INDEX idx_case_filing     ON case_files(filing_date);

COMMENT ON TABLE  case_files                IS 'Central case file registry for all judicial cases';
COMMENT ON COLUMN case_files.priority_score IS 'AI-computed priority score (0-100). Higher = more urgent';
COMMENT ON COLUMN case_files.case_number    IS 'Unique case number assigned at filing';

-- ============================================================
-- DOCUMENTS TABLE
-- ============================================================
CREATE TABLE documents (
    id              BIGSERIAL       PRIMARY KEY,
    uuid            VARCHAR(36)     NOT NULL UNIQUE,
    case_id         BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    file_name       VARCHAR(255)    NOT NULL,
    original_name   VARCHAR(255),
    document_type   VARCHAR(50),
    file_size_bytes BIGINT,
    mime_type       VARCHAR(100),
    storage_path    VARCHAR(1000),
    storage_bucket  VARCHAR(200),
    description     VARCHAR(500),
    is_verified     BOOLEAN         NOT NULL DEFAULT FALSE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);

CREATE INDEX idx_document_case_id ON documents(case_id);
CREATE INDEX idx_document_type    ON documents(document_type);
CREATE INDEX idx_document_deleted ON documents(is_deleted);

COMMENT ON TABLE  documents              IS 'Document metadata for case-attached files (actual files in cloud storage)';
COMMENT ON COLUMN documents.storage_path IS 'Cloud storage key/path (S3 or GCS object key)';
