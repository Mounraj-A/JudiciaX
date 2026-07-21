from pydantic import BaseModel, Field
from typing import Dict, Any

class DecisionSupportRequest(BaseModel):
    case_uuid: str
    feature_vector: Dict[str, Any]
    priority_report: Dict[str, Any]
    trust_report: Dict[str, Any]
    explainability_report: Dict[str, Any]
    workflow_status: str
    case_metadata: Dict[str, Any]
