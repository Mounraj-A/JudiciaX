from pydantic import BaseModel, Field
from typing import Dict, Any, List

class GovernancePolicy(BaseModel):
    policy_id: str
    version: str
    description: str
    allowed_pipeline_versions: List[str]
    max_latency_ms: float = 2000.0
    min_cts_override_threshold: float = 40.0
    is_active: bool = True
