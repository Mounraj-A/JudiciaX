from app.features.dto.domain import FeatureVector
from loguru import logger

class FeatureNormalizationService:
    """
    Normalizes numeric features (e.g. Min-Max scaling for durations).
    """
    
    def process(self, vector: FeatureVector) -> FeatureVector:
        logger.info("Running FeatureNormalizationService")
        
        # Example Normalization: Cap Party counts to a maximum of 10 and scale 0-1
        num_parties = vector.party_features.features.get("num_parties", 0)
        capped_parties = min(num_parties, 10)
        normalized_parties = capped_parties / 10.0
        
        vector.party_features.features["num_parties_normalized"] = normalized_parties
        
        return vector
