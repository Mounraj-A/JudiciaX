from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.scheduling_recommendation import SchedulingRecommendation
from datetime import datetime, timedelta

class SchedulingRecommendationEngine:
    def analyze(self, request: DecisionSupportRequest) -> SchedulingRecommendation:
        # Architecture placeholder. Real implementation would check calendar systems.
        # Here we just output a standard support DTO.
        jpi = request.priority_report.get("jpi_score", 0.0)
        
        # Simple heuristic: Higher JPI gets scheduled sooner
        days_to_add = 2 if jpi > 80 else 14
        next_date = (datetime.utcnow() + timedelta(days=days_to_add)).isoformat()
        
        return SchedulingRecommendation(
            next_hearing_date=next_date,
            preferred_courtroom="Courtroom A (Placeholder)",
            estimated_duration_minutes=60 if jpi > 80 else 30,
            conflict_detected=False
        )
