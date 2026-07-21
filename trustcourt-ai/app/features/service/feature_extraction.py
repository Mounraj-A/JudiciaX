from app.legal.dto.domain import LegalDocument
from app.features.dto.domain import FeatureVector
from app.features.extractor.extractors import CaseFeatureExtractor, PartyFeatureExtractor, MedicalFeatureExtractor
from loguru import logger

class FeatureExtractionService:
    """
    Transforms a LegalDocument into the initial un-normalized FeatureVector structure.
    """
    
    def __init__(self):
        self.case_extractor = CaseFeatureExtractor()
        self.party_extractor = PartyFeatureExtractor()
        self.medical_extractor = MedicalFeatureExtractor()
        # Other extractors would be instantiated here (Document, Timeline, Legal, etc.)
        
    def process(self, document: LegalDocument, vector: FeatureVector) -> FeatureVector:
        logger.info("Running FeatureExtractionService")
        
        vector.case_features = self.case_extractor.extract(document)
        vector.party_features = self.party_extractor.extract(document)
        vector.medical_features = self.medical_extractor.extract(document)
        
        return vector
