from app.features.dto.domain import FeatureVector
from app.jpi.dto.domain import PriorityRule, PriorityDecisionTrace
from typing import List
from loguru import logger

class PriorityRuleEngine:
    """
    Evaluates completely deterministic logic against the feature vector.
    """
    
    def evaluate(self, vector: FeatureVector, rules: List[PriorityRule], trace: PriorityDecisionTrace) -> List[PriorityRule]:
        logger.info("Running PriorityRuleEngine")
        triggered_rules = []
        
        # In a real engine, we might use a formal Rules Engine framework (like Drools in Java)
        # Here we simulate the evaluation of configured rules against the dense features.
        
        # E.g. Check Medical Emergency Rule (R1)
        med_emergency = vector.medical_features.features.get("medical_emergency", 0)
        if med_emergency == 1:
            rule = next((r for r in rules if r.rule_id == "R1"), None)
            if rule and rule.is_active:
                triggered_rules.append(rule)
                trace.evaluated_features.append("medical_emergency")
                trace.triggered_rules.append(rule.rule_id)
                logger.debug(f"Rule Triggered: {rule.rule_name}")
                
        # E.g. Check Child Victim Rule (R2)
        child_victim = vector.party_features.features.get("child_victim", 0) # Assumed derived
        if child_victim == 1:
            rule = next((r for r in rules if r.rule_id == "R2"), None)
            if rule and rule.is_active:
                triggered_rules.append(rule)
                trace.evaluated_features.append("child_victim")
                trace.triggered_rules.append(rule.rule_id)
                logger.debug(f"Rule Triggered: {rule.rule_name}")
                
        return triggered_rules
