from fastapi import APIRouter
from app.governance.service.impl.governance_service_impl import GovernancePlatformFacade
from app.governance.dto.request.governance_request import GovernanceRequest

router = APIRouter(prefix="/governance", tags=["Governance"])
facade = GovernancePlatformFacade()

@router.post("/validate")
async def validate_pipeline(request: GovernanceRequest):
    is_valid = facade.governance_engine.enforce(request)
    return {"status": "VALID" if is_valid else "INVALID"}
