from fastapi import APIRouter, HTTPException
from app.legal.dto.domain import LegalDocument
from app.features.dto.domain import FeatureVector
from app.features.pipeline.orchestrator import FeaturePipelineService
from loguru import logger

router = APIRouter(prefix="/features", tags=["Feature Engineering"])
pipeline = FeaturePipelineService()

@router.post("/extract", response_model=FeatureVector)
async def extract_features(document: LegalDocument):
    """
    Transforms a strictly typed LegalDocument into a canonical FeatureVector 
    for downstream ML consumption.
    """
    if not document:
        raise HTTPException(status_code=400, detail="LegalDocument is required.")
        
    try:
        vector = pipeline.process(document)
        return vector
    except Exception as e:
        logger.error(f"Failed to process feature engineering: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error during Feature Engineering.")

@router.get("/{document_uuid}", response_model=FeatureVector)
async def get_features(document_uuid: str):
    """
    Retrieves a cached FeatureVector from the Feature Store.
    """
    record = pipeline.store_svc.get(document_uuid)
    if not record:
        raise HTTPException(status_code=404, detail="FeatureVector not found in Feature Store.")
    return record.feature_vector

@router.get("/version/info")
async def get_version():
    return {"module": "Enterprise Legal Feature Engineering Engine", "version": "1.0.0"}
