from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Dict
from app.features.dto.domain import FeatureVector
from app.cts.dto.domain import CaseTrustScore, TrustSimulation
from app.cts.pipeline.orchestrator import TrustEngineService
from app.cts.simulation.service import TrustSimulationService
from loguru import logger

router = APIRouter(prefix="/cts", tags=["Case Trust Score"])

engine_svc = TrustEngineService()
sim_svc = TrustSimulationService(engine_svc)

@router.post("/calculate", response_model=CaseTrustScore)
async def calculate_trust(vector: FeatureVector):
    """
    Calculates the deterministic Case Trust Score based on a FeatureVector.
    """
    try:
        return engine_svc.process(vector)
    except Exception as e:
        logger.error(f"Failed to calculate CTS: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error during CTS calculation.")

class SimulationRequest(BaseModel):
    scenario_name: str
    overrides: Dict[str, float]
    base_vector: FeatureVector

@router.post("/simulate", response_model=TrustSimulation)
async def simulate_trust(request: SimulationRequest):
    """
    Executes a 'What-If' simulation without storing the result in official history.
    """
    try:
        return sim_svc.simulate(request.base_vector, request.scenario_name, request.overrides)
    except Exception as e:
        logger.error(f"Failed to simulate CTS: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error during CTS simulation.")

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
    return {"module": "Case Trust Score Engine", "version": "1.0.0"}
