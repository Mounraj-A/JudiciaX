from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.checklist_item import Checklist, ChecklistItem, ItemStatus

class EvidenceChecklistEngine:
    def generate(self, request: DecisionSupportRequest) -> Checklist:
        meta = request.case_metadata
        items = [
            ChecklistItem(item_id="EVID_01", name="Submitted Evidence", status=ItemStatus.COMPLETED if meta.get("evidence_submitted") else ItemStatus.PENDING, description="Physical/Digital evidence submitted", action_required="Submit evidence"),
            ChecklistItem(item_id="EVID_02", name="Verified Evidence", status=ItemStatus.COMPLETED if meta.get("evidence_verified") else ItemStatus.PENDING, description="Evidence passed basic verification", action_required="Verify evidence authenticity"),
            ChecklistItem(item_id="EVID_03", name="Chain of Custody", status=ItemStatus.COMPLETED if meta.get("chain_of_custody_logged") else ItemStatus.PENDING, description="Custody logs are complete", action_required="Update custody logs")
        ]
        
        completed = sum(1 for i in items if i.status == ItemStatus.COMPLETED)
        return Checklist(checklist_type="Evidence", items=items, completion_percentage=(completed / len(items)) * 100)
