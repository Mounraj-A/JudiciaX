from typing import List
from app.dto.request import OCRRequest
from app.dto.response import OCRResponse
from app.ocr.engines.factory import OCREngineFactory
from app.ocr.preprocessing.pipeline import PreprocessingPipeline
from app.ocr.postprocessing.normalizer import PostprocessingNormalizer
from app.ocr.validation.validator import OCRValidator
from loguru import logger
import time
import uuid

class OCRPipelineOrchestrator:
    
    @classmethod
    async def process_document(cls, request: OCRRequest, image_paths: List[str]) -> OCRResponse:
        """
        Orchestrates the full OCR extraction lifecycle:
        1. Preprocessing
        2. Engine Extraction
        3. Postprocessing
        4. Validation
        """
        start_time = time.time()
        logger.info(f"Starting OCR Pipeline for document {request.documentUuid}")
        
        # 1. Preprocessing
        processed_paths = [PreprocessingPipeline.process_image(p) for p in image_paths]
        
        # 2. Engine Extraction
        engine = OCREngineFactory.get_engine(request.engine)
        pages = await engine.extract_text(processed_paths, request.language)
        
        # 3. Postprocessing
        normalized_pages = PostprocessingNormalizer.normalize_pages(pages)
        full_text = PostprocessingNormalizer.extract_full_text(normalized_pages)
        
        # 4. Validation
        validation = OCRValidator.validate_results(normalized_pages)
        
        total_time_ms = (time.time() - start_time) * 1000
        logger.info(f"Completed OCR Pipeline in {total_time_ms:.2f}ms. Valid: {validation.isValid}")
        
        return OCRResponse(
            requestId=str(uuid.uuid4()),
            correlationId=request.correlationId,
            documentUuid=request.documentUuid,
            engineUsed=engine.name,
            language=request.language,
            totalTimeMs=total_time_ms,
            averageConfidence=validation.confidenceScore,
            pages=normalized_pages,
            fullText=full_text,
            validation=validation,
            warnings=validation.issues
        )
