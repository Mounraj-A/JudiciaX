from pydantic import BaseModel, Field
from enum import Enum

class RiskSeverity(str, Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"

class RiskAssessment(BaseModel):
    risk_id: str
    title: str = Field(..., description="e.g. Legal Deadline Risk")
    description: str
    severity: RiskSeverity
    mitigation_suggestion: str
