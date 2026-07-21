from app.legal.dto.domain import LegalDocument
from app.features.dto.domain import FeatureVector
from app.features.service.feature_extraction import FeatureExtractionService
from app.features.validator.validator import FeatureValidationService
from app.features.normalizer.normalizer import FeatureNormalizationService
from app.features.encoder.encoder import FeatureEncodingService
from app.features.transformer.transformer import FeatureTransformationService
from app.features.pipeline.aggregator import FeatureAggregationService
from app.features.store.store import FeatureStoreService
from loguru import logger
import time

class FeaturePipelineService:
    """
    Orchestrates the deterministic transformation of LegalDocument into a FeatureVector.
    """
    
    def __init__(self):
        self.extraction_svc = FeatureExtractionService()
        self.validation_svc = FeatureValidationService()
        self.normalization_svc = FeatureNormalizationService()
        self.encoding_svc = FeatureEncodingService()
        self.transformation_svc = FeatureTransformationService()
        self.aggregation_svc = FeatureAggregationService()
        self.store_svc = FeatureStoreService()
        
    def process(self, document: LegalDocument) -> FeatureVector:
        logger.info(f"Starting Feature Engineering Pipeline for doc UUID: {document.extraction_metadata.document_uuid}")
        
        vector = FeatureVector(document_uuid=document.extraction_metadata.document_uuid)
        
        # 1. Extraction (Mapping LegalDocument objects to 17 Feature groups)
        vector = self.extraction_svc.process(document, vector)
        
        # 2. Validation
        vector = self.validation_svc.process(vector)
        
        # 3. Normalization
        vector = self.normalization_svc.process(vector)
        
        # 4. Encoding
        vector = self.encoding_svc.process(vector)
        
        # 5. Transformation (Derived/Composite)
        vector = self.transformation_svc.process(vector)
        
        # 6. Aggregation (Flattening into dense ML arrays)
        vector = self.aggregation_svc.process(vector)
        
        # 7. Persist to Feature Store
        self.store_svc.save(vector)
        
        logger.info("Feature Engineering Pipeline complete.")
        return vector
