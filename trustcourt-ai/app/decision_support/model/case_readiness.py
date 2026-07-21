from pydantic import BaseModel, Field
from enum import Enum

class ReadinessLevel(str, Enum):
    READY_FOR_REVIEW = "READY_FOR_REVIEW"
    PENDING_DOCUMENTS = "PENDING_DOCUMENTS"
    PENDING_EVIDENCE = "PENDING_EVIDENCE"
    NEEDS_VERIFICATION = "NEEDS_VERIFICATION"

class CaseReadinessReport(BaseModel):
    readiness_score: float = Field(..., ge=0.0, le=100.0)
    readiness_level: ReadinessLevel
    readiness_summary: str
    is_ready: bool = Field(..., description="True if score > threshold (e.g. 80)")
