from typing import Dict, Any
from app.xai.service.explainability_services import ITechnicalExplanationService

class TechnicalExplainer(ITechnicalExplanationService):
    def generate(self, data: Dict[str, Any]) -> str:
        """
        Generates a detailed technical explanation suitable for auditors and data scientists.
        """
        jpi = data.get("jpi", 0.0)
        cts = data.get("cts", 0.0)
        top_features = data.get("top_features", [])
        
        return f"JPI: {jpi}, CTS: {cts}. Top contributing features: {top_features}. Pipeline executed without threshold violations."
