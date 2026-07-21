from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.case_readiness import CaseReadinessReport, ReadinessLevel
from app.decision_support.config.jdse_config import jdse_config

class CaseReadinessEngine:
    def evaluate(self, request: DecisionSupportRequest, procedural_pct: float, doc_pct: float, ev_pct: float) -> CaseReadinessReport:
        # Simplistic heuristic for architectural demonstration
        jpi = request.priority_report.get("jpi_score", 0.0)
        cts = request.trust_report.get("cts_score", 0.0)
        
        # Calculate a weighted readiness score
        readiness_score = (procedural_pct * 0.4) + (doc_pct * 0.3) + (ev_pct * 0.3)
        
        if readiness_score >= 100.0 and cts >= jdse_config.ready_cts_threshold:
            level = ReadinessLevel.READY_FOR_REVIEW
            summary = "Case is fully verified and ready for judicial review."
            is_ready = True
        elif doc_pct < 100.0:
            level = ReadinessLevel.PENDING_DOCUMENTS
            summary = "Case is pending required documents."
            is_ready = False
        elif ev_pct < 100.0:
            level = ReadinessLevel.PENDING_EVIDENCE
            summary = "Case is pending evidence submission."
            is_ready = False
        else:
            level = ReadinessLevel.NEEDS_VERIFICATION
            summary = "Case needs manual verification by the clerk."
            is_ready = False
            
        return CaseReadinessReport(
            readiness_score=readiness_score,
            readiness_level=level,
            readiness_summary=summary,
            is_ready=is_ready
        )
