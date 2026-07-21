from typing import List
from app.dto.response import OCRPage, OCRValidation
from loguru import logger

class OCRValidator:
    
    MINIMUM_CONFIDENCE_THRESHOLD = 0.80
    
    @classmethod
    def validate_results(cls, pages: List[OCRPage]) -> OCRValidation:
        """
        Validates the extracted OCR data against enterprise thresholds.
        """
        logger.debug("Validating OCR extraction results")
        
        if not pages:
            return OCRValidation(
                isValid=False,
                missingPages=[],
                unreadablePages=[],
                confidenceScore=0.0,
                issues=["No pages extracted."]
            )
            
        unreadable_pages = []
        total_confidence = 0.0
        
        for page in pages:
            total_confidence += page.averageConfidence
            if page.averageConfidence < cls.MINIMUM_CONFIDENCE_THRESHOLD:
                unreadable_pages.append(page.pageNumber)
                
        avg_confidence = total_confidence / len(pages)
        is_valid = len(unreadable_pages) == 0 and avg_confidence >= cls.MINIMUM_CONFIDENCE_THRESHOLD
        
        issues = []
        if len(unreadable_pages) > 0:
            issues.append(f"Pages {unreadable_pages} have confidence below {cls.MINIMUM_CONFIDENCE_THRESHOLD}")
            
        return OCRValidation(
            isValid=is_valid,
            missingPages=[],
            unreadablePages=unreadable_pages,
            confidenceScore=avg_confidence,
            issues=issues
        )
