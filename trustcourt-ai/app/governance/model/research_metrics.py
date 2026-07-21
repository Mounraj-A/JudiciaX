from pydantic import BaseModel
from typing import Dict, Any

class ResearchMetrics(BaseModel):
    experiment_id: str
    dataset_version: str
    accuracy_placeholder: float = 0.0
    precision_placeholder: float = 0.0
    recall_placeholder: float = 0.0
    f1_placeholder: float = 0.0
    roc_placeholder: float = 0.0
    coverage: float = 0.0
    explanation_quality: float = 0.0
    trust_stability: float = 0.0
    priority_stability: float = 0.0
    recommendation_acceptance: float = 0.0
    judge_override_rate: float = 0.0
    pipeline_completeness: float = 0.0
