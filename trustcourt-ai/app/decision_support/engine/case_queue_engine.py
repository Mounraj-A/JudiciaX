from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.case_queue_recommendation import CaseQueueRecommendation, QueueType
from app.decision_support.config.jdse_config import jdse_config

class CaseQueueEngine:
    def optimize(self, request: DecisionSupportRequest) -> CaseQueueRecommendation:
        jpi = request.priority_report.get("jpi_score", 0.0)
        
        if jpi >= jdse_config.emergency_jpi_threshold:
            queue_type = QueueType.EMERGENCY
            reason = "JPI exceeds emergency threshold."
            position = 1
        elif jpi >= jdse_config.ready_jpi_threshold:
            queue_type = QueueType.REGULAR
            reason = "Case is ready for regular scheduling."
            position = 10 # Placeholder for actual queue metric
        else:
            queue_type = QueueType.PENDING
            reason = "Case lacks priority for immediate scheduling."
            position = 100
            
        return CaseQueueRecommendation(
            recommended_queue=queue_type,
            priority_position=position,
            reasoning=reason
        )
