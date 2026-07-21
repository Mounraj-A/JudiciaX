from app.governance.service.governance_services import IHistoryEngine
from typing import Any
from app.governance.audit.governance_audit import logger

class HistoryEngine(IHistoryEngine):
    def log_history(self, record: Any):
        """
        Archives execution logs for long-term governance compliance.
        """
        logger.info(f"ARCHIVED HISTORY: {record}")
