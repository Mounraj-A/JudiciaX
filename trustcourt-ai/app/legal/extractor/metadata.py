import re
from typing import Optional
from app.legal.extractor.base import BaseExtractor
from app.legal.dto.domain import CaseMetadata, ExtractionContext
from loguru import logger

class MetadataExtractor(BaseExtractor[CaseMetadata]):
    """Extracts Case Number, FIR, Court Name."""
    
    def extract(self, text: str) -> CaseMetadata:
        logger.debug("Executing MetadataExtractor")
        metadata = CaseMetadata()
        
        # FIR Number Extraction
        fir_match = re.search(r"(?i)FIR\s+No\.?\s*(\d+/\d{4})", text)
        if fir_match:
            metadata.fir_number = self._create_context(fir_match.group(1), "REGEX", "FIR_PATTERN")
            
        # Case Number Extraction
        case_match = re.search(r"(?i)(W\.P\.\s*No\.?\s*\d+/\d{4}|Crl\.A\.\s*No\.?\s*\d+/\d{4})", text)
        if case_match:
            metadata.case_number = self._create_context(case_match.group(1), "REGEX", "CASE_PATTERN")
            
        return metadata
