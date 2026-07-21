from fastapi import APIRouter
from app.governance.service.impl.governance_service_impl import GovernancePlatformFacade
from app.governance.dto.request.governance_request import GovernanceRequest
from app.governance.model.benchmark_result import BenchmarkResult

router = APIRouter(prefix="/benchmark", tags=["Benchmark"])
facade = GovernancePlatformFacade()

@router.post("/system", response_model=BenchmarkResult)
async def measure_system(request: GovernanceRequest):
    return facade.benchmark_engine.measure(request)
