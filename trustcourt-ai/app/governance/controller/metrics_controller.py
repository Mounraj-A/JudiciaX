from fastapi import APIRouter
from app.governance.service.impl.governance_service_impl import GovernancePlatformFacade
from app.governance.model.research_metrics import ResearchMetrics

router = APIRouter(prefix="/metrics", tags=["Metrics"])
facade = GovernancePlatformFacade()

@router.get("/dashboard", response_model=ResearchMetrics)
async def get_dashboard():
    return facade.metrics_engine.calculate_metrics()
