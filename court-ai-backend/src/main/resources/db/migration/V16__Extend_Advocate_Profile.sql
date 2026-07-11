-- ============================================================
-- V16__Extend_Advocate_Profile.sql
-- Flyway Migration: Extend advocate_profiles with full professional data
-- Additive only — ADD COLUMN IF NOT EXISTS — backward compatible
-- ============================================================

ALTER TABLE advocate_profiles
    ADD COLUMN IF NOT EXISTS enrollment_date         DATE,
    ADD COLUMN IF NOT EXISTS state_bar_council       VARCHAR(200),
    ADD COLUMN IF NOT EXISTS office_address          TEXT,
    ADD COLUMN IF NOT EXISTS office_city             VARCHAR(100),
    ADD COLUMN IF NOT EXISTS office_state            VARCHAR(100),
    ADD COLUMN IF NOT EXISTS office_pincode          VARCHAR(10),
    ADD COLUMN IF NOT EXISTS profile_photo_path      VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS digital_signature_path  VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS verification_status     VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    ADD COLUMN IF NOT EXISTS verified_by_uuid        VARCHAR(36),
    ADD COLUMN IF NOT EXISTS verified_at             TIMESTAMP;

-- CHECK constraint for verification_status
ALTER TABLE advocate_profiles
    ADD CONSTRAINT chk_advocate_verification_status
    CHECK (verification_status IN ('PENDING','APPROVED','REJECTED'));

CREATE INDEX IF NOT EXISTS idx_advocate_verification ON advocate_profiles(verification_status);

COMMENT ON COLUMN advocate_profiles.enrollment_date        IS 'Date of bar enrollment';
COMMENT ON COLUMN advocate_profiles.state_bar_council      IS 'State Bar Council where enrolled (e.g. Bar Council of Tamil Nadu)';
COMMENT ON COLUMN advocate_profiles.verification_status    IS 'Admin verification: PENDING, APPROVED, REJECTED';
COMMENT ON COLUMN advocate_profiles.profile_photo_path     IS 'Relative path to profile photo in local/cloud storage';
COMMENT ON COLUMN advocate_profiles.digital_signature_path IS 'Relative path to digital signature image';
