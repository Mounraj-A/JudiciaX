from app.jpi.dto.domain import PriorityContribution, PriorityScore, PriorityLevel
from typing import List
from loguru import logger

class PriorityCalculationEngine:
    """
    Calculates the final Raw, Weighted, and Normalized Priority Scores and determines the Priority Level.
    """
    
    def calculate(self, contributions: List[PriorityContribution]) -> PriorityScore:
        logger.info("Running PriorityCalculationEngine")
        
        raw_score = sum(c.intermediate_score for c in contributions)
        
        # Cap the max score at 100
        normalized_score = min(raw_score, 100.0)
        
        # Calculate percentage contributions
        if raw_score > 0:
            for c in contributions:
                c.percentage_contribution = (c.intermediate_score / raw_score) * 100.0
                
        # Determine Priority Level based on configurable thresholds (hardcoded here for demo)
        level = PriorityLevel.LOW
        if normalized_score >= 90:
            level = PriorityLevel.EMERGENCY
        elif normalized_score >= 75:
            level = PriorityLevel.CRITICAL
        elif normalized_score >= 50:
            level = PriorityLevel.HIGH
        elif normalized_score >= 25:
            level = PriorityLevel.MEDIUM
            
        logger.info(f"Final Priority Score: {normalized_score} -> Level: {level}")
        
        return PriorityScore(
            raw_score=raw_score,
            weighted_score=raw_score, # Can differ if non-linear weights applied
            normalized_score=normalized_score,
            level=level
        )
