from pydantic import BaseModel, Field
from typing import Dict, Any, Optional

class ExperimentRecord(BaseModel):
    experiment_id: str
    researcher: str
    configuration: Dict[str, Any]
    dataset_version: str
    pipeline_version: str
    status: str = Field(..., description="e.g. RUNNING, COMPLETED, FAILED")
    execution_time_ms: float = 0.0
    results: Dict[str, Any] = Field(default_factory=dict)
    metrics_id: Optional[str] = None
    artifacts_generated: list[str] = Field(default_factory=list)
