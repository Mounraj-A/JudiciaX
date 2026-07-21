from app.governance.service.governance_services import IExperimentEngine
from app.governance.model.experiment_record import ExperimentRecord
from typing import Dict, Any
from datetime import datetime

class ExperimentEngine(IExperimentEngine):
    def start_experiment(self, config: Dict[str, Any]) -> ExperimentRecord:
        """
        Initializes an experiment context for Ablation or Robustness tracking.
        """
        return ExperimentRecord(
            experiment_id=f"EXP_{datetime.utcnow().timestamp()}",
            researcher=config.get("researcher", "System"),
            configuration=config,
            dataset_version=config.get("dataset_version", "latest"),
            pipeline_version=config.get("pipeline_version", "1.0.0"),
            status="RUNNING"
        )
