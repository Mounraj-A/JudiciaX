from app.cts.dto.domain import TrustGovernance, TrustLevel, TrustScore
from loguru import logger

class TrustDecisionGovernanceService:
    """
    Final governance check on the Trust Score. Resolves extreme anomalies.
    """
    
    def validate(self, governance: TrustGovernance, score: TrustScore) -> TrustGovernance:
        logger.info("Running TrustDecisionGovernanceService")
        
        # Example Governance rule: Trust Level cannot be VERY_HIGH if score is under 90
        if score.level == TrustLevel.VERY_HIGH and score.normalized_score < 90:
            governance.validation_messages.append("VERY_HIGH Level suppressed by Governance due to Score < 90")
            governance.overrides_applied = True
            score.level = TrustLevel.HIGH
            logger.warning("Governance Override applied to Trust Level")
            
        return governance
