from pydantic import BaseModel, Field
from typing import List, Dict, Any

class NodeData(BaseModel):
    id: str
    label: str
    node_type: str = Field(..., description="E.g., Feature, Rule, Weight, Score")
    attributes: Dict[str, Any] = Field(default_factory=dict)

class EdgeData(BaseModel):
    source: str
    target: str
    relationship: str = Field(..., description="E.g., triggers, applies, yields")
    weight: float = 1.0

class ReasoningGraph(BaseModel):
    case_uuid: str
    nodes: List[NodeData] = Field(default_factory=list)
    edges: List[EdgeData] = Field(default_factory=list)
    
    def to_networkx_format(self) -> Dict[str, Any]:
        """Future ready for NetworkX import"""
        return {
            "nodes": [n.dict() for n in self.nodes],
            "links": [e.dict() for e in self.edges]
        }
