from abc import ABC, abstractmethod
from typing import Dict, Any, List
import networkx as nx
from app.legal.dto.domain import LegalDocument, Relationship
from loguru import logger

class KnowledgeGraphEngine(ABC):
    """
    Abstract Base Class for building the Legal Knowledge Graph.
    Ensures the pipeline is decoupled from the specific graph implementation (e.g., NetworkX, Neo4j).
    """
    
    @abstractmethod
    def build_graph(self, document: LegalDocument) -> None:
        """Constructs the graph from the LegalDocument entities and relationships."""
        pass
        
    @abstractmethod
    def add_relationship(self, relationship: Relationship) -> None:
        """Adds a specific relationship to the graph."""
        pass
        
    @abstractmethod
    def serialize(self) -> Dict[str, Any]:
        """Returns the serialized format of the graph suitable for JSON output."""
        pass

class NetworkXKnowledgeGraphEngine(KnowledgeGraphEngine):
    """
    Concrete implementation of the KnowledgeGraphEngine using NetworkX.
    Generates a deterministic in-memory graph.
    """
    
    def __init__(self):
        self.graph = nx.DiGraph()
        
    def build_graph(self, document: LegalDocument) -> None:
        logger.debug("Building NetworkX Knowledge Graph from LegalDocument")
        
        # 1. Add nodes for all entities
        for party in document.parties:
            self.graph.add_node(party.canonical_id, label="Party", name=party.canonical_name, role=party.role.value)
            
        for judge in document.judges:
            self.graph.add_node(judge.canonical_id, label="Judge", name=judge.canonical_name)
            
        for adv in document.advocates:
            self.graph.add_node(adv.canonical_id, label="Advocate", name=adv.canonical_name)
            
        for org in document.organizations:
            self.graph.add_node(org.canonical_id, label="Organization", name=org.canonical_name, type=org.org_type.value)
            
        # 2. Add edges from relationships
        for rel in document.relationships:
            self.add_relationship(rel)
            
    def add_relationship(self, relationship: Relationship) -> None:
        self.graph.add_edge(
            relationship.source_entity_id, 
            relationship.target_entity_id, 
            type=relationship.relation_type,
            confidence=relationship.confidence
        )
        
    def serialize(self) -> Dict[str, Any]:
        """Serialize the NetworkX graph to a dictionary format."""
        from networkx.readwrite import json_graph
        try:
            return json_graph.node_link_data(self.graph)
        except Exception as e:
            logger.error(f"Failed to serialize Knowledge Graph: {e}")
            return {"nodes": [], "links": []}
