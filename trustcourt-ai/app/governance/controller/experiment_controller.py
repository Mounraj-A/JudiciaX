from fastapi import APIRouter
from app.governance.service.impl.governance_service_impl import GovernancePlatformFacade
from app.governance.model.experiment_record import ExperimentRecord
from typing import Dict, Any

router = APIRouter(prefix="/experiment", tags=["Experiment"])
facade = GovernancePlatformFacade()

@router.post("/ablation", response_model=ExperimentRecord)
async def trigger_ablation(config: Dict[str, Any]):
    return facade.experiment_engine.start_experiment(config)
