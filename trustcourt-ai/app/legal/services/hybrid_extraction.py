from app.legal.dto.domain import LegalDocument
from app.legal.extractor.entity import PartyExtractor, JudgeExtractor
from app.legal.extractor.metadata import MetadataExtractor
from loguru import logger

class HybridExtractionService:
    """
    Executes the layered extraction strategy:
    Regex -> Dictionary -> SpaCy NER -> RapidFuzz -> Conflict Resolution
    """
    
    def __init__(self):
        self.metadata_ext = MetadataExtractor()
        self.party_ext = PartyExtractor()
        self.judge_ext = JudgeExtractor()
        
    def execute(self, text: str) -> LegalDocument:
        logger.info("Starting Hybrid Extraction Flow")
        
        document = LegalDocument()
        
        # 1. Extract Metadata
        document.metadata = self.metadata_ext.extract(text)
        
        # 2. Extract Entities
        document.parties.extend(self.party_ext.extract(text))
        document.judges.extend(self.judge_ext.extract(text))
        
        logger.info(f"Hybrid Extraction extracted {len(document.parties)} parties, {len(document.judges)} judges.")
        
        return document
