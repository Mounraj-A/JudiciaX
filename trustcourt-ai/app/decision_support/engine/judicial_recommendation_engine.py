from typing import List
from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.judicial_recommendation import JudicialRecommendation, RecommendationPriority
from app.decision_support.model.case_readiness import CaseReadinessReport, ReadinessLevel
from app.decision_support.config.jdse_config import jdse_config

class JudicialRecommendationEngine:
    def generate(self, request: DecisionSupportRequest, readiness: CaseReadinessReport) -> List[JudicialRecommendation]:
        recs = []
        jpi = request.priority_report.get("jpi_score", 0.0)
        cts = request.trust_report.get("cts_score", 0.0)
        
        if readiness.readiness_level == ReadinessLevel.READY_FOR_REVIEW:
            recs.append(JudicialRecommendation(
                recommendation_id="REC_01",
                title="Mark Ready for Judicial Review",
                description="All preconditions met. Case can be scheduled.",
                priority=RecommendationPriority.HIGH,
                rule_triggered="RULE_READINESS_COMPLETE"
            ))
            
        if jpi >= jdse_config.emergency_jpi_threshold:
            recs.append(JudicialRecommendation(
                recommendation_id="REC_02",
                title="Escalate Due to Emergency",
                description="High Priority Index suggests an emergency. Immediate attention required.",
                priority=RecommendationPriority.CRITICAL,
                rule_triggered="RULE_JPI_EMERGENCY"
            ))
            
        if cts < jdse_config.critical_risk_cts_threshold:
            recs.append(JudicialRecommendation(
                recommendation_id="REC_03",
                title="Request Additional Evidence",
                description="Trust score is critically low. More evidence required to proceed.",
                priority=RecommendationPriority.HIGH,
                rule_triggered="RULE_CTS_LOW"
            ))
            
        return recs
