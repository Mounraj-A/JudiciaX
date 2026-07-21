-- ============================================================
-- V109__Update_Party_Type_Constraint.sql
-- Flyway Migration: Drops the old check constraint on case_parties
-- to allow the new wizard party types (Co-Defendant, etc.)
-- ============================================================

ALTER TABLE case_parties DROP CONSTRAINT IF EXISTS chk_party_type;
