from abc import ABC, abstractmethod
from typing import Dict, Any, List
from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.decision_support_report import DecisionSupportReport
from app.decision_support.model.case_readiness import CaseReadinessReport
from app.decision_support.model.judicial_recommendation import JudicialRecommendation
from app.decision_support.model.checklist_item import Checklist
from app.decision_support.model.risk_assessment import RiskAssessment
from app.decision_support.model.scheduling_recommendation import SchedulingRecommendation
from app.decision_support.model.case_queue_recommendation import CaseQueueRecommendation
from app.decision_support.model.workload_recommendation import WorkloadRecommendation

class ICaseReadinessService(ABC):
    @abstractmethod
    def evaluate_readiness(self, request: DecisionSupportRequest) -> CaseReadinessReport:
        pass

class IRecommendationService(ABC):
    @abstractmethod
    def generate_recommendations(self, request: DecisionSupportRequest, readiness: CaseReadinessReport) -> List[JudicialRecommendation]:
        pass

class IChecklistService(ABC):
    @abstractmethod
    def generate_procedural_checklist(self, request: DecisionSupportRequest) -> Checklist:
        pass
        
    @abstractmethod
    def generate_document_checklist(self, request: DecisionSupportRequest) -> Checklist:
        pass
        
    @abstractmethod
    def generate_evidence_checklist(self, request: DecisionSupportRequest) -> Checklist:
        pass

class ISchedulingService(ABC):
    @abstractmethod
    def analyze_scheduling(self, request: DecisionSupportRequest) -> SchedulingRecommendation:
        pass

class IQueueOptimizationService(ABC):
    @abstractmethod
    def optimize_queue(self, request: DecisionSupportRequest) -> CaseQueueRecommendation:
        pass

class IWorkloadService(ABC):
    @abstractmethod
    def analyze_workload(self, judge_id: str) -> WorkloadRecommendation:
        pass

class IRiskAssessmentService(ABC):
    @abstractmethod
    def assess_risks(self, request: DecisionSupportRequest) -> List[RiskAssessment]:
        pass

class ISimilarCaseService(ABC):
    @abstractmethod
    def find_similar_cases(self, request: DecisionSupportRequest) -> List[Dict[str, Any]]:
        pass

class IDecisionSupportService(ABC):
    @abstractmethod
    def analyze_case(self, request: DecisionSupportRequest) -> DecisionSupportReport:
        pass
