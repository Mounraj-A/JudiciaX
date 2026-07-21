from typing import List
from app.xai.dto.request.explain_request import ExplainRequest
from app.xai.model.weight_contribution import WeightContribution

class WeightExplanationEngine:
    def explain(self, request: ExplainRequest) -> List[WeightContribution]:
        """
        Explains Configured vs Applied weights and normalizes impact.
        """
        weights_explained = []
        
        # Example using priority contributions
        for contrib in request.priority_contributions:
            feature_name = contrib.get("feature_name", "UNKNOWN")
            cfg_weight = float(contrib.get("configured_weight", 1.0))
            app_weight = float(contrib.get("applied_weight", 1.0))
            
            # Simulated normalization
            norm_weight = min(app_weight / max(cfg_weight, 0.001), 1.0)
            
            weights_explained.append(WeightContribution(
                feature_name=feature_name,
                configured_weight=cfg_weight,
                applied_weight=app_weight,
                normalized_weight=norm_weight,
                weight_percentage=norm_weight * 100,
                weight_impact=f"Weight for {feature_name} was adjusted from {cfg_weight} to {app_weight} due to contextual rules." if cfg_weight != app_weight else f"Default weight {cfg_weight} applied."
            ))
            
        return weights_explained
