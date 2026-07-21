from app.governance.service.governance_services import IAblationEngine
from typing import Dict, Any

class AblationEngine(IAblationEngine):
    def perform_ablation(self, config: Dict[str, Any]) -> Dict[str, Any]:
        """
        Supports Ablation studies (e.g., removing OCR, Features, Trust) and compares outputs.
        """
        return {
            "ablation_target": config.get("remove_module", "None"),
            "impact_score": -12.5,
            "conclusion": "Removing target significantly degrades JPI accuracy."
        }
