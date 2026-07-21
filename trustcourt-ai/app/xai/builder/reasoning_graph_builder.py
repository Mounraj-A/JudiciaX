from app.xai.model.unified_decision_trace import UnifiedDecisionTrace
from app.xai.model.reasoning_graph import ReasoningGraph, NodeData, EdgeData

class ReasoningGraphBuilder:
    def build(self, trace: UnifiedDecisionTrace) -> ReasoningGraph:
        """
        Builds an implementation-agnostic ReasoningGraph from the UnifiedDecisionTrace.
        Can be exported to NetworkX, Neo4j, etc.
        """
        graph = ReasoningGraph(case_uuid=trace.case_uuid)
        
        # Start node
        case_node_id = f"CASE_{trace.case_uuid}"
        graph.nodes.append(NodeData(
            id=case_node_id, 
            label="Case", 
            node_type="Entity", 
            attributes={"initial_jpi": trace.initial_priority_score}
        ))
        
        for step in trace.steps:
            feature_node_id = f"FEAT_{step.feature_name}"
            rule_node_id = f"RULE_{step.rule_applied}"
            
            # Add nodes
            graph.nodes.append(NodeData(id=feature_node_id, label=step.feature_name, node_type="Feature"))
            graph.nodes.append(NodeData(id=rule_node_id, label=step.rule_applied, node_type="Rule"))
            
            # Add edges
            graph.edges.append(EdgeData(source=case_node_id, target=feature_node_id, relationship="has_feature"))
            graph.edges.append(EdgeData(source=feature_node_id, target=rule_node_id, relationship="triggers"))
            
        # Add final score node
        score_node_id = "SCORE_FINAL"
        graph.nodes.append(NodeData(id=score_node_id, label="Final Score", node_type="Score", attributes={"jpi": trace.final_jpi, "cts": trace.final_cts}))
        
        # Connect last step to final score
        if trace.steps:
            last_rule_id = f"RULE_{trace.steps[-1].rule_applied}"
            graph.edges.append(EdgeData(source=last_rule_id, target=score_node_id, relationship="yields", weight=trace.steps[-1].weight_applied))
            
        return graph
