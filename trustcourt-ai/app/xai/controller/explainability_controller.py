from fastapi import APIRouter, Depends, HTTPException, status
from app.xai.dto.request.explain_request import ExplainRequest
from app.xai.model.explainability_report import ExplainabilityReport, ExplanationSummary
from app.xai.service.impl.explainability_engine_service_impl import ExplainabilityEngineServiceImpl
from app.xai.config.xai_config import xai_config

router = APIRouter(prefix="/xai", tags=["Explainability"])
engine_service = ExplainabilityEngineServiceImpl()

@router.post("/explain", response_model=ExplainabilityReport, status_code=status.HTTP_200_OK)
async def generate_explanation(request: ExplainRequest):
    """
    Generate a full technical explainability report for a case decision.
    """
    try:
        report = engine_service.generate_report(request)
        return report
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/explain/judge", response_model=ExplanationSummary)
async def generate_judge_explanation(request: ExplainRequest):
    """
    Generate a judge-focused explanation summary.
    """
    report = engine_service.generate_report(request)
    return report.summaries.get("Judge")

@router.post("/explain/citizen", response_model=ExplanationSummary)
async def generate_citizen_explanation(request: ExplainRequest):
    """
    Generate a citizen-friendly explanation summary.
    """
    report = engine_service.generate_report(request)
    return report.summaries.get("Citizen")

@router.get("/version")
async def get_version():
    """
    Return XAI Engine and Policy versions.
    """
    return {
        "engine_version": xai_config.engine_version,
        "policy_version": xai_config.policy_version,
        "rule_version": xai_config.rule_version
    }

# Mocking History and Report Retrieval
@router.get("/report/{case_uuid}")
async def get_report(case_uuid: str):
    return {"status": "Retrieval not implemented in this phase", "case_uuid": case_uuid}

@router.get("/history/{case_uuid}")
async def get_history(case_uuid: str):
    return {"status": "History not implemented in this phase", "case_uuid": case_uuid}
