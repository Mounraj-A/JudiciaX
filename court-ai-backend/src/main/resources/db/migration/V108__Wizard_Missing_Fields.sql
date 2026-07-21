-- ============================================================
-- V108__Wizard_Missing_Fields.sql
-- Flyway Migration: Adds missing fields to case_files for Wizard Phase F13
-- ============================================================

ALTER TABLE case_files
    ADD COLUMN IF NOT EXISTS court_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS detailed_description TEXT,
    ADD COLUMN IF NOT EXISTS case_category_uuid VARCHAR(36);
