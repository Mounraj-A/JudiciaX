from app.features.dto.domain import FeatureVector
from app.cts.dto.domain import TrustRule, TrustDecisionTrace
from typing import List
from loguru import logger

class TrustRuleEngine:
    """
    Evaluates completely deterministic trust logic against the feature vector.
    """
    
    def evaluate(self, vector: FeatureVector, rules: List[TrustRule], trace: TrustDecisionTrace) -> List[TrustRule]:
        logger.info("Running TrustRuleEngine")
        triggered_rules = []
        
        # Example: Document Completeness (T_RULE_01)
        doc_complete = vector.document_features.features.get("document_completeness", 0)
        # Using a simulated check - if it's explicitly 0 it fails, if it's 1 it passes.
        # Since it's trust, we are checking for positive markers
        if doc_complete == 1:
            rule = next((r for r in rules if r.rule_id == "T_RULE_01"), None)
            if rule and rule.is_active:
                triggered_rules.append(rule)
                trace.evaluated_features.append("document_completeness")
                trace.triggered_rules.append(rule.rule_id)
                logger.debug(f"Trust Rule Triggered: {rule.rule_name}")
                
        # Example: Evidence Verification (T_RULE_02)
        evidence_verified = vector.document_features.features.get("evidence_verification", 0)
        if evidence_verified == 1:
            rule = next((r for r in rules if r.rule_id == "T_RULE_02"), None)
            if rule and rule.is_active:
                triggered_rules.append(rule)
                trace.evaluated_features.append("evidence_verification")
                trace.triggered_rules.append(rule.rule_id)
                logger.debug(f"Trust Rule Triggered: {rule.rule_name}")
                
        return triggered_rules
