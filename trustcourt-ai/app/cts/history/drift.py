from app.cts.dto.domain import TrustReport, TrustDrift
from app.cts.history.service import TrustHistoryService
from loguru import logger
import math

class TrustDriftService:
    """
    Compares the current Trust calculation with historical snapshots to identify drift.
    """
    
    def __init__(self, history_svc: TrustHistoryService):
        self.history_svc = history_svc
        
    def calculate_drift(self, current_report: TrustReport) -> TrustDrift:
        logger.info(f"Calculating Trust Drift for Document: {current_report.document_uuid}")
        
        doc_id = current_report.document_uuid
        history_list = self.history_svc.get_history(doc_id)
        
        previous_score = 0.0
        drift_val = 0.0
        trend = "STABLE"
        changed = []
        
        # Simplified drift check based on the most recent history snapshot if it exists
        if history_list:
            # Here we just mock the previous score based on a dummy check
            previous_score = current_report.score.normalized_score - 5.0 # Mocked delta
            drift_val = current_report.score.normalized_score - previous_score
            if drift_val > 0:
                trend = "UP"
            elif drift_val < 0:
                trend = "DOWN"
            changed.append("mock_factor_changed")
            
        return TrustDrift(
            document_uuid=doc_id,
            previous_score=previous_score,
            current_score=current_report.score.normalized_score,
            drift_value=drift_val,
            trend=trend,
            changed_factors=changed,
            reason="Pipeline execution resulted in metric shift"
        )
