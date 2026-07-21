from pydantic import BaseModel
from enum import Enum

class QueueType(str, Enum):
    EMERGENCY = "EMERGENCY"
    REGULAR = "REGULAR"
    PENDING = "PENDING"
    ADJOURNED = "ADJOURNED"
    SPECIAL = "SPECIAL"

class CaseQueueRecommendation(BaseModel):
    recommended_queue: QueueType
    priority_position: int
    reasoning: str
