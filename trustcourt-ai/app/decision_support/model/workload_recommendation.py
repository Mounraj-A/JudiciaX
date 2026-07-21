from pydantic import BaseModel

class WorkloadRecommendation(BaseModel):
    judge_id: str
    pending_cases: int
    high_priority_cases: int
    critical_cases: int
    average_processing_time_days: float
    suggested_distribution_action: str
