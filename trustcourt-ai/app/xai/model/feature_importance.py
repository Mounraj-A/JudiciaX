from pydantic import BaseModel, Field
from enum import Enum

class ContributionType(str, Enum):
    POSITIVE = "POSITIVE"
    NEGATIVE = "NEGATIVE"
    NEUTRAL = "NEUTRAL"

class FeatureImportance(BaseModel):
    feature_name: str
    feature_value: str
    contribution_type: ContributionType
    contribution_percentage: float = Field(..., ge=0.0, le=100.0)
    contribution_rank: int
    description: str = Field(..., description="Human readable reason why this feature was important")
