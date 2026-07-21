from typing import List
from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.risk_assessment import RiskAssessment, RiskSeverity
from app.decision_support.config.jdse_config import jdse_config

class RiskAssessmentEngine:
    def assess(self, request: DecisionSupportRequest) -> List[RiskAssessment]:
        risks = []
        cts = request.trust_report.get("cts_score", 0.0)
        meta = request.case_metadata
        
        if cts < jdse_config.critical_risk_cts_threshold:
            risks.append(RiskAssessment(
                risk_id="RSK_01",
                title="Low Trust Score",
                description="The case exhibits a very low trust score, indicating potential anomalies or fraud.",
                severity=RiskSeverity.CRITICAL,
                mitigation_suggestion="Assign to a senior verification officer for manual review."
            ))
            
        if not meta.get("required_docs_present"):
            risks.append(RiskAssessment(
                risk_id="RSK_02",
                title="Missing Required Documents",
                description="Crucial documents are missing from the case file.",
                severity=RiskSeverity.HIGH,
                mitigation_suggestion="Send automated reminder to the filing party."
            ))
            
        if meta.get("has_legal_deadline_approaching"):
            risks.append(RiskAssessment(
                risk_id="RSK_03",
                title="Legal Deadline Approaching",
                description="Statutory deadlines are imminent.",
                severity=RiskSeverity.HIGH,
                mitigation_suggestion="Prioritize scheduling in the next available slot."
            ))
            
        return risks
