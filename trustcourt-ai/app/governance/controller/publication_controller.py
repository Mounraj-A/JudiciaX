from fastapi import APIRouter
from app.governance.service.impl.governance_service_impl import GovernancePlatformFacade
from app.governance.model.publication_report import PublicationReport

router = APIRouter(prefix="/publication", tags=["Publication"])
facade = GovernancePlatformFacade()

@router.get("/report/{experiment_id}", response_model=PublicationReport)
async def generate_publication_report(experiment_id: str):
    return facade.publication_engine.generate(experiment_id)
