-- ============================================================
-- V23 — Document Management Module
-- New tables: document_verifications, document_checksums,
--             document_ocr_jobs
-- NOTE: document_versions already created in V14.
-- ============================================================

-- ── document_verifications ────────────────────────────────
CREATE TABLE IF NOT EXISTS document_verifications (
    id                  BIGINT       NOT NULL DEFAULT nextval('base_seq'),
    uuid                VARCHAR(36)  NOT NULL,
    document_id         BIGINT       NOT NULL REFERENCES documents(id),
    verified_by_uuid    VARCHAR(36),
    verified_at         TIMESTAMP,
    status              VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    remarks             TEXT,
    verification_type   VARCHAR(50)  NOT NULL DEFAULT 'CLERK_REVIEW',
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uq_doc_verification_uuid UNIQUE (uuid)
);

CREATE INDEX idx_docverif_document_id  ON document_verifications(document_id);
CREATE INDEX idx_docverif_status       ON document_verifications(status);
CREATE INDEX idx_docverif_is_deleted   ON document_verifications(is_deleted);

-- ── document_checksums ────────────────────────────────────
CREATE TABLE IF NOT EXISTS document_checksums (
    id              BIGINT       NOT NULL DEFAULT nextval('base_seq'),
    uuid            VARCHAR(36)  NOT NULL,
    document_id     BIGINT       NOT NULL REFERENCES documents(id),
    sha256          VARCHAR(128) NOT NULL,
    md5             VARCHAR(64)  NOT NULL,
    generated_at    TIMESTAMP    NOT NULL,
    is_duplicate    BOOLEAN      NOT NULL DEFAULT FALSE,
    duplicate_of_uuid VARCHAR(36),
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uq_doc_checksum_uuid UNIQUE (uuid)
);

CREATE UNIQUE INDEX idx_docchk_document_id ON document_checksums(document_id);
CREATE INDEX        idx_docchk_sha256      ON document_checksums(sha256);
CREATE INDEX        idx_docchk_is_deleted  ON document_checksums(is_deleted);

-- ── document_ocr_jobs ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS document_ocr_jobs (
    id              BIGINT       NOT NULL DEFAULT nextval('base_seq'),
    uuid            VARCHAR(36)  NOT NULL,
    document_id     BIGINT       NOT NULL REFERENCES documents(id),
    status          VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    ocr_engine      VARCHAR(50)  DEFAULT 'TESSERACT',
    language        VARCHAR(20)  DEFAULT 'eng',
    started_at      TIMESTAMP,
    completed_at    TIMESTAMP,
    retry_count     INT          NOT NULL DEFAULT 0,
    max_retries     INT          NOT NULL DEFAULT 3,
    error_message   TEXT,
    extracted_text  TEXT,
    queued_at       TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uq_doc_ocr_uuid UNIQUE (uuid)
);

CREATE INDEX idx_docrocr_document_id ON document_ocr_jobs(document_id);
CREATE INDEX idx_docrocr_status      ON document_ocr_jobs(status);
CREATE INDEX idx_docrocr_is_deleted  ON document_ocr_jobs(is_deleted);

-- ── Extend documents table with new tracking columns ─────
ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS document_status   VARCHAR(40)  DEFAULT 'UPLOADED',
    ADD COLUMN IF NOT EXISTS download_count    INT          NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_downloaded_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS content_hash      VARCHAR(128),
    ADD COLUMN IF NOT EXISTS ocr_text          TEXT;
