from app.jpi.dto.domain import PriorityExplanation, PriorityContribution, PriorityScore
from typing import List
from loguru import logger

class PriorityExplanationService:
    """
    Builds the explanation payload detailing why a specific priority score was generated.
    """
    
    def build(self, contributions: List[PriorityContribution], score: PriorityScore) -> PriorityExplanation:
        logger.info("Running PriorityExplanationService")
        
        reasons = []
        for c in contributions:
            reasons.append(f"Triggered by {c.feature_name} with weight {c.applied_weight} (Rule {c.applied_rule_id}) contributing {c.percentage_contribution:.1f}%")
            
        return PriorityExplanation(
            generated_because=reasons,
            contributions=contributions
        )
