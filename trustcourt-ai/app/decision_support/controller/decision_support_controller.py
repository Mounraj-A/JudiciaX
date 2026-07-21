from fastapi import APIRouter, HTTPException, status
from app.decision_support.dto.request.decision_support_request import DecisionSupportRequest
from app.decision_support.model.decision_support_report import DecisionSupportReport
from app.decision_support.service.impl.decision_support_service_impl import DecisionSupportServiceImpl
from app.decision_support.config.jdse_config import jdse_config

router = APIRouter(prefix="/decision-support", tags=["Decision Support"])
service = DecisionSupportServiceImpl()

@router.post("/analyze", response_model=DecisionSupportReport, status_code=status.HTTP_200_OK)
async def analyze_case(request: DecisionSupportRequest):
    """
    Executes the Decision Support pipeline to generate checklists, recommendations, and readiness.
    """
    try:
        report = service.analyze_case(request)
        return report
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/{case_uuid}")
async def get_report(case_uuid: str):
    """
    Retrieve a previously generated decision support report.
    """
    return {"status": "Retrieval not implemented in this phase", "case_uuid": case_uuid}

@router.get("/checklist/{case_uuid}")
async def get_checklist(case_uuid: str):
    """
    Retrieve only the checklists for a case.
    """
    return {"status": "Checklist retrieval not implemented in this phase", "case_uuid": case_uuid}

@router.get("/recommendations/{case_uuid}")
async def get_recommendations(case_uuid: str):
    """
    Retrieve only the judicial recommendations for a case.
    """
    return {"status": "Recommendations retrieval not implemented in this phase", "case_uuid": case_uuid}

@router.get("/readiness/{case_uuid}")
async def get_readiness(case_uuid: str):
    """
    Retrieve only the case readiness status.
    """
    return {"status": "Readiness retrieval not implemented in this phase", "case_uuid": case_uuid}

@router.get("/version")
async def get_version():
    """
    Return JDSE Engine and Policy versions.
    """
    return {
        "engine_version": jdse_config.engine_version,
        "policy_version": jdse_config.policy_version
    }
