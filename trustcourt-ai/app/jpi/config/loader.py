from app.jpi.dto.domain import PriorityConfiguration, PriorityRule, PriorityWeight, PriorityPolicy
from loguru import logger

class PriorityConfigurationLoader:
    """
    Simulates external configuration loading (e.g. from YAML or DB).
    Provides the active rules, weights, and policies for the JPI Engine.
    """
    
    def __init__(self):
        self._config = self._load_defaults()
        
    def _load_defaults(self) -> PriorityConfiguration:
        logger.info("Loading default external Priority Configurations")
        
        rules = [
            PriorityRule(rule_id="R1", rule_name="Medical Emergency Rule", rule_category="URGENCY", is_active=True, version="1.0", description="Increases priority if medical emergency is true", legal_reference="SC Directive 2023"),
            PriorityRule(rule_id="R2", rule_name="Child Victim Rule", rule_category="URGENCY", is_active=True, version="1.0", description="Increases priority for child victims", legal_reference="POCSO Act")
        ]
        
        weights = [
            PriorityWeight(weight_id="W1", factor_name="medical_emergency", base_weight=30.0),
            PriorityWeight(weight_id="W2", factor_name="child_victim", base_weight=25.0),
            PriorityWeight(weight_id="W3", factor_name="serious_offence", base_weight=15.0)
        ]
        
        policies = [
            PriorityPolicy(policy_id="POL-SC-1", policy_level="SC", active=True, version="1.0")
        ]
        
        return PriorityConfiguration(rules=rules, weights=weights, policies=policies)
        
    def get_configuration(self) -> PriorityConfiguration:
        return self._config
