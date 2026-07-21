from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional
from app.legal.dto.domain import LegalDocument
from app.legal.pipeline.orchestrator import LegalExtractionPipelineService
from loguru import logger
import uuid

router = APIRouter(prefix="/legal", tags=["Legal Extraction"])

pipeline_service = LegalExtractionPipelineService()

class ExtractionRequest(BaseModel):
    documentUuid: str
    ocrText: str
    languageHint: Optional[str] = "en"

@router.post("/extract", response_model=LegalDocument)
async def extract_legal_document(request: ExtractionRequest):
    """
    Executes the full Enterprise Legal Information Extraction Engine pipeline.
    Takes raw OCR text and converts it into a structured, validated LegalDocument.
    """
    if not request.ocrText or not request.ocrText.strip():
        raise HTTPException(status_code=400, detail="OCR text cannot be empty.")
        
    try:
        # In a real system, the text might be fetched from a storage blob using documentUuid.
        # For this phase, we accept it in the payload.
        doc = pipeline_service.process_ocr_text(request.documentUuid, request.ocrText)
        return doc
    except Exception as e:
        logger.error(f"Failed to process legal extraction for {request.documentUuid}: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error during Legal Extraction.")

@router.get("/version")
async def get_version():
    return {"module": "Legal Information Extraction Engine", "version": "1.0.0"}
