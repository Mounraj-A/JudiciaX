from typing import Dict, Any
from app.xai.dto.request.explain_request import ExplainRequest
from app.xai.model.explainability_report import ExplainabilityReport
from app.xai.engine.feature_importance_engine import FeatureImportanceEngine
from app.xai.engine.rule_explanation_engine import RuleExplanationEngine
from app.xai.engine.weight_explanation_engine import WeightExplanationEngine
from app.xai.builder.reasoning_graph_builder import ReasoningGraphBuilder
from app.xai.model.unified_decision_trace import UnifiedDecisionTrace, TraceStep
from app.xai.audit.xai_audit import XAIAuditService, XAIAuditEvent

class XAIPipelineOrchestrator:
    def __init__(self):
        self.feature_engine = FeatureImportanceEngine()
        self.rule_engine = RuleExplanationEngine()
        self.weight_engine = WeightExplanationEngine()
        self.graph_builder = ReasoningGraphBuilder()

    def execute(self, request: ExplainRequest) -> ExplainabilityReport:
        XAIAuditService.log_event(XAIAuditEvent.XAI_STARTED, request.case_uuid)
        
        # 1. Calculate Feature Importance
        feature_importance = self.feature_engine.calculate(request)
        XAIAuditService.log_event(XAIAuditEvent.FEATURE_EXPLAINED, request.case_uuid)
        
        # 2. Explain Rules
        rule_contributions = self.rule_engine.explain(request)
        XAIAuditService.log_event(XAIAuditEvent.RULE_EXPLAINED, request.case_uuid)
        
        # 3. Explain Weights
        weight_contributions = self.weight_engine.explain(request)
        XAIAuditService.log_event(XAIAuditEvent.WEIGHT_EXPLAINED, request.case_uuid)
        
        # 4. Generate Unified Decision Trace
        # Normally this involves merging complex traces, simplified here for architecture demonstration
        unified_trace = UnifiedDecisionTrace(
            case_uuid=request.case_uuid,
            initial_priority_score=0.0,
            initial_trust_score=0.0,
            final_jpi=request.priority_report.get("jpi_score", 0.0),
            final_cts=request.trust_report.get("cts_score", 0.0),
            steps=[]
        )
        # Assuming request.priority_decision_trace provides steps
        for i, step_data in enumerate(request.priority_decision_trace):
            unified_trace.steps.append(TraceStep(
                step_id=f"step_{i}",
                feature_name=step_data.get("feature", "unknown"),
                rule_applied=step_data.get("rule", "unknown"),
                weight_applied=step_data.get("weight", 0.0),
                intermediate_score=step_data.get("score", 0.0),
                confidence_at_step=step_data.get("confidence", 1.0)
            ))
        XAIAuditService.log_event(XAIAuditEvent.DECISION_TRACE_GENERATED, request.case_uuid)
            
        # 5. Build Graph
        reasoning_graph = self.graph_builder.build(unified_trace)
        
        # Build raw report (Summaries added by Service Implementation)
        report = ExplainabilityReport(
            case_uuid=request.case_uuid,
            jpi_score=unified_trace.final_jpi,
            cts_score=unified_trace.final_cts,
            overall_confidence=0.95, # Mock value, would come from ConfidenceExplanationService
            feature_importance=feature_importance,
            rule_contributions=rule_contributions,
            weight_contributions=weight_contributions,
            decision_trace=unified_trace,
            reasoning_graph=reasoning_graph,
            governance={"policy": "1.0", "engine": "1.0"}
        )
        
        return report
