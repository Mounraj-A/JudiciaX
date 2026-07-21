from typing import Dict, Any
from app.xai.config.xai_config import xai_config

class XAIGovernanceService:
    @staticmethod
    def audit_decision(jpi_score: float, cts_score: float) -> Dict[str, Any]:
        """
        Explains why governance accepted or rejected a decision based on threshold versions.
        """
        # Threshold logic mocked for architectural demonstration
        accepted = True
        reason = "Scores are within acceptable bounds."
        
        if jpi_score < 0 or jpi_score > 100:
            accepted = False
            reason = f"JPI score {jpi_score} violates boundaries."
            
        return {
            "policy_version": xai_config.policy_version,
            "engine_version": xai_config.engine_version,
            "accepted": accepted,
            "reason": reason
        }
