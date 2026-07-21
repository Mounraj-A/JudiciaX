from app.jpi.dto.domain import PriorityGovernance, PriorityLevel, PriorityScore
from loguru import logger

class PriorityDecisionGovernanceService:
    """
    Final check on the Priority decision. Resolves emergency overrides,
    validates threshold consistency, and generates the Governance report.
    """
    
    def validate(self, governance: PriorityGovernance, score: PriorityScore) -> PriorityGovernance:
        logger.info("Running PriorityDecisionGovernanceService")
        
        # E.g. Governance rule: Emergency level cannot be assigned if normalized score is below 90
        if score.level == PriorityLevel.EMERGENCY and score.normalized_score < 90:
            governance.validation_messages.append("Emergency Level suppressed by Governance due to Score < 90")
            governance.overrides_applied = True
            score.level = PriorityLevel.CRITICAL
            logger.warning("Governance Override applied to Priority Level")
            
        return governance
