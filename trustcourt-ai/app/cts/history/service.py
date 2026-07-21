from app.cts.dto.domain import TrustSnapshot, TrustHistory, TrustReport, TrustLevel
from typing import Dict, Optional, List
from loguru import logger

class TrustHistoryService:
    """
    Handles Persistence of Decision Traces, Snapshots, and History tracking for CTS.
    Uses in-memory dicts to simulate a database.
    """
    
    def __init__(self):
        self._snapshots: Dict[str, TrustSnapshot] = {}
        self._history: Dict[str, List[TrustHistory]] = {}
        
    def save_snapshot(self, report: TrustReport) -> TrustSnapshot:
        logger.info(f"Saving Trust Snapshot for report {report.report_id}")
        
        snapshot = TrustSnapshot(report=report)
        self._snapshots[snapshot.snapshot_id] = snapshot
        
        # Track history
        doc_id = report.document_uuid
        old_level = None
        if doc_id in self._history and self._history[doc_id]:
            old_level = self._history[doc_id][-1].new_level
            
        history = TrustHistory(
            document_uuid=doc_id,
            old_level=old_level,
            new_level=report.score.level,
            reason_for_change="CTS Calculation Pipeline Execution"
        )
        
        if doc_id not in self._history:
            self._history[doc_id] = []
        self._history[doc_id].append(history)
        
        return snapshot
        
    def get_snapshot(self, snapshot_id: str) -> Optional[TrustSnapshot]:
        return self._snapshots.get(snapshot_id)
        
    def get_history(self, document_uuid: str) -> List[TrustHistory]:
        return self._history.get(document_uuid, [])
