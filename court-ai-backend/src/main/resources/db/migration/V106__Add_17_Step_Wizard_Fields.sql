-- Add new fields to case_files
ALTER TABLE case_files
    ADD COLUMN IF NOT EXISTS state VARCHAR(100),
    ADD COLUMN IF NOT EXISTS district VARCHAR(100),
    ADD COLUMN IF NOT EXISTS court_establishment VARCHAR(200),
    ADD COLUMN IF NOT EXISTS bench VARCHAR(100),
    ADD COLUMN IF NOT EXISTS court_hall VARCHAR(100),
    ADD COLUMN IF NOT EXISTS case_nature VARCHAR(50),
    ADD COLUMN IF NOT EXISTS filing_mode VARCHAR(50),
    ADD COLUMN IF NOT EXISTS signing_method VARCHAR(50),
    ADD COLUMN IF NOT EXISTS language VARCHAR(50),
    ADD COLUMN IF NOT EXISTS cause_title VARCHAR(500),
    ADD COLUMN IF NOT EXISTS subject VARCHAR(500),
    ADD COLUMN IF NOT EXISTS nature_of_suit VARCHAR(200),
    ADD COLUMN IF NOT EXISTS relief_sought TEXT,
    ADD COLUMN IF NOT EXISTS cause_of_action TEXT,
    ADD COLUMN IF NOT EXISTS date_of_cause_action DATE,
    ADD COLUMN IF NOT EXISTS court_fees_total DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS payment_method VARCHAR(50);

-- Add new fields to case_parties
ALTER TABLE case_parties
    ADD COLUMN IF NOT EXISTS alias_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS age INTEGER,
    ADD COLUMN IF NOT EXISTS pan_number VARCHAR(50),
    ADD COLUMN IF NOT EXISTS passport_number VARCHAR(50),
    ADD COLUMN IF NOT EXISTS nationality VARCHAR(100),
    ADD COLUMN IF NOT EXISTS additional_address TEXT,
    ADD COLUMN IF NOT EXISTS other_information TEXT;

-- Create subordinate_court_details table
CREATE TABLE IF NOT EXISTS subordinate_court_details (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,

    case_id BIGINT NOT NULL,
    subordinate_court VARCHAR(200),
    judge_name VARCHAR(200),
    cnr_number VARCHAR(50),
    case_number VARCHAR(50),
    year INTEGER,
    judgment_date DATE,
    impugned_order_date DATE,
    cc_applied_date DATE,
    cc_ready_date DATE,

    CONSTRAINT fk_subordinate_court_case FOREIGN KEY (case_id) REFERENCES case_files (id)
);

-- Create act_section_details table
CREATE TABLE IF NOT EXISTS act_section_details (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,

    case_id BIGINT NOT NULL,
    act_name VARCHAR(300),
    section VARCHAR(100),
    rule_info VARCHAR(100),
    article VARCHAR(100),

    CONSTRAINT fk_act_section_case FOREIGN KEY (case_id) REFERENCES case_files (id)
);
