from app.decision_support.service.decision_support_services import IDecisionSupportService
from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.decision_support_report import DecisionSupportReport
from app.decision_support.pipeline.decision_support_pipeline import DecisionSupportPipeline

class DecisionSupportServiceImpl(IDecisionSupportService):
    def __init__(self):
        self.pipeline = DecisionSupportPipeline()

    def analyze_case(self, request: DecisionSupportRequest) -> DecisionSupportReport:
        """
        Executes the full Decision Support Pipeline to generate a consolidated report.
        """
        return self.pipeline.execute(request)
