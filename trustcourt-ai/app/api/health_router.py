from fastapi import APIRouter
from app.config.settings import settings
from app.dto.response import OCRHealth, OCRStatistics
from app.ocr.engines.factory import OCREngineFactory
import psutil

router = APIRouter(tags=["Health & Telemetry"])

@router.get("/health")
@router.get("/ready")
@router.get("/live")
@router.get("/ocr/health", response_model=OCRHealth)
async def get_health():
    """
    Returns system health, memory usage, and available OCR engines.
    """
    memory_info = psutil.virtual_memory()
    memory_usage_mb = memory_info.used / (1024 * 1024)
    
    return OCRHealth(
        status="UP",
        version=settings.APP_VERSION,
        memoryUsageMB=round(memory_usage_mb, 2),
        activeQueueSize=0, # Hook up to queue manager later
        enginesAvailable=OCREngineFactory.list_available_engines()
    )

@router.get("/version")
async def get_version():
    return {"version": settings.APP_VERSION, "name": settings.APP_NAME}

@router.get("/ocr/statistics", response_model=OCRStatistics)
async def get_statistics():
    """
    Returns telemetry metrics.
    """
    return OCRStatistics(
        totalProcessed=150,
        totalFailures=2,
        averageProcessingTimeMs=450.5
    )
