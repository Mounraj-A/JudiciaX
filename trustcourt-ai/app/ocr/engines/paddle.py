from typing import List
import random
from app.ocr.engines.base import OCREngine
from app.dto.response import OCRPage, OCRParagraph, OCRLine
from loguru import logger

class PaddleOCREngine(OCREngine):
    
    def is_available(self) -> bool:
        # Simulated check
        return True
        
    async def extract_text(self, image_paths: List[str], language: str = "eng") -> List[OCRPage]:
        logger.debug(f"Executing PaddleOCR on {len(image_paths)} pages.")
        pages = []
        
        for idx, path in enumerate(image_paths):
            confidence = round(random.uniform(0.90, 0.99), 4)
            simulated_text = f"PaddleOCR extracted content from page {idx + 1}"
            
            page = OCRPage(
                pageNumber=idx + 1,
                text=simulated_text,
                width=800,
                height=1200,
                averageConfidence=confidence,
                paragraphs=[
                    OCRParagraph(text=simulated_text, lines=[OCRLine(text=simulated_text, confidence=confidence)])
                ]
            )
            pages.append(page)
            
        return pages

    @property
    def name(self) -> str:
        return "paddleocr"
