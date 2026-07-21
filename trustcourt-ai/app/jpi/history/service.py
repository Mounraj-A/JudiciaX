from app.jpi.dto.domain import PrioritySnapshot, PriorityHistory, PriorityReport, PriorityLevel
from typing import Dict, Optional, List
from loguru import logger

class PriorityHistoryService:
    """
    Handles Persistence of Decision Traces, Snapshots, and History tracking.
    Uses in-memory dicts to simulate a database.
    """
    
    def __init__(self):
        self._snapshots: Dict[str, PrioritySnapshot] = {}
        self._history: Dict[str, List[PriorityHistory]] = {}
        
    def save_snapshot(self, report: PriorityReport) -> PrioritySnapshot:
        logger.info(f"Saving Priority Snapshot for report {report.report_id}")
        
        snapshot = PrioritySnapshot(report=report)
        self._snapshots[snapshot.snapshot_id] = snapshot
        
        # Also track history
        doc_id = report.document_uuid
        
        old_level = None
        if doc_id in self._history and self._history[doc_id]:
            old_level = self._history[doc_id][-1].new_level
            
        history = PriorityHistory(
            document_uuid=doc_id,
            old_level=old_level,
            new_level=report.score.level,
            reason_for_change="JPI Calculation Pipeline Execution"
        )
        
        if doc_id not in self._history:
            self._history[doc_id] = []
        self._history[doc_id].append(history)
        
        return snapshot
        
    def get_snapshot(self, snapshot_id: str) -> Optional[PrioritySnapshot]:
        return self._snapshots.get(snapshot_id)
        
    def get_history(self, document_uuid: str) -> List[PriorityHistory]:
        return self._history.get(document_uuid, [])
