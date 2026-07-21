from enum import Enum
import logging
from datetime import datetime

logger = logging.getLogger("xai.audit")

class XAIAuditEvent(str, Enum):
    XAI_STARTED = "XAI_STARTED"
    FEATURE_EXPLAINED = "FEATURE_EXPLAINED"
    RULE_EXPLAINED = "RULE_EXPLAINED"
    WEIGHT_EXPLAINED = "WEIGHT_EXPLAINED"
    DECISION_TRACE_GENERATED = "DECISION_TRACE_GENERATED"
    REPORT_CREATED = "REPORT_CREATED"
    XAI_COMPLETED = "XAI_COMPLETED"

class XAIAuditService:
    @staticmethod
    def log_event(event: XAIAuditEvent, case_uuid: str, details: str = ""):
        # In a real enterprise app, this integrates with Kafka/DB
        log_message = f"[{datetime.utcnow().isoformat()}] AUDIT - {event.value} - Case: {case_uuid} - {details}"
        logger.info(log_message)
