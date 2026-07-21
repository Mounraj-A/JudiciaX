from typing import List, Dict, Any
from app.xai.service.explainability_services import IExplainabilityEngineService
from app.xai.dto.request.explain_request import ExplainRequest
from app.xai.model.explainability_report import ExplainabilityReport, ExplanationSummary
from app.xai.pipeline.xai_pipeline_orchestrator import XAIPipelineOrchestrator
from app.xai.explainer.judge_explainer import JudgeExplainer
from app.xai.explainer.citizen_explainer import CitizenExplainer
from app.xai.explainer.technical_explainer import TechnicalExplainer
from app.xai.audit.xai_audit import XAIAuditService, XAIAuditEvent

class ExplainabilityEngineServiceImpl(IExplainabilityEngineService):
    def __init__(self):
        self.orchestrator = XAIPipelineOrchestrator()
        self.judge_explainer = JudgeExplainer()
        self.citizen_explainer = CitizenExplainer()
        self.tech_explainer = TechnicalExplainer()

    def generate_report(self, request: ExplainRequest) -> ExplainabilityReport:
        # Run standard generation pipeline
        report = self.orchestrator.execute(request)
        
        # Prepare context data for explainers
        context_data = {
            "jpi": report.jpi_score,
            "cts": report.cts_score,
            "top_features": [f.feature_name for f in report.feature_importance[:3]]
        }
        
        # Add Persona Summaries
        report.summaries["Judge"] = ExplanationSummary(
            persona="Judge",
            summary_text=self.judge_explainer.generate(context_data)
        )
        
        report.summaries["Citizen"] = ExplanationSummary(
            persona="Citizen",
            summary_text=self.citizen_explainer.generate(context_data)
        )
        
        report.summaries["Technical"] = ExplanationSummary(
            persona="Technical",
            summary_text=self.tech_explainer.generate(context_data)
        )
        
        XAIAuditService.log_event(XAIAuditEvent.REPORT_CREATED, request.case_uuid)
        XAIAuditService.log_event(XAIAuditEvent.XAI_COMPLETED, request.case_uuid)
        
        return report
