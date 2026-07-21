from typing import Dict, Any
from app.xai.service.explainability_services import IJudgeExplanationService

class JudgeExplainer(IJudgeExplanationService):
    def generate(self, data: Dict[str, Any]) -> str:
        """
        Generates a judge-focused explanation focusing on rules, precedents, and legal logic.
        """
        # In a full implementation, this uses an LLM or template engine.
        # This is a deterministic structural generation.
        priority_level = "HIGH" if data.get("jpi", 0) > 75 else "MEDIUM"
        top_features = data.get("top_features", [])
        
        feature_str = ", ".join(top_features) if top_features else "various factors"
        return f"This case has been assigned a {priority_level} priority because it involves {feature_str}. The decision aligns with the triggered legal rules and precedents, ensuring procedural readiness."
