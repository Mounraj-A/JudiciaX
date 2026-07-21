-- V105: Add filing number, acknowledgement number, submission metadata to case_files
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS filing_number          VARCHAR(80)  UNIQUE;
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS acknowledgement_number VARCHAR(100) UNIQUE;
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS submitted_at           TIMESTAMP;
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS urgency_level          VARCHAR(20);
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS nature_of_case         VARCHAR(200);
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS cause_of_action        TEXT;
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS relief_sought          TEXT;
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS filing_mode            VARCHAR(50)  DEFAULT 'ONLINE';
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS language               VARCHAR(50)  DEFAULT 'ENGLISH';
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS jurisdiction           VARCHAR(200);
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS subject                VARCHAR(500);
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS remarks                TEXT;
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS limitation_date        DATE;
ALTER TABLE case_files ADD COLUMN IF NOT EXISTS incident_date          DATE;

CREATE INDEX IF NOT EXISTS idx_case_filing_number ON case_files(filing_number);
