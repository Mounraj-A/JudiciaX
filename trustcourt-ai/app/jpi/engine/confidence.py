from app.features.dto.domain import FeatureVector
from app.jpi.dto.domain import PriorityConfidence, PriorityDecisionTrace
from loguru import logger

class PriorityConfidenceService:
    """
    Evaluates the confidence in the priority decision based on feature completeness
    and rule coverage.
    """
    
    def evaluate(self, vector: FeatureVector, trace: PriorityDecisionTrace) -> PriorityConfidence:
        logger.info("Running PriorityConfidenceService")
        
        # Feature completeness derived from FeatureVector statistics
        feat_comp = vector.statistics.completeness_score if vector.statistics else 0.8
        
        # Rule coverage logic (simulated)
        # If many rules were triggered or significant weights applied, coverage is high
        rule_coverage = min(1.0, len(trace.triggered_rules) / 5.0) if len(trace.triggered_rules) > 0 else 0.5
        
        overall = (feat_comp * 0.7) + (rule_coverage * 0.3)
        
        return PriorityConfidence(
            feature_completeness=feat_comp,
            rule_coverage=rule_coverage,
            overall_confidence=overall
        )
