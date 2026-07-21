from enum import Enum
import logging
from datetime import datetime

logger = logging.getLogger("governance.audit")

class GovernanceAuditEvent(str, Enum):
    EXPERIMENT_CREATED = "EXPERIMENT_CREATED"
    BENCHMARK_STARTED = "BENCHMARK_STARTED"
    PIPELINE_EVALUATED = "PIPELINE_EVALUATED"
    REPORT_GENERATED = "REPORT_GENERATED"
    DATASET_REGISTERED = "DATASET_REGISTERED"
    POLICY_UPDATED = "POLICY_UPDATED"
    GOVERNANCE_COMPLETED = "GOVERNANCE_COMPLETED"

class GovernanceAuditService:
    @staticmethod
    def log_event(event: GovernanceAuditEvent, reference_id: str, details: str = ""):
        log_message = f"[{datetime.utcnow().isoformat()}] AUDIT_GOV - {event.value} - Ref: {reference_id} - {details}"
        logger.info(log_message)
