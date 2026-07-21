from app.features.dto.domain import FeatureVector
from app.jpi.dto.domain import PriorityWeight, PriorityRule, PriorityContribution, PriorityDecisionTrace
from typing import List
from loguru import logger

class PriorityWeightEngine:
    """
    Applies configurable weights to the feature vector and active rules.
    """
    
    def apply_weights(self, vector: FeatureVector, rules: List[PriorityRule], weights: List[PriorityWeight], trace: PriorityDecisionTrace) -> List[PriorityContribution]:
        logger.info("Running PriorityWeightEngine")
        
        contributions = []
        
        # 1. Apply rule-based weights
        for rule in rules:
            # Map rule to a factor name conceptually. For demo, we assume mapping.
            # E.g. R1 corresponds to "medical_emergency"
            mapped_factor = "medical_emergency" if rule.rule_id == "R1" else "child_victim" if rule.rule_id == "R2" else None
            
            weight = next((w for w in weights if w.factor_name == mapped_factor), None)
            
            if weight:
                trace.applied_weights[weight.factor_name] = weight.base_weight * weight.multiplier
                contrib = PriorityContribution(
                    feature_name=weight.factor_name,
                    applied_rule_id=rule.rule_id,
                    applied_weight=weight.base_weight * weight.multiplier,
                    intermediate_score=weight.base_weight * weight.multiplier,
                    percentage_contribution=0.0 # Calculated later
                )
                contributions.append(contrib)
                logger.debug(f"Applied weight for {weight.factor_name}: {contrib.applied_weight}")
                
        # 2. Add continuous dense numerical features directly if needed (e.g. pending_days / 365)
        
        return contributions
