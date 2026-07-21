from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional
from app.decision_support.model.case_readiness import CaseReadinessReport
from app.decision_support.model.judicial_recommendation import JudicialRecommendation
from app.decision_support.model.checklist_item import Checklist
from app.decision_support.model.risk_assessment import RiskAssessment
from app.decision_support.model.scheduling_recommendation import SchedulingRecommendation
from app.decision_support.model.case_queue_recommendation import CaseQueueRecommendation
from app.decision_support.model.workload_recommendation import WorkloadRecommendation

class DecisionSupportReport(BaseModel):
    case_uuid: str
    readiness: CaseReadinessReport
    recommendations: List[JudicialRecommendation] = Field(default_factory=list)
    procedural_checklist: Optional[Checklist] = None
    document_checklist: Optional[Checklist] = None
    evidence_checklist: Optional[Checklist] = None
    risks: List[RiskAssessment] = Field(default_factory=list)
    scheduling: Optional[SchedulingRecommendation] = None
    queue_recommendation: Optional[CaseQueueRecommendation] = None
    workload_analysis: Optional[WorkloadRecommendation] = None
    summary: str = Field(..., description="High-level Decision Support Summary")
