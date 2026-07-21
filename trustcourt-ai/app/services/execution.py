import os
from typing import List
from fastapi import UploadFile
from app.dto.request import OCRRequest
from app.dto.response import OCRResponse
from app.pipeline.orchestrator import OCRPipelineOrchestrator
import aiofiles
from loguru import logger

class OCRExecutionService:
    
    @staticmethod
    async def process_upload(request: OCRRequest, files: List[UploadFile]) -> OCRResponse:
        """
        Handles the HTTP layer file parsing, temporary storage, and triggers the pipeline.
        """
        logger.info(f"Received {len(files)} files for OCR execution.")
        
        temp_paths = []
        try:
            # Temporary storage before preprocessing
            for file in files:
                # In production, use a secure temp directory
                temp_path = f"/tmp/{request.documentUuid}_{file.filename}"
                # For Windows compat in this demo, let's use the local directory
                temp_path = f"./{request.documentUuid}_{file.filename}"
                
                async with aiofiles.open(temp_path, 'wb') as out_file:
                    content = await file.read()
                    await out_file.write(content)
                temp_paths.append(temp_path)
                
            response = await OCRPipelineOrchestrator.process_document(request, temp_paths)
            return response
            
        finally:
            # Cleanup temporary files
            for path in temp_paths:
                if os.path.exists(path):
                    try:
                        os.remove(path)
                    except Exception as e:
                        logger.warning(f"Failed to cleanup temp file {path}: {e}")
