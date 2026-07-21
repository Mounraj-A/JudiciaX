from pydantic import BaseModel, Field
from typing import Optional

class SchedulingRecommendation(BaseModel):
    next_hearing_date: Optional[str] = Field(None, description="Recommended Next Hearing Date (ISO Format)")
    preferred_courtroom: Optional[str] = None
    estimated_duration_minutes: int = 30
    conflict_detected: bool = False
    conflict_reason: Optional[str] = None
