from typing import Dict, List
from loguru import logger

class TrustDependencyGraph:
    """
    Abstract representation of trust factor dependencies without tying strictly to NetworkX.
    """
    
    def __init__(self):
        self.nodes = {}
        self.edges = []
        
    def add_node(self, node_id: str, attributes: dict):
        self.nodes[node_id] = attributes
        
    def add_edge(self, from_node: str, to_node: str, weight: float = 1.0):
        self.edges.append({"from": from_node, "to": to_node, "weight": weight})
        
    def build_default_graph(self):
        logger.info("Building default TrustDependencyGraph")
        self.add_node("ocr_confidence", {"type": "source"})
        self.add_node("document_quality", {"type": "intermediate"})
        self.add_node("evidence_quality", {"type": "intermediate"})
        self.add_node("evidence_trust", {"type": "sink"})
        self.add_node("overall_trust", {"type": "sink"})
        
        self.add_edge("ocr_confidence", "document_quality")
        self.add_edge("document_quality", "evidence_quality")
        self.add_edge("evidence_quality", "evidence_trust")
        self.add_edge("evidence_trust", "overall_trust")
        
    def get_dependencies(self, node_id: str) -> List[str]:
        return [edge["from"] for edge in self.edges if edge["to"] == node_id]
