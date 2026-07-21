from typing import Dict, Any
from app.xai.service.explainability_services import ICitizenExplanationService

class CitizenExplainer(ICitizenExplanationService):
    def generate(self, data: Dict[str, Any]) -> str:
        """
        Generates a highly readable, human-friendly explanation free of technical jargon.
        """
        jpi = data.get("jpi", 0)
        top_features = data.get("top_features", [])
        
        urgency = "needs immediate attention" if jpi > 80 else "is processing normally"
        reasons = " and ".join(top_features[:2]) if top_features else "standard procedures"
        
        return f"Your case {urgency}. This is primarily due to {reasons}. We are committed to transparency and fairness in handling your request."
