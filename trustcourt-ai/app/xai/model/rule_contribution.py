from pydantic import BaseModel, Field
from enum import Enum
from typing import Optional

class RuleStatus(str, Enum):
    TRIGGERED = "TRIGGERED"
    IGNORED = "IGNORED"
    REJECTED = "REJECTED"
    CONFLICTING = "CONFLICTING"

class RuleContribution(BaseModel):
    rule_id: str
    rule_name: str
    status: RuleStatus
    priority: int
    rule_version: str
    description: str
    legal_reference: Optional[str] = None
    conflict_resolution: Optional[str] = Field(None, description="How conflict was resolved if CONFLICTING")
