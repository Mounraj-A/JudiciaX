from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.decision_support_report import DecisionSupportReport
from app.decision_support.engine.case_readiness_engine import CaseReadinessEngine
from app.decision_support.engine.judicial_recommendation_engine import JudicialRecommendationEngine
from app.decision_support.engine.risk_assessment_engine import RiskAssessmentEngine
from app.decision_support.engine.scheduling_recommendation_engine import SchedulingRecommendationEngine
from app.decision_support.engine.case_queue_engine import CaseQueueEngine
from app.decision_support.engine.workload_balancing_engine import WorkloadBalancingEngine
from app.decision_support.checklist.procedural_checklist_engine import ProceduralChecklistEngine
from app.decision_support.checklist.document_checklist_engine import DocumentChecklistEngine
from app.decision_support.checklist.evidence_checklist_engine import EvidenceChecklistEngine
from app.decision_support.builder.decision_support_builder import DecisionSupportBuilder
from app.decision_support.audit.jdse_audit import JDSEAuditService, JDSEAuditEvent

class DecisionSupportPipeline:
    def __init__(self):
        self.readiness_engine = CaseReadinessEngine()
        self.recommendation_engine = JudicialRecommendationEngine()
        self.risk_engine = RiskAssessmentEngine()
        self.scheduling_engine = SchedulingRecommendationEngine()
        self.queue_engine = CaseQueueEngine()
        self.workload_engine = WorkloadBalancingEngine()
        
        self.procedural_chk = ProceduralChecklistEngine()
        self.doc_chk = DocumentChecklistEngine()
        self.ev_chk = EvidenceChecklistEngine()

    def execute(self, request: DecisionSupportRequest) -> DecisionSupportReport:
        case_uuid = request.case_uuid
        JDSEAuditService.log_event(JDSEAuditEvent.DECISION_SUPPORT_STARTED, case_uuid)
        
        builder = DecisionSupportBuilder(case_uuid)
        
        # 1. Checklists
        proc_chk = self.procedural_chk.generate(request)
        doc_chk = self.doc_chk.generate(request)
        ev_chk = self.ev_chk.generate(request)
        builder.set_checklists(proc_chk, doc_chk, ev_chk)
        JDSEAuditService.log_event(JDSEAuditEvent.CHECKLIST_GENERATED, case_uuid)
        
        # 2. Case Readiness
        readiness = self.readiness_engine.evaluate(
            request, 
            procedural_pct=proc_chk.completion_percentage,
            doc_pct=doc_chk.completion_percentage,
            ev_pct=ev_chk.completion_percentage
        )
        builder.set_readiness(readiness)
        JDSEAuditService.log_event(JDSEAuditEvent.READINESS_GENERATED, case_uuid)
        
        # 3. Recommendations
        recs = self.recommendation_engine.generate(request, readiness)
        builder.add_recommendations(recs)
        JDSEAuditService.log_event(JDSEAuditEvent.RECOMMENDATIONS_GENERATED, case_uuid)
        
        # 4. Risk Assessment
        risks = self.risk_engine.assess(request)
        builder.add_risks(risks)
        JDSEAuditService.log_event(JDSEAuditEvent.RISK_ANALYZED, case_uuid)
        
        # 5. Scheduling & Queue
        scheduling = self.scheduling_engine.analyze(request)
        queue = self.queue_engine.optimize(request)
        builder.set_scheduling(scheduling).set_queue(queue)
        JDSEAuditService.log_event(JDSEAuditEvent.QUEUE_ANALYZED, case_uuid)
        
        # 6. Workload (assuming Judge ID is in metadata)
        judge_id = request.case_metadata.get("judge_assigned", "UNASSIGNED")
        if judge_id != "UNASSIGNED":
            workload = self.workload_engine.analyze(judge_id)
            builder.set_workload(workload)
            
        # Build Report
        report = builder.build()
        JDSEAuditService.log_event(JDSEAuditEvent.REPORT_GENERATED, case_uuid)
        JDSEAuditService.log_event(JDSEAuditEvent.DECISION_SUPPORT_COMPLETED, case_uuid)
        
        return report
