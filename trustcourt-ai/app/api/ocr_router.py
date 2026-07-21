from fastapi import APIRouter, UploadFile, File, Form, HTTPException, Depends
from typing import List, Optional
from app.dto.request import OCRRequest
from app.dto.response import OCRResponse
from app.services.execution import OCRExecutionService
from loguru import logger

router = APIRouter(prefix="/ocr", tags=["OCR Execution"])

@router.post("/process", response_model=OCRResponse)
async def process_ocr(
    documentUuid: str = Form(...),
    correlationId: str = Form(...),
    engine: Optional[str] = Form(None),
    language: Optional[str] = Form("eng"),
    files: List[UploadFile] = File(...)
):
    """
    Execute full OCR extraction on a document.
    """
    if not files:
        raise HTTPException(status_code=400, detail="No files provided for OCR processing.")
        
    request = OCRRequest(
        documentUuid=documentUuid,
        correlationId=correlationId,
        engine=engine,
        language=language
    )
    
    try:
        response = await OCRExecutionService.process_upload(request, files)
        return response
    except Exception as e:
        logger.error(f"Error processing OCR request: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error during OCR execution.")

@router.post("/process-batch")
async def process_batch_ocr():
    # Placeholder for batch processing (async queue)
    return {"message": "Batch processing queued."}

@router.post("/validate")
async def validate_ocr():
    # Placeholder for validation endpoint
    return {"message": "Validation complete."}

@router.post("/preprocess")
async def preprocess_image():
    # Placeholder for preprocessing endpoint
    return {"message": "Preprocessing complete."}

