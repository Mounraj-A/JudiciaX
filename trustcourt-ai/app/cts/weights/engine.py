from app.features.dto.domain import FeatureVector
from app.cts.dto.domain import TrustWeight, TrustRule, TrustContribution, TrustDecisionTrace
from typing import List
from loguru import logger

class TrustWeightEngine:
    """
    Applies configurable weights to the validated trust factors.
    """
    
    def apply_weights(self, vector: FeatureVector, rules: List[TrustRule], weights: List[TrustWeight], trace: TrustDecisionTrace) -> List[TrustContribution]:
        logger.info("Running TrustWeightEngine")
        contributions = []
        
        for rule in rules:
            # Map rule to a factor name conceptually.
            mapped_factor = "document_completeness" if rule.rule_id == "T_RULE_01" else "evidence_verification" if rule.rule_id == "T_RULE_02" else None
            
            weight = next((w for w in weights if w.factor_name == mapped_factor), None)
            
            if weight:
                applied_val = weight.base_weight * weight.multiplier
                trace.applied_weights[weight.factor_name] = applied_val
                
                # Confidence derived from feature metadata in a real scenario
                feat_confidence = 0.95 
                
                contrib = TrustContribution(
                    feature_name=weight.factor_name,
                    applied_rule_id=rule.rule_id,
                    applied_weight=applied_val,
                    intermediate_score=applied_val * feat_confidence,
                    percentage_contribution=0.0, # Calculated later
                    confidence=feat_confidence
                )
                contributions.append(contrib)
                logger.debug(f"Applied Trust Weight for {weight.factor_name}: {contrib.applied_weight}")
                
        return contributions
