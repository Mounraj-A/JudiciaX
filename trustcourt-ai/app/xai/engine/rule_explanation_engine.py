from typing import List
from app.xai.dto.request.explain_request import ExplainRequest
from app.xai.model.rule_contribution import RuleContribution, RuleStatus

class RuleExplanationEngine:
    def explain(self, request: ExplainRequest) -> List[RuleContribution]:
        """
        Explains which rules were triggered, ignored, rejected or conflicting.
        Extracts directly from PriorityDecisionTrace and TrustDecisionTrace.
        """
        rules_explained = []
        
        # Combine traces
        all_traces = request.priority_decision_trace + request.trust_decision_trace
        
        for step in all_traces:
            rule_id = step.get("rule_applied", "UNKNOWN_RULE")
            # Example logic mapping trace to explanation
            status = step.get("status", RuleStatus.TRIGGERED)
            
            rules_explained.append(RuleContribution(
                rule_id=rule_id,
                rule_name=rule_id.replace("_", " ").title(),
                status=status,
                priority=step.get("priority", 1),
                rule_version=step.get("rule_version", "1.0"),
                description=f"Rule {rule_id} was {status.lower()} during evaluation.",
                legal_reference=step.get("legal_reference", "No specific reference"),
                conflict_resolution=step.get("conflict_resolution", None)
            ))
            
        return rules_explained
