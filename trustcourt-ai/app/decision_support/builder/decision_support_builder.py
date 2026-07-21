from app.decision_support.model.decision_support_report import DecisionSupportReport
from app.decision_support.model.case_readiness import CaseReadinessReport
from app.decision_support.model.judicial_recommendation import JudicialRecommendation
from app.decision_support.model.checklist_item import Checklist
from app.decision_support.model.risk_assessment import RiskAssessment
from app.decision_support.model.scheduling_recommendation import SchedulingRecommendation
from app.decision_support.model.case_queue_recommendation import CaseQueueRecommendation
from app.decision_support.model.workload_recommendation import WorkloadRecommendation
from typing import List

class DecisionSupportBuilder:
    def __init__(self, case_uuid: str):
        self.case_uuid = case_uuid
        self.readiness = None
        self.recommendations = []
        self.procedural_checklist = None
        self.document_checklist = None
        self.evidence_checklist = None
        self.risks = []
        self.scheduling = None
        self.queue_recommendation = None
        self.workload_analysis = None
        
    def set_readiness(self, readiness: CaseReadinessReport):
        self.readiness = readiness
        return self
        
    def add_recommendations(self, recs: List[JudicialRecommendation]):
        self.recommendations.extend(recs)
        return self
        
    def set_checklists(self, procedural: Checklist, document: Checklist, evidence: Checklist):
        self.procedural_checklist = procedural
        self.document_checklist = document
        self.evidence_checklist = evidence
        return self
        
    def add_risks(self, risks: List[RiskAssessment]):
        self.risks.extend(risks)
        return self
        
    def set_scheduling(self, scheduling: SchedulingRecommendation):
        self.scheduling = scheduling
        return self
        
    def set_queue(self, queue: CaseQueueRecommendation):
        self.queue_recommendation = queue
        return self
        
    def set_workload(self, workload: WorkloadRecommendation):
        self.workload_analysis = workload
        return self
        
    def build(self) -> DecisionSupportReport:
        if not self.readiness:
            raise ValueError("Readiness report is mandatory to build DecisionSupportReport.")
            
        summary = f"Case {self.case_uuid} is {self.readiness.readiness_level.value}. {len(self.recommendations)} actionable recommendations provided. {len(self.risks)} risks identified."
        
        return DecisionSupportReport(
            case_uuid=self.case_uuid,
            readiness=self.readiness,
            recommendations=self.recommendations,
            procedural_checklist=self.procedural_checklist,
            document_checklist=self.document_checklist,
            evidence_checklist=self.evidence_checklist,
            risks=self.risks,
            scheduling=self.scheduling,
            queue_recommendation=self.queue_recommendation,
            workload_analysis=self.workload_analysis,
            summary=summary
        )
