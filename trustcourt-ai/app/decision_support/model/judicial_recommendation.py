from pydantic import BaseModel, Field
from enum import Enum

class RecommendationPriority(str, Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"

class JudicialRecommendation(BaseModel):
    recommendation_id: str
    title: str = Field(..., description="e.g. Request Additional Evidence")
    description: str
    priority: RecommendationPriority
    rule_triggered: str = Field(..., description="The rule that generated this recommendation")
    actionable: bool = True
