from app.governance.service.governance_services import IGovernanceEngine
from app.governance.dto.request.governance_request import GovernanceRequest
from app.governance.config.governance_config import governance_config

class GovernanceEngine(IGovernanceEngine):
    def enforce(self, request: GovernanceRequest) -> bool:
        """
        Validates the incoming pipeline execution against the active Governance Policy.
        """
        if request.pipeline_version != governance_config.global_policy_version and governance_config.enforce_strict_lineage:
            return False
            
        # Example: Enforce that nothing can proceed if Trust is artificially manipulated
        if request.cts_score < 0 or request.cts_score > 100:
            return False
            
        return True
