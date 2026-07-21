from app.features.dto.domain import FeatureVector
from app.cts.dto.domain import ReadinessScore, ReadinessLevel
from loguru import logger

class ReadinessEngine:
    """
    Evaluates procedural and workflow readiness independently of Trust.
    """
    
    def evaluate(self, vector: FeatureVector) -> ReadinessScore:
        logger.info("Running ReadinessEngine")
        
        # In a real system, we look at workflow statuses.
        # Here we mock the computation based on feature completeness stats.
        completeness = vector.statistics.completeness_score if vector.statistics else 0.5
        
        raw_score = completeness * 100.0
        normalized = min(max(raw_score, 0.0), 100.0)
        
        level = ReadinessLevel.NOT_READY
        if normalized >= 85:
            level = ReadinessLevel.READY
        elif normalized >= 50:
            level = ReadinessLevel.PARTIAL
            
        logger.info(f"Readiness Score: {normalized} -> Level: {level}")
        
        return ReadinessScore(
            raw_score=raw_score,
            normalized_score=normalized,
            level=level
        )
