from app.legal.dto.domain import LegalDocument
from app.features.dto.domain import FeatureGroup
from app.features.extractor.base import BaseFeatureExtractor
from loguru import logger

class CaseFeatureExtractor(BaseFeatureExtractor):
    
    def extract(self, document: LegalDocument) -> FeatureGroup:
        logger.debug("Extracting Case Features")
        group = FeatureGroup(group_name="Case Features")
        
        # In a real scenario, we'd parse this from document.metadata
        has_case_number = 1 if document.metadata.case_number else 0
        has_fir = 1 if document.metadata.fir_number else 0
        
        self._add_feature(group, "has_case_number", has_case_number, "BOOLEAN", "Indicates if a case number exists")
        self._add_feature(group, "has_fir", has_fir, "BOOLEAN", "Indicates if an FIR exists")
        self._add_feature(group, "case_complexity_score", 0.0, "NUMERICAL", "Placeholder for complexity logic")
        
        return group

class PartyFeatureExtractor(BaseFeatureExtractor):
    
    def extract(self, document: LegalDocument) -> FeatureGroup:
        logger.debug("Extracting Party Features")
        group = FeatureGroup(group_name="Party Features")
        
        num_parties = len(document.parties)
        num_victims = sum(1 for p in document.parties if p.role and p.role.value.lower() == "victim")
        num_accused = sum(1 for p in document.parties if p.role and p.role.value.lower() == "accused")
        
        self._add_feature(group, "num_parties", num_parties, "NUMERICAL", "Total number of parties")
        self._add_feature(group, "num_victims", num_victims, "NUMERICAL", "Total number of victims")
        self._add_feature(group, "num_accused", num_accused, "NUMERICAL", "Total number of accused")
        self._add_feature(group, "multiple_accused", 1 if num_accused > 1 else 0, "BOOLEAN", "If there are multiple accused")
        
        return group

class MedicalFeatureExtractor(BaseFeatureExtractor):
    
    def extract(self, document: LegalDocument) -> FeatureGroup:
        logger.debug("Extracting Medical Features (Derived)")
        group = FeatureGroup(group_name="Medical Features")
        
        # Derived heuristic: if an organization of type "Hospital" exists, flag medical emergency
        has_hospital = any(org.org_type.value.lower() == "hospital" for org in document.organizations if org.org_type)
        
        self._add_feature(group, "medical_emergency", 1 if has_hospital else 0, "BOOLEAN", "Derived from presence of hospital")
        self._add_feature(group, "medical_evidence_present", 1 if has_hospital else 0, "BOOLEAN", "Medical records presence flag")
        
        return group
