-- ============================================================
-- V14__Create_Core_Domain_Tables.sql
-- Flyway Migration: Core judicial domain tables
-- Creates all NEW tables for Phase 1 domain model.
-- Does NOT modify any existing tables (V1-V13 safe).
-- ============================================================

-- ============================================================
-- COURTS
-- ============================================================
CREATE TABLE courts (
    id              BIGSERIAL       PRIMARY KEY,
    uuid            VARCHAR(36)     NOT NULL UNIQUE,
    court_code      VARCHAR(20)     NOT NULL UNIQUE,
    court_name      VARCHAR(300)    NOT NULL,
    court_type      VARCHAR(50),
    state           VARCHAR(100),
    district        VARCHAR(100),
    address         TEXT,
    phone_number    VARCHAR(20),
    email           VARCHAR(150),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);
CREATE INDEX idx_court_code       ON courts(court_code);
CREATE INDEX idx_court_state      ON courts(state);
CREATE INDEX idx_court_is_deleted ON courts(is_deleted);
COMMENT ON TABLE courts IS 'Court institutions — parent for benches, rooms, and case files';

-- ============================================================
-- COURT BENCHES
-- ============================================================
CREATE TABLE court_benches (
    id              BIGSERIAL       PRIMARY KEY,
    uuid            VARCHAR(36)     NOT NULL UNIQUE,
    court_id        BIGINT          NOT NULL REFERENCES courts(id) ON DELETE RESTRICT,
    bench_number    VARCHAR(20)     NOT NULL,
    bench_type      VARCHAR(30),
    description     VARCHAR(500),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    CONSTRAINT chk_bench_type CHECK (bench_type IN ('SINGLE','DIVISION','FULL','SPECIAL'))
);
CREATE INDEX idx_bench_court_id   ON court_benches(court_id);
CREATE INDEX idx_bench_is_deleted ON court_benches(is_deleted);
COMMENT ON TABLE court_benches IS 'Judicial bench composition within a court';

-- ============================================================
-- COURT ROOMS
-- ============================================================
CREATE TABLE court_rooms (
    id                      BIGSERIAL   PRIMARY KEY,
    uuid                    VARCHAR(36) NOT NULL UNIQUE,
    court_id                BIGINT      NOT NULL REFERENCES courts(id) ON DELETE RESTRICT,
    room_number             VARCHAR(20) NOT NULL,
    floor                   VARCHAR(20),
    capacity                INT,
    has_video_conferencing  BOOLEAN     NOT NULL DEFAULT FALSE,
    is_active               BOOLEAN     NOT NULL DEFAULT TRUE,
    is_deleted              BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100)
);
CREATE INDEX idx_room_court_id   ON court_rooms(court_id);
CREATE INDEX idx_room_is_deleted ON court_rooms(is_deleted);
COMMENT ON TABLE court_rooms IS 'Physical courtrooms within a court building';

-- ============================================================
-- CASE CATEGORIES
-- ============================================================
CREATE TABLE case_categories (
    id              BIGSERIAL       PRIMARY KEY,
    uuid            VARCHAR(36)     NOT NULL UNIQUE,
    category_code   VARCHAR(20)     NOT NULL UNIQUE,
    category_name   VARCHAR(100)    NOT NULL,
    description     VARCHAR(500),
    display_order   INT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);
CREATE INDEX idx_case_cat_code       ON case_categories(category_code);
CREATE INDEX idx_case_cat_is_deleted ON case_categories(is_deleted);
COMMENT ON TABLE case_categories IS 'High-level groupings for case types — administered by system admins';

-- ============================================================
-- CASE PARTIES
-- ============================================================
CREATE TABLE case_parties (
    id              BIGSERIAL       PRIMARY KEY,
    uuid            VARCHAR(36)     NOT NULL UNIQUE,
    case_id         BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    party_type      VARCHAR(30)     NOT NULL,
    party_name      VARCHAR(300)    NOT NULL,
    party_address   TEXT,
    phone_number    VARCHAR(15),
    email           VARCHAR(150),
    advocate_id     BIGINT          REFERENCES advocate_profiles(id) ON DELETE SET NULL,
    is_primary      BOOLEAN         NOT NULL DEFAULT FALSE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    CONSTRAINT chk_party_type CHECK (party_type IN (
        'PETITIONER','RESPONDENT','INTERVENOR','WITNESS','AMICUS_CURIAE'
    ))
);
CREATE INDEX idx_party_case_id    ON case_parties(case_id);
CREATE INDEX idx_party_type       ON case_parties(party_type);
CREATE INDEX idx_party_is_deleted ON case_parties(is_deleted);
COMMENT ON TABLE case_parties IS 'All parties involved in a judicial case';

-- ============================================================
-- CASE ASSIGNMENTS
-- ============================================================
CREATE TABLE case_assignments (
    id                  BIGSERIAL       PRIMARY KEY,
    uuid                VARCHAR(36)     NOT NULL UNIQUE,
    case_id             BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    judge_id            BIGINT          NOT NULL REFERENCES judge_profiles(id) ON DELETE RESTRICT,
    assigned_at         TIMESTAMP       NOT NULL,
    unassigned_at       TIMESTAMP,
    assignment_reason   VARCHAR(500),
    assigned_by_uuid    VARCHAR(36),
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);
CREATE INDEX idx_assign_case_id    ON case_assignments(case_id);
CREATE INDEX idx_assign_judge_id   ON case_assignments(judge_id);
CREATE INDEX idx_assign_is_active  ON case_assignments(is_active);
CREATE INDEX idx_assign_is_deleted ON case_assignments(is_deleted);
COMMENT ON TABLE case_assignments IS 'Judge-to-case assignment records — full history preserved';

-- ============================================================
-- CASE STATUS HISTORY
-- ============================================================
CREATE TABLE case_status_history (
    id                  BIGSERIAL       PRIMARY KEY,
    uuid                VARCHAR(36)     NOT NULL UNIQUE,
    case_id             BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    from_status         VARCHAR(30),
    to_status           VARCHAR(30)     NOT NULL,
    changed_at          TIMESTAMP       NOT NULL,
    changed_by_uuid     VARCHAR(36),
    changed_by_role     VARCHAR(30),
    remarks             VARCHAR(500),
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);
CREATE INDEX idx_csh_case_id    ON case_status_history(case_id);
CREATE INDEX idx_csh_changed_at ON case_status_history(changed_at);
CREATE INDEX idx_csh_to_status  ON case_status_history(to_status);
COMMENT ON TABLE case_status_history IS 'Append-only audit trail of all case status transitions';

-- ============================================================
-- CASE FLAGS
-- ============================================================
CREATE TABLE case_flags (
    id                      BIGSERIAL   PRIMARY KEY,
    uuid                    VARCHAR(36) NOT NULL UNIQUE,
    case_id                 BIGINT      NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT UNIQUE,
    medical_emergency       BOOLEAN     NOT NULL DEFAULT FALSE,
    child_involved          BOOLEAN     NOT NULL DEFAULT FALSE,
    women_safety            BOOLEAN     NOT NULL DEFAULT FALSE,
    senior_citizen          BOOLEAN     NOT NULL DEFAULT FALSE,
    disability              BOOLEAN     NOT NULL DEFAULT FALSE,
    financial_fraud         BOOLEAN     NOT NULL DEFAULT FALSE,
    cyber_crime             BOOLEAN     NOT NULL DEFAULT FALSE,
    threat_to_life          BOOLEAN     NOT NULL DEFAULT FALSE,
    high_public_interest    BOOLEAN     NOT NULL DEFAULT FALSE,
    is_deleted              BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100)
);
CREATE INDEX idx_flag_case_id    ON case_flags(case_id);
CREATE INDEX idx_flag_is_deleted ON case_flags(is_deleted);
COMMENT ON TABLE case_flags IS 'Boolean AI feature flags per case — used for urgency classification in Phase 2';

-- ============================================================
-- HEARINGS
-- ============================================================
CREATE TABLE hearings (
    id                  BIGSERIAL       PRIMARY KEY,
    uuid                VARCHAR(36)     NOT NULL UNIQUE,
    case_id             BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    court_room_id       BIGINT          REFERENCES court_rooms(id) ON DELETE SET NULL,
    judge_id            BIGINT          REFERENCES judge_profiles(id) ON DELETE SET NULL,
    scheduled_at        TIMESTAMP       NOT NULL,
    actual_start_at     TIMESTAMP,
    actual_end_at       TIMESTAMP,
    status              VARCHAR(20)     NOT NULL DEFAULT 'SCHEDULED',
    adjourn_reason      VARCHAR(500),
    next_hearing_date   DATE,
    hearing_number      INT,
    notes               TEXT,
    is_virtual          BOOLEAN         NOT NULL DEFAULT FALSE,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    CONSTRAINT chk_hearing_status CHECK (status IN (
        'SCHEDULED','IN_PROGRESS','COMPLETED','ADJOURNED','CANCELLED'
    ))
);
CREATE INDEX idx_hearing_case_id    ON hearings(case_id);
CREATE INDEX idx_hearing_status     ON hearings(status);
CREATE INDEX idx_hearing_scheduled  ON hearings(scheduled_at);
CREATE INDEX idx_hearing_is_deleted ON hearings(is_deleted);
COMMENT ON TABLE hearings IS 'Court hearing sessions — one case may have many hearings';

-- ============================================================
-- EVIDENCE
-- ============================================================
CREATE TABLE evidence (
    id                  BIGSERIAL       PRIMARY KEY,
    uuid                VARCHAR(36)     NOT NULL UNIQUE,
    case_id             BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    evidence_type       VARCHAR(30)     NOT NULL,
    title               VARCHAR(300)    NOT NULL,
    description         TEXT,
    document_id         BIGINT          REFERENCES documents(id) ON DELETE SET NULL,
    collected_at        DATE,
    collected_by        VARCHAR(200),
    location            VARCHAR(500),
    is_admitted         BOOLEAN         NOT NULL DEFAULT FALSE,
    admission_remarks   VARCHAR(500),
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    CONSTRAINT chk_evidence_type CHECK (evidence_type IN (
        'DOCUMENTARY','PHYSICAL','DIGITAL','WITNESS_STATEMENT','EXPERT_OPINION','OTHER'
    ))
);
CREATE INDEX idx_evidence_case_id    ON evidence(case_id);
CREATE INDEX idx_evidence_type       ON evidence(evidence_type);
CREATE INDEX idx_evidence_is_deleted ON evidence(is_deleted);
COMMENT ON TABLE evidence IS 'Evidence items submitted to a judicial case';

-- ============================================================
-- EVIDENCE VERIFICATIONS
-- ============================================================
CREATE TABLE evidence_verifications (
    id                      BIGSERIAL       PRIMARY KEY,
    uuid                    VARCHAR(36)     NOT NULL UNIQUE,
    evidence_id             BIGINT          NOT NULL REFERENCES evidence(id) ON DELETE RESTRICT UNIQUE,
    verification_status     VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    verified_by_uuid        VARCHAR(36),
    verified_by_role        VARCHAR(30),
    verified_at             TIMESTAMP,
    remarks                 VARCHAR(1000),
    is_deleted              BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    CONSTRAINT chk_evver_status CHECK (verification_status IN (
        'PENDING','VERIFIED','REJECTED','UNDER_REVIEW'
    ))
);
CREATE INDEX idx_evver_evidence_id  ON evidence_verifications(evidence_id);
CREATE INDEX idx_evver_status       ON evidence_verifications(verification_status);
CREATE INDEX idx_evver_is_deleted   ON evidence_verifications(is_deleted);
COMMENT ON TABLE evidence_verifications IS 'Court verification decisions on submitted evidence';

-- ============================================================
-- JUDGE NOTES
-- ============================================================
CREATE TABLE judge_notes (
    id              BIGSERIAL       PRIMARY KEY,
    uuid            VARCHAR(36)     NOT NULL UNIQUE,
    case_id         BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    judge_id        BIGINT          NOT NULL REFERENCES judge_profiles(id) ON DELETE RESTRICT,
    note_text       TEXT            NOT NULL,
    note_type       VARCHAR(30)     NOT NULL DEFAULT 'OBSERVATION',
    note_date       DATE            NOT NULL,
    is_confidential BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    CONSTRAINT chk_note_type CHECK (note_type IN (
        'OBSERVATION','INSTRUCTION','SUMMARY','INTERIM_ORDER','OTHER'
    ))
);
CREATE INDEX idx_jnote_case_id    ON judge_notes(case_id);
CREATE INDEX idx_jnote_judge_id   ON judge_notes(judge_id);
CREATE INDEX idx_jnote_is_deleted ON judge_notes(is_deleted);
COMMENT ON TABLE judge_notes IS 'Private judicial observations — confidential by default';

-- ============================================================
-- DOCUMENT VERSIONS
-- ============================================================
CREATE TABLE document_versions (
    id                  BIGSERIAL       PRIMARY KEY,
    uuid                VARCHAR(36)     NOT NULL UNIQUE,
    document_id         BIGINT          NOT NULL REFERENCES documents(id) ON DELETE RESTRICT,
    version_number      INT             NOT NULL,
    storage_path        VARCHAR(1000),
    checksum            VARCHAR(128),
    file_size_bytes     BIGINT,
    uploaded_by_uuid    VARCHAR(36),
    change_note         VARCHAR(500),
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    CONSTRAINT uq_docver_document_version UNIQUE (document_id, version_number)
);
CREATE INDEX idx_docver_document_id ON document_versions(document_id);
CREATE INDEX idx_docver_is_deleted  ON document_versions(is_deleted);
COMMENT ON TABLE document_versions IS 'Version history for court documents — immutable once created';

-- ============================================================
-- CASE ANALYSIS  (AI Placeholder)
-- ============================================================
CREATE TABLE case_analysis (
    id                  BIGSERIAL           PRIMARY KEY,
    uuid                VARCHAR(36)         NOT NULL UNIQUE,
    case_id             BIGINT              NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT UNIQUE,
    urgency_score       DOUBLE PRECISION,
    delay_impact_score  DOUBLE PRECISION,
    trust_score         DOUBLE PRECISION,
    confidence_score    DOUBLE PRECISION,
    recommendation      TEXT,
    model_version       VARCHAR(50),
    generated_at        TIMESTAMP,
    raw_response        TEXT,
    is_deleted          BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);
CREATE INDEX idx_analysis_case_id    ON case_analysis(case_id);
CREATE INDEX idx_analysis_is_deleted ON case_analysis(is_deleted);
COMMENT ON TABLE case_analysis IS 'AI analysis results — populated by FastAPI service in Phase 2';

-- ============================================================
-- CASE AI QUEUE  (AI Placeholder)
-- ============================================================
CREATE TABLE case_ai_queue (
    id                  BIGSERIAL       PRIMARY KEY,
    uuid                VARCHAR(36)     NOT NULL UNIQUE,
    case_id             BIGINT          NOT NULL REFERENCES case_files(id) ON DELETE RESTRICT,
    queue_status        VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    attempts            INT             NOT NULL DEFAULT 0,
    processed_at        TIMESTAMP,
    error_message       TEXT,
    request_payload     TEXT,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    CONSTRAINT chk_aiq_status CHECK (queue_status IN (
        'PENDING','PROCESSING','COMPLETED','FAILED','SKIPPED'
    ))
);
CREATE INDEX idx_aiq_case_id    ON case_ai_queue(case_id);
CREATE INDEX idx_aiq_status     ON case_ai_queue(queue_status);
CREATE INDEX idx_aiq_created_at ON case_ai_queue(created_at);
CREATE INDEX idx_aiq_is_deleted ON case_ai_queue(is_deleted);
COMMENT ON TABLE case_ai_queue IS 'AI processing queue — polled by FastAPI scheduler in Phase 2';

-- ============================================================
-- NOTIFICATION RECIPIENTS
-- ============================================================
CREATE TABLE notification_recipients (
    id                  BIGSERIAL       PRIMARY KEY,
    uuid                VARCHAR(36)     NOT NULL UNIQUE,
    notification_id     BIGINT          NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    user_id             BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    delivery_channel    VARCHAR(20)     NOT NULL DEFAULT 'IN_APP',
    delivery_status     VARCHAR(20)     NOT NULL DEFAULT 'SENT',
    sent_at             TIMESTAMP,
    delivered_at        TIMESTAMP,
    read_at             TIMESTAMP,
    failure_reason      VARCHAR(500),
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    CONSTRAINT chk_delivery_channel CHECK (delivery_channel IN ('IN_APP','EMAIL','SMS','PUSH')),
    CONSTRAINT chk_delivery_status  CHECK (delivery_status  IN ('SENT','DELIVERED','READ','FAILED')),
    CONSTRAINT uq_nrecip_notification_user_channel
        UNIQUE (notification_id, user_id, delivery_channel)
);
CREATE INDEX idx_nrecip_notification_id ON notification_recipients(notification_id);
CREATE INDEX idx_nrecip_user_id         ON notification_recipients(user_id);
CREATE INDEX idx_nrecip_status          ON notification_recipients(delivery_status);
CREATE INDEX idx_nrecip_is_deleted      ON notification_recipients(is_deleted);
COMMENT ON TABLE notification_recipients IS 'Per-user per-channel notification delivery tracking';

-- ============================================================
-- SEED: CASE CATEGORIES
-- ============================================================
INSERT INTO case_categories (uuid, category_code, category_name, description, display_order, created_at, created_by) VALUES
('C0001111-0000-0000-0000-000000000001', 'CRIM',  'Criminal',      'Criminal cases including IPC, NDPS, POCSO',     1, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000002', 'CIVIL',  'Civil',        'Civil suits, injunctions, property disputes',   2, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000003', 'FAM',   'Family',        'Matrimonial, custody, succession cases',        3, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000004', 'COMM',  'Commercial',    'Commercial disputes, contracts, insolvency',    4, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000005', 'CONST', 'Constitutional','Writ petitions, fundamental rights matters',    5, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000006', 'LAB',   'Labour',        'Employment, industrial disputes, ESI/PF',      6, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000007', 'TAX',   'Tax',           'Income tax, GST, customs and excise matters',  7, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000008', 'CONS',  'Consumer',      'Consumer protection and product liability',     8, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000009', 'ENV',   'Environmental', 'Environmental protection and pollution matters',9, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000010', 'MA',    'Motor Accident','Motor accident claim tribunals',               10, NOW(), 'SYSTEM'),
('C0001111-0000-0000-0000-000000000011', 'OTH',   'Other',         'Miscellaneous judicial matters',               11, NOW(), 'SYSTEM')
ON CONFLICT (category_code) DO NOTHING;
