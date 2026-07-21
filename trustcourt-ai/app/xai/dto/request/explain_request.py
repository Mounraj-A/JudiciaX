from pydantic import BaseModel, Field
from typing import Dict, Any, List

class ExplainRequest(BaseModel):
    case_uuid: str
    feature_vector: Dict[str, Any] = Field(..., description="The raw feature vector input")
    priority_report: Dict[str, Any] = Field(..., description="JPI Priority output")
    trust_report: Dict[str, Any] = Field(..., description="CTS Trust output")
    priority_decision_trace: List[Dict[str, Any]] = Field(default_factory=list)
    trust_decision_trace: List[Dict[str, Any]] = Field(default_factory=list)
    
    # Metadata for rule mappings if provided by previous phases
    priority_contributions: List[Dict[str, Any]] = Field(default_factory=list)
    trust_contributions: List[Dict[str, Any]] = Field(default_factory=list)
