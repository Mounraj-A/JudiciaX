from pydantic import BaseModel, Field
from enum import Enum

class ItemStatus(str, Enum):
    COMPLETED = "COMPLETED"
    PENDING = "PENDING"
    NOT_APPLICABLE = "NOT_APPLICABLE"

class ChecklistItem(BaseModel):
    item_id: str
    name: str
    status: ItemStatus
    description: str
    action_required: str = Field("", description="Action to take if pending")

class Checklist(BaseModel):
    checklist_type: str = Field(..., description="Procedural, Document, or Evidence")
    items: list[ChecklistItem] = Field(default_factory=list)
    completion_percentage: float = 0.0
