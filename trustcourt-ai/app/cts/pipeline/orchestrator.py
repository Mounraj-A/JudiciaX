from app.features.dto.domain import FeatureVector
from app.cts.dto.domain import (
    CaseTrustScore, TrustReport, TrustDecisionTrace, TrustGovernance, 
    TrustLineage
)
from app.cts.config.loader import TrustConfigurationLoader
from app.cts.graph.dependency import TrustDependencyGraph
from app.cts.rules.engine import TrustRuleEngine
from app.cts.engine.readiness import ReadinessEngine
from app.cts.weights.engine import TrustWeightEngine
from app.cts.calculator.engine import TrustCalculationEngine
from app.cts.engine.confidence import TrustConfidenceService
from app.cts.governance.service import TrustDecisionGovernanceService
from app.cts.history.service import TrustHistoryService
from app.cts.history.drift import TrustDriftService
from loguru import logger
import time
import uuid

class TrustEngineService:
    """
    Orchestrates the massive, fully decoupled Case Trust Score (CTS) pipeline.
    """
    
    def __init__(self):
        self.config_loader = TrustConfigurationLoader()
        self.rule_engine = TrustRuleEngine()
        self.readiness_engine = ReadinessEngine()
        self.weight_engine = TrustWeightEngine()
        self.calc_engine = TrustCalculationEngine()
        self.confidence_svc = TrustConfidenceService()
        self.governance_svc = TrustDecisionGovernanceService()
        self.history_svc = TrustHistoryService()
        self.drift_svc = TrustDriftService(self.history_svc)
        
        self.graph = TrustDependencyGraph()
        self.graph.build_default_graph()
        
    def process(self, vector: FeatureVector) -> CaseTrustScore:
        logger.info(f"Starting CTS Calculation Pipeline for doc UUID: {vector.document_uuid}")
        start_time = time.time()
        
        config = self.config_loader.get_configuration()
        
        # Lineage Setup
        lineage = TrustLineage(
            feature_source="FeatureVector_Phase7",
            feature_uuid=str(uuid.uuid4()),
            document_source="LegalDocument_Phase6",
            document_uuid=vector.document_uuid,
            extraction_confidence=0.98,
            feature_version="1.0.0",
            pipeline_version="1.0.0"
        )
        
        trace = TrustDecisionTrace(
            configuration_version=config.version,
            evaluated_features=[],
            triggered_rules=[],
            applied_weights={},
            readiness_impact=0.0
        )
        
        governance = TrustGovernance(
            conflict_resolved_count=0,
            overrides_applied=False,
            policy_compliant=True,
            validation_messages=[]
        )
        
        # Readiness
        readiness = self.readiness_engine.evaluate(vector)
        trace.readiness_impact = readiness.normalized_score
        
        # Rule & Weight Evaluation
        triggered_rules = self.rule_engine.evaluate(vector, config.rules, trace)
        contributions = self.weight_engine.apply_weights(vector, triggered_rules, config.weights, trace)
        
        # Scoring & Profiling
        score, profile = self.calc_engine.calculate(contributions, readiness)
        
        # Governance & Confidence
        governance = self.governance_svc.validate(governance, score)
        confidence = self.confidence_svc.evaluate(vector)
        
        report = TrustReport(
            document_uuid=vector.document_uuid,
            score=score,
            readiness=readiness,
            profile=profile,
            confidence=confidence,
            governance=governance,
            decision_trace=trace,
            lineage=lineage,
            processing_time_ms=(time.time() - start_time) * 1000,
            snapshot_id="PENDING",
            history_id="PENDING"
        )
        
        # Persistence
        snapshot = self.history_svc.save_snapshot(report)
        report.snapshot_id = snapshot.snapshot_id
        
        # Drift Analysis
        drift = self.drift_svc.calculate_drift(report)
        logger.info(f"Calculated Trust Drift: {drift.drift_value} (Trend: {drift.trend})")
        
        logger.info("CTS Calculation complete.")
        
        return CaseTrustScore(
            document_uuid=vector.document_uuid,
            trust_report=report
        )
