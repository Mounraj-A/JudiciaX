from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.checklist_item import Checklist, ChecklistItem, ItemStatus

class ProceduralChecklistEngine:
    def generate(self, request: DecisionSupportRequest) -> Checklist:
        meta = request.case_metadata
        items = [
            ChecklistItem(item_id="PROC_01", name="Case Filed", status=ItemStatus.COMPLETED if meta.get("is_filed") else ItemStatus.PENDING, description="Initial case filing in system", action_required="File the case"),
            ChecklistItem(item_id="PROC_02", name="Documents Uploaded", status=ItemStatus.COMPLETED if meta.get("has_documents") else ItemStatus.PENDING, description="Basic required documents uploaded", action_required="Upload documents"),
            ChecklistItem(item_id="PROC_03", name="Jurisdiction Verified", status=ItemStatus.COMPLETED if meta.get("jurisdiction_verified") else ItemStatus.PENDING, description="Jurisdictional checks complete", action_required="Verify Jurisdiction"),
            ChecklistItem(item_id="PROC_04", name="Judge Assigned", status=ItemStatus.COMPLETED if meta.get("judge_assigned") else ItemStatus.PENDING, description="Case assigned to a judge", action_required="Assign judge")
        ]
        
        completed = sum(1 for i in items if i.status == ItemStatus.COMPLETED)
        return Checklist(checklist_type="Procedural", items=items, completion_percentage=(completed / len(items)) * 100)
