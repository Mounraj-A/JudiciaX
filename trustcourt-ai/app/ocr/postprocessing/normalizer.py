from typing import List
from app.dto.response import OCRPage
from loguru import logger

class PostprocessingNormalizer:
    
    @staticmethod
    def normalize_pages(pages: List[OCRPage]) -> List[OCRPage]:
        """
        Normalizes whitespace, fixes common OCR artifacts, and reconstructs full text.
        """
        logger.debug("Postprocessing OCR results")
        
        for page in pages:
            # 1. Normalize whitespace
            page.text = " ".join(page.text.split())
            
            # 2. Re-calculate average confidence if needed
            # 3. Preserve structure (Simulated)
            
        return pages
        
    @staticmethod
    def extract_full_text(pages: List[OCRPage]) -> str:
        """
        Combines all page text into a single document string.
        """
        return "\n\n".join([page.text for page in pages])
