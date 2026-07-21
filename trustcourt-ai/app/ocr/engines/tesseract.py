from typing import List
import random
from app.ocr.engines.base import OCREngine
from app.dto.response import OCRPage, OCRParagraph, OCRLine, OCRWord
from loguru import logger

class TesseractEngine(OCREngine):
    
    def is_available(self) -> bool:
        # Simulated availability check
        return True
        
    async def extract_text(self, image_paths: List[str], language: str = "eng") -> List[OCRPage]:
        logger.debug(f"Executing Tesseract OCR on {len(image_paths)} pages.")
        pages = []
        
        # Simulate processing images
        for idx, path in enumerate(image_paths):
            confidence = round(random.uniform(0.85, 0.99), 4)
            simulated_text = f"Tesseract extracted content from page {idx + 1}"
            
            page = OCRPage(
                pageNumber=idx + 1,
                text=simulated_text,
                width=800,
                height=1200,
                averageConfidence=confidence,
                paragraphs=[
                    OCRParagraph(
                        text=simulated_text,
                        lines=[
                            OCRLine(
                                text=simulated_text,
                                confidence=confidence,
                                words=[OCRWord(text="Tesseract", confidence=confidence)],
                                bbox=[0, 0, 100, 20]
                            )
                        ],
                        bbox=[0, 0, 100, 20]
                    )
                ]
            )
            pages.append(page)
            
        return pages

    @property
    def name(self) -> str:
        return "tesseract"
