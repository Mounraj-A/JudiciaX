from pydantic import BaseModel, Field
from typing import List, Optional

class TraceStep(BaseModel):
    step_id: str
    feature_name: str
    rule_applied: str
    weight_applied: float
    intermediate_score: float
    confidence_at_step: float

class UnifiedDecisionTrace(BaseModel):
    case_uuid: str
    initial_priority_score: float = 0.0
    initial_trust_score: float = 0.0
    final_jpi: float
    final_cts: float
    steps: List[TraceStep] = Field(default_factory=list)
