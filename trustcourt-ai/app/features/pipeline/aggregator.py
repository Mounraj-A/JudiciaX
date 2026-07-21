from app.features.dto.domain import FeatureVector
from loguru import logger

class FeatureAggregationService:
    """
    Flattens the grouped features into dense numerical and categorical arrays
    that can be directly ingested by Scikit-Learn, PyTorch, or XGBoost.
    """
    
    def process(self, vector: FeatureVector) -> FeatureVector:
        logger.info("Running FeatureAggregationService")
        
        num_arr = []
        cat_arr = []
        bool_arr = []
        
        # Iterate over all defined groups
        groups = [
            vector.case_features, vector.party_features, vector.document_features,
            vector.legal_features, vector.timeline_features, vector.medical_features,
            vector.risk_features, vector.procedural_features, vector.trust_features,
            vector.urgency_features, vector.social_features
        ]
        
        for group in groups:
            for name, meta in group.metadata.items():
                val = group.features.get(name)
                
                # Simple type-based aggregation
                if meta.feature_type == "NUMERICAL":
                    num_arr.append(float(val) if val is not None else 0.0)
                elif meta.feature_type == "CATEGORICAL":
                    cat_arr.append(int(val) if val is not None else 0)
                elif meta.feature_type == "BOOLEAN":
                    bool_arr.append(int(val) if val is not None else 0)
                    
        vector.dense_numerical = num_arr
        vector.dense_categorical = cat_arr
        vector.dense_boolean = bool_arr
        
        logger.debug(f"Aggregated dense arrays: Num={len(num_arr)}, Cat={len(cat_arr)}, Bool={len(bool_arr)}")
        
        return vector
