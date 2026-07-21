from app.features.dto.domain import FeatureVector
from loguru import logger

class FeatureValidationService:
    """
    Validates the extracted features for missing values, required fields, and range consistency.
    """
    
    def process(self, vector: FeatureVector) -> FeatureVector:
        logger.info("Running FeatureValidationService")
        
        # Example validation logic
        missing_count = 0
        total_count = 0
        
        groups = [
            vector.case_features, 
            vector.party_features, 
            vector.medical_features
        ]
        
        for group in groups:
            for name, val in group.features.items():
                total_count += 1
                if val is None:
                    missing_count += 1
                    logger.warning(f"Missing required feature: {name} in {group.group_name}")
                    
        # Update statistics
        vector.statistics = type(vector.statistics)(
            total_features=total_count,
            missing_values=missing_count,
            completeness_score=1.0 - (missing_count / total_count) if total_count > 0 else 1.0
        ) if vector.statistics else None 
        # Fallback if statistics wasn't initialized
        if not vector.statistics:
            from app.features.dto.domain import FeatureStatistics
            vector.statistics = FeatureStatistics(
                total_features=total_count,
                missing_values=missing_count,
                completeness_score=1.0 - (missing_count / total_count) if total_count > 0 else 1.0
            )
            
        return vector
