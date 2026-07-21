from typing import List
from app.xai.dto.request.explain_request import ExplainRequest
from app.xai.model.feature_importance import FeatureImportance, ContributionType

class FeatureImportanceEngine:
    def calculate(self, request: ExplainRequest) -> List[FeatureImportance]:
        """
        Calculate feature importance based on traces.
        Does not recalculate scores, only explains them.
        """
        features_impact = []
        
        # Merge priority and trust contributions to determine overall importance
        # Mocking the extraction from traces for demonstration of the architecture
        
        for idx, contrib in enumerate(request.priority_contributions + request.trust_contributions):
            feature_name = contrib.get("feature_name", f"feature_{idx}")
            impact_val = float(contrib.get("impact", 0.0))
            
            if impact_val > 0:
                c_type = ContributionType.POSITIVE
            elif impact_val < 0:
                c_type = ContributionType.NEGATIVE
            else:
                c_type = ContributionType.NEUTRAL
                
            features_impact.append(FeatureImportance(
                feature_name=feature_name,
                feature_value=str(contrib.get("feature_value", "")),
                contribution_type=c_type,
                contribution_percentage=min(abs(impact_val) * 100, 100.0), # Example normalization
                contribution_rank=0, # To be sorted
                description=f"Feature '{feature_name}' contributed {'positively' if c_type == ContributionType.POSITIVE else 'negatively'}."
            ))
            
        # Sort and assign rank
        features_impact.sort(key=lambda x: x.contribution_percentage, reverse=True)
        for rank, f in enumerate(features_impact, 1):
            f.contribution_rank = rank
            
        return features_impact
