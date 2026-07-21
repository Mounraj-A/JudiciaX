from typing import Dict, Any, Optional
from pydantic import BaseModel

class ReasoningNode(BaseModel):
    id: str
    type: str
    label: str
    metadata: Dict[str, Any] = {}
    
class ReasoningEdge(BaseModel):
    source_id: str
    target_id: str
    relationship: str
    weight: float = 1.0
