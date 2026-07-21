from pydantic import BaseModel
from typing import Dict, Any

class GovernanceRequest(BaseModel):
    case_uuid: str
    jpi_score: float
    cts_score: float
    pipeline_version: str
    configuration_metadata: Dict[str, Any]
