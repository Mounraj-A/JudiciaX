from typing import List, Dict, Any, Optional
from pydantic import BaseModel, Field
from datetime import datetime

class ExplanationSummary(BaseModel):
    persona: str = Field(..., description="Target persona: Judge, Citizen, Technical, Research")
    summary_text: str = Field(..., description="Human readable summary of the decision")
    generated_at: datetime = Field(default_factory=datetime.utcnow)

class ExplainabilityReport(BaseModel):
    case_uuid: str
    jpi_score: float
    cts_score: float
    overall_confidence: float
    summaries: Dict[str, ExplanationSummary] = Field(default_factory=dict)
    feature_importance: List[Any] = Field(default_factory=list)
    rule_contributions: List[Any] = Field(default_factory=list)
    weight_contributions: List[Any] = Field(default_factory=list)
    decision_trace: Optional[Any] = None
    reasoning_graph: Optional[Any] = None
    governance: Optional[Any] = None
    
    class Config:
        schema_extra = {
            "example": {
                "case_uuid": "123e4567-e89b-12d3-a456-426614174000",
                "jpi_score": 85.5,
                "cts_score": 92.0,
                "overall_confidence": 0.95
            }
        }
