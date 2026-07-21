from enum import Enum
import logging
from datetime import datetime

logger = logging.getLogger("jdse.audit")

class JDSEAuditEvent(str, Enum):
    DECISION_SUPPORT_STARTED = "DECISION_SUPPORT_STARTED"
    READINESS_GENERATED = "READINESS_GENERATED"
    CHECKLIST_GENERATED = "CHECKLIST_GENERATED"
    RECOMMENDATIONS_GENERATED = "RECOMMENDATIONS_GENERATED"
    RISK_ANALYZED = "RISK_ANALYZED"
    QUEUE_ANALYZED = "QUEUE_ANALYZED"
    REPORT_GENERATED = "REPORT_GENERATED"
    DECISION_SUPPORT_COMPLETED = "DECISION_SUPPORT_COMPLETED"

class JDSEAuditService:
    @staticmethod
    def log_event(event: JDSEAuditEvent, case_uuid: str, details: str = ""):
        log_message = f"[{datetime.utcnow().isoformat()}] AUDIT - {event.value} - Case: {case_uuid} - {details}"
        logger.info(log_message)
