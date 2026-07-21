from app.features.dto.domain import FeatureVector, FeatureStoreRecord
from typing import Dict, Optional
from loguru import logger

class FeatureStoreService:
    """
    Simulates a persistent Feature Store (e.g. Redis/Feast/Hopsworks) for caching
    and versioning the FeatureVectors before they are served to ML models.
    """
    
    def __init__(self):
        # Mock in-memory storage mapping document_uuid -> FeatureStoreRecord
        self._store: Dict[str, FeatureStoreRecord] = {}
        
    def save(self, vector: FeatureVector) -> FeatureStoreRecord:
        logger.info(f"Saving FeatureVector {vector.vector_id} to Feature Store")
        
        record = FeatureStoreRecord(
            vector_id=vector.vector_id,
            document_uuid=vector.document_uuid,
            feature_vector=vector
        )
        
        self._store[vector.document_uuid] = record
        return record
        
    def get(self, document_uuid: str) -> Optional[FeatureStoreRecord]:
        return self._store.get(document_uuid)
