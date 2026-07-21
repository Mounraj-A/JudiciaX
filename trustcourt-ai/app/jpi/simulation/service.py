from app.features.dto.domain import FeatureVector
from app.jpi.dto.domain import PrioritySimulation
from app.jpi.pipeline.orchestrator import PriorityEngineService
from loguru import logger
import copy

class PrioritySimulationService:
    """
    Executes a What-If analysis by overriding features and re-running the JPI engine,
    without persisting to the main history.
    """
    
    def __init__(self, engine_svc: PriorityEngineService):
        self.engine_svc = engine_svc
        
    def simulate(self, base_vector: FeatureVector, scenario: str, feature_overrides: dict) -> PrioritySimulation:
        logger.info(f"Running PrioritySimulationService for scenario: {scenario}")
        
        # Deep copy to ensure we don't mutate original
        sim_vector = copy.deepcopy(base_vector)
        
        # Apply overrides
        for feature_name, value in feature_overrides.items():
            # Simplistic lookup for simulation demo
            for group in [sim_vector.medical_features, sim_vector.party_features]:
                if feature_name in group.features:
                    group.features[feature_name] = value
                    
        # Rerun pipeline
        jpi = self.engine_svc.process(sim_vector)
        
        # (In a real system we'd disable the history save inside process for simulations)
        
        return PrioritySimulation(
            what_if_scenario=scenario,
            simulated_report=jpi.priority_report
        )
