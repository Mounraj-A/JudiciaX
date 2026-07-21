from pydantic import BaseModel, Field

class WeightContribution(BaseModel):
    feature_name: str
    configured_weight: float = Field(..., description="The base weight configured in the system")
    applied_weight: float = Field(..., description="The actual weight applied after dynamic adjustment")
    normalized_weight: float = Field(..., description="Weight normalized between 0 and 1")
    weight_percentage: float = Field(..., description="Percentage contribution of this weight to the total score")
    weight_impact: str = Field(..., description="Human readable explanation of the weight's impact")
