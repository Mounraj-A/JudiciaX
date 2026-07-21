from app.governance.service.governance_services import ISnapshotEngine
from typing import Dict, Any
import json
import hashlib

class SnapshotEngine(ISnapshotEngine):
    def create_snapshot(self, state: Dict[str, Any]) -> str:
        """
        Snapshots configuration state for reproducibility in research.
        """
        state_str = json.dumps(state, sort_keys=True)
        return hashlib.sha256(state_str.encode()).hexdigest()
