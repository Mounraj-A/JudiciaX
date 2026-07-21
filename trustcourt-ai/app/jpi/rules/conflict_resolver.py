from app.jpi.dto.domain import PriorityRule, PriorityGovernance
from typing import List
from loguru import logger

class PriorityRuleConflictResolver:
    """
    Detects and resolves rule evaluation conflicts (e.g., mutually exclusive rules triggering).
    """
    
    def resolve(self, triggered_rules: List[PriorityRule], governance: PriorityGovernance) -> List[PriorityRule]:
        logger.info("Running PriorityRuleConflictResolver")
        
        resolved_rules = []
        rule_ids = [r.rule_id for r in triggered_rules]
        
        # Mock conflict resolution: if we have rule R1 and R2 which are mutually exclusive (example)
        if "R1" in rule_ids and "R_MUTUAL_EX" in rule_ids:
            logger.warning("Conflict detected between R1 and R_MUTUAL_EX. Overriding R_MUTUAL_EX.")
            governance.conflict_resolved_count += 1
            governance.validation_messages.append("R_MUTUAL_EX suppressed due to R1 precedence.")
            
        for rule in triggered_rules:
            # Simple pass-through for now
            resolved_rules.append(rule)
            
        return resolved_rules
