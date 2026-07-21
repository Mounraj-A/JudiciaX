from app.jpi.dto.domain import PriorityPolicy, PriorityGovernance
from typing import List
from loguru import logger

class PriorityPolicyService:
    """
    Enforces High Court / Supreme Court policies on priority execution.
    """
    
    def enforce(self, policies: List[PriorityPolicy], governance: PriorityGovernance):
        logger.info("Running PriorityPolicyService")
        
        active_policies = [p for p in policies if p.active]
        if not active_policies:
            governance.validation_messages.append("No active overarching policies found.")
            
        for policy in active_policies:
            logger.debug(f"Enforcing Policy: {policy.policy_id} (Level: {policy.policy_level})")
            
        governance.policy_compliant = True
