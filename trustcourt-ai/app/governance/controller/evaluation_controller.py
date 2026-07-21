from fastapi import APIRouter
from app.governance.service.impl.governance_service_impl import GovernancePlatformFacade
from app.governance.dto.request.governance_request import GovernanceRequest
from typing import Dict, Any

router = APIRouter(prefix="/evaluation", tags=["Evaluation"])
facade = GovernancePlatformFacade()

@router.post("/fairness")
async def evaluate_fairness(request: GovernanceRequest):
    return facade.evaluation_engine.evaluate(request)
