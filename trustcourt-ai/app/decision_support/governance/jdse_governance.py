from typing import Dict, Any
from app.decision_support.config.jdse_config import jdse_config

class JDSEGovernanceService:
    @staticmethod
    def audit_support_report(report_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Validates that the generated Decision Support Report complies with current policy thresholds.
        """
        # Architectural mock
        jpi = report_data.get("jpi", 0.0)
        cts = report_data.get("cts", 0.0)
        
        compliant = True
        reason = "Compliant with active JDSE policies."
        
        if jpi > 100 or jpi < 0 or cts > 100 or cts < 0:
            compliant = False
            reason = "Invalid score boundaries detected in input reports."
            
        return {
            "policy_version": jdse_config.policy_version,
            "engine_version": jdse_config.engine_version,
            "compliant": compliant,
            "reason": reason
        }
