from app.features.dto.domain import FeatureVector
from loguru import logger

class FeatureTransformationService:
    """
    Computes composite or derived features based on existing combinations.
    """
    
    def process(self, vector: FeatureVector) -> FeatureVector:
        logger.info("Running FeatureTransformationService")
        
        # Example Transformation: Risk Score based on existing features
        # If there are multiple accused and medical emergency, increase a composite risk flag
        
        multi_accused = vector.party_features.features.get("multiple_accused", 0)
        medical_emer = vector.medical_features.features.get("medical_emergency", 0)
        
        # Derived boolean flag for "High Public Risk"
        high_risk = 1 if (multi_accused == 1 and medical_emer == 1) else 0
        
        vector.risk_features.features["high_public_risk"] = high_risk
        
        return vector
