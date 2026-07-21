from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional, Dict
from app.features.dto.domain import FeatureVector
from app.jpi.dto.domain import JudicialPriorityIndex, PrioritySimulation
from app.jpi.pipeline.orchestrator import PriorityEngineService
from app.jpi.simulation.service import PrioritySimulationService
from loguru import logger

router = APIRouter(prefix="/jpi", tags=["Judicial Priority Index"])

engine_svc = PriorityEngineService()
sim_svc = PrioritySimulationService(engine_svc)

@router.post("/calculate", response_model=JudicialPriorityIndex)
async def calculate_priority(vector: FeatureVector):
    """
    Calculates the deterministic Judicial Priority Index based on a FeatureVector.
    """
    try:
        return engine_svc.process(vector)
    except Exception as e:
        logger.error(f"Failed to calculate JPI: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error during JPI calculation.")

class SimulationRequest(BaseModel):
    scenario_name: str
    overrides: Dict[str, float]
    base_vector: FeatureVector

@router.post("/simulate", response_model=PrioritySimulation)
async def simulate_priority(request: SimulationRequest):
    """
    Executes a 'What-If' simulation without storing the result in official history.
    """
    try:
        return sim_svc.simulate(request.base_vector, request.scenario_name, request.overrides)
    except Exception as e:
        logger.error(f"Failed to simulate JPI: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error during JPI simulation.")

@router.get("/snapshot/{snapshot_id}")
async def get_snapshot(snapshot_id: str):
    snapshot = engine_svc.history_svc.get_snapshot(snapshot_id)
    if not snapshot:
        raise HTTPException(status_code=404, detail="Snapshot not found")
    return snapshot

@router.get("/history/{document_uuid}")
async def get_history(document_uuid: str):
    history = engine_svc.history_svc.get_history(document_uuid)
    return {"document_uuid": document_uuid, "history": history}

@router.get("/version/info")
async def get_version():
    return {"module": "Judicial Priority Index Engine", "version": "1.0.0"}
