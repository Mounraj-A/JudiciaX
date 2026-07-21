from app.features.dto.domain import FeatureVector
from app.cts.dto.domain import TrustSimulation
from app.cts.pipeline.orchestrator import TrustEngineService
from loguru import logger
import copy

class TrustSimulationService:
    """
    Executes a What-If analysis by overriding features and re-running the CTS engine,
    without persisting to the main history.
    """
    
    def __init__(self, engine_svc: TrustEngineService):
        self.engine_svc = engine_svc
        
    def simulate(self, base_vector: FeatureVector, scenario: str, feature_overrides: dict) -> TrustSimulation:
        logger.info(f"Running TrustSimulationService for scenario: {scenario}")
        
        sim_vector = copy.deepcopy(base_vector)
        
        for feature_name, value in feature_overrides.items():
            for group in [sim_vector.document_features, sim_vector.case_features]:
                if feature_name in group.features:
                    group.features[feature_name] = value
                    
        # Rerun pipeline (In production, disable history saving during sim)
        cts = self.engine_svc.process(sim_vector)
        
        return TrustSimulation(
            what_if_scenario=scenario,
            simulated_report=cts.trust_report
        )
