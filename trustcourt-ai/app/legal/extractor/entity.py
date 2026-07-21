import re
from typing import List
from app.legal.extractor.base import BaseExtractor
from app.legal.dto.domain import Party, Judge, Advocate, ExtractionContext
from loguru import logger

class PartyExtractor(BaseExtractor[Party]):
    """Extracts Petitioner, Respondent, Accused, Victim from text using Hybrid strategy."""
    
    def extract(self, text: str) -> List[Party]:
        logger.debug("Executing PartyExtractor")
        parties = []
        
        # 1. Regex Strategy (Mocking hybrid execution order)
        petitioner_pattern = re.compile(r"(?i)petitioner[s]?\s*:\s*([A-Za-z\s]+)")
        for match in petitioner_pattern.finditer(text):
            raw_name = match.group(1).strip()
            parties.append(
                Party(
                    canonical_id=self._generate_id("PRTY"),
                    raw_name=self._create_context(raw_name, "REGEX", "PARTY_PETITIONER"),
                    canonical_name=raw_name,
                    role=self._create_context("Petitioner", "INFERRED", "RULE_ROLE")
                )
            )
            
        # 2. Add SpaCy NER fallback here if regex fails (Simulated)
        return parties

class JudgeExtractor(BaseExtractor[Judge]):
    """Extracts Judges."""
    
    def extract(self, text: str) -> List[Judge]:
        logger.debug("Executing JudgeExtractor")
        judges = []
        
        pattern = re.compile(r"(?i)before\s+hon'?ble\s+mr\.\s+justice\s+([A-Za-z\s\.]+)")
        for match in pattern.finditer(text):
            raw_name = match.group(1).strip()
            judges.append(
                Judge(
                    canonical_id=self._generate_id("JDG"),
                    raw_name=self._create_context(raw_name, "REGEX", "HON_JUSTICE_PATTERN"),
                    canonical_name=raw_name,
                    designation=self._create_context("Justice", "INFERRED", "RULE_DESIGNATION")
                )
            )
            
        return judges
