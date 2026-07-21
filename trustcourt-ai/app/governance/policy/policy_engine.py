from app.governance.service.governance_services import IPolicyEngine
from app.governance.model.governance_policy import GovernancePolicy

class PolicyEngine(IPolicyEngine):
    def validate_policy(self, policy: GovernancePolicy) -> bool:
        """
        Ensures a new governance policy is valid before activating it.
        """
        if policy.max_latency_ms < 0:
            return False
        if policy.min_cts_override_threshold > 100 or policy.min_cts_override_threshold < 0:
            return False
        return True
