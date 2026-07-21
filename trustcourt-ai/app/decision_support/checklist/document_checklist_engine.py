from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.checklist_item import Checklist, ChecklistItem, ItemStatus

class DocumentChecklistEngine:
    def generate(self, request: DecisionSupportRequest) -> Checklist:
        meta = request.case_metadata
        items = [
            ChecklistItem(item_id="DOC_01", name="Required Documents", status=ItemStatus.COMPLETED if meta.get("required_docs_present") else ItemStatus.PENDING, description="All statutorily required documents present", action_required="Request missing documents"),
            ChecklistItem(item_id="DOC_02", name="Verification Status", status=ItemStatus.COMPLETED if meta.get("docs_verified") else ItemStatus.PENDING, description="Documents verified by clerk", action_required="Verify documents"),
            ChecklistItem(item_id="DOC_03", name="No Expired Documents", status=ItemStatus.COMPLETED if not meta.get("has_expired_docs") else ItemStatus.PENDING, description="Check for expired IDs or certificates", action_required="Renew expired documents")
        ]
        
        completed = sum(1 for i in items if i.status == ItemStatus.COMPLETED)
        return Checklist(checklist_type="Document", items=items, completion_percentage=(completed / len(items)) * 100)
