from app.features.dto.domain import FeatureVector
from loguru import logger

class FeatureEncodingService:
    """
    Deterministically encodes categorical values into integers/one-hot logic.
    """
    
    def process(self, vector: FeatureVector) -> FeatureVector:
        logger.info("Running FeatureEncodingService")
        
        # Example Encoding: Case Type to Integer Label
        # In a real system, this maps to a predefined dictionary/config
        case_type = vector.case_features.features.get("case_type", "UNKNOWN")
        case_type_map = {"W.P.": 1, "CRL.A.": 2, "UNKNOWN": 0}
        
        vector.case_features.features["case_type_encoded"] = case_type_map.get(str(case_type).upper(), 0)
        
        return vector
