from app.cts.dto.domain import (
    TrustContribution, TrustScore, TrustLevel, TrustProfile, TrustSection, ReadinessScore
)
from typing import List
from loguru import logger

class TrustCalculationEngine:
    """
    Calculates the TrustProfile and Final Trust Score merging Readiness.
    """
    
    def calculate(self, contributions: List[TrustContribution], readiness: ReadinessScore) -> tuple[TrustScore, TrustProfile]:
        logger.info("Running TrustCalculationEngine")
        
        raw_score = sum(c.intermediate_score for c in contributions)
        
        # Build Trust Profile (Mocking the sections based on raw score for demo)
        def _build_section(base_val: float) -> TrustSection:
            val = min(max(base_val, 0), 100)
            status = "PASS" if val > 60 else "WARN" if val > 40 else "FAIL"
            return TrustSection(score=val, confidence=0.9, status=status, reason="Calculated")

        profile = TrustProfile(
            overall_trust=_build_section(raw_score),
            document_trust=_build_section(raw_score * 0.8),
            evidence_trust=_build_section(raw_score * 0.9),
            identity_trust=_build_section(100.0), # assumed verified
            integrity_trust=_build_section(raw_score * 0.95),
            procedural_trust=_build_section(readiness.normalized_score),
            workflow_trust=_build_section(readiness.normalized_score),
            ai_readiness=_build_section(raw_score),
            verification_trust=_build_section(85.0),
            metadata_trust=_build_section(90.0)
        )
        
        # Blend Trust and Readiness (e.g. 70% Trust, 30% Readiness)
        normalized_score = min((raw_score * 0.7) + (readiness.normalized_score * 0.3), 100.0)
        
        # Calculate percentage contributions
        if raw_score > 0:
            for c in contributions:
                c.percentage_contribution = (c.intermediate_score / raw_score) * 100.0
                
        level = TrustLevel.VERY_LOW
        if normalized_score >= 90:
            level = TrustLevel.VERY_HIGH
        elif normalized_score >= 70:
            level = TrustLevel.HIGH
        elif normalized_score >= 40:
            level = TrustLevel.MEDIUM
        elif normalized_score >= 20:
            level = TrustLevel.LOW
            
        logger.info(f"Final Trust Score: {normalized_score} -> Level: {level}")
        
        score = TrustScore(
            raw_score=raw_score,
            weighted_score=raw_score, 
            normalized_score=normalized_score,
            level=level
        )
        
        return score, profile
