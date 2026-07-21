from app.features.dto.domain import FeatureVector
from app.jpi.dto.domain import (
    PriorityReport, PriorityDecisionTrace, PriorityGovernance,
    PriorityScore, PriorityExplanation, PriorityConfidence, JudicialPriorityIndex
)
from app.jpi.config.loader import PriorityConfigurationLoader
from app.jpi.policy.service import PriorityPolicyService
from app.jpi.rules.engine import PriorityRuleEngine
from app.jpi.rules.conflict_resolver import PriorityRuleConflictResolver
from app.jpi.weights.engine import PriorityWeightEngine
from app.jpi.calculator.engine import PriorityCalculationEngine
from app.jpi.engine.confidence import PriorityConfidenceService
from app.jpi.governance.service import PriorityDecisionGovernanceService
from app.jpi.engine.explanation import PriorityExplanationService
from app.jpi.history.service import PriorityHistoryService
from loguru import logger
import time

class PriorityEngineService:
    """
    Orchestrates the massive, fully decoupled Judicial Priority Index (JPI) pipeline.
    Transforms a strictly formatted FeatureVector into a governed PriorityReport.
    """
    
    def __init__(self):
        self.config_loader = PriorityConfigurationLoader()
        self.policy_svc = PriorityPolicyService()
        self.rule_engine = PriorityRuleEngine()
        self.conflict_resolver = PriorityRuleConflictResolver()
        self.weight_engine = PriorityWeightEngine()
        self.calc_engine = PriorityCalculationEngine()
        self.confidence_svc = PriorityConfidenceService()
        self.governance_svc = PriorityDecisionGovernanceService()
        self.explanation_svc = PriorityExplanationService()
        self.history_svc = PriorityHistoryService()
        
    def process(self, vector: FeatureVector) -> JudicialPriorityIndex:
        logger.info(f"Starting JPI Calculation Pipeline for doc UUID: {vector.document_uuid}")
        start_time = time.time()
        
        # Initialize Trace and Governance objects
        trace = PriorityDecisionTrace(
            evaluated_features=[],
            triggered_rules=[],
            applied_weights={}
        )
        governance = PriorityGovernance(
            conflict_resolved_count=0,
            overrides_applied=False,
            policy_compliant=False,
            validation_messages=[]
        )
        
        # 1. Load Configurations
        config = self.config_loader.get_configuration()
        
        # 2. Policy Enforcement
        self.policy_svc.enforce(config.policies, governance)
        
        # 3. Rule Evaluation
        triggered_rules = self.rule_engine.evaluate(vector, config.rules, trace)
        
        # 4. Conflict Resolution
        resolved_rules = self.conflict_resolver.resolve(triggered_rules, governance)
        
        # 5. Weight Application
        contributions = self.weight_engine.apply_weights(vector, resolved_rules, config.weights, trace)
        
        # 6. Scoring
        score = self.calc_engine.calculate(contributions)
        
        # 7. Governance Validation (Overrides / Threshold checks)
        governance = self.governance_svc.validate(governance, score)
        
        # 8. Confidence Check
        confidence = self.confidence_svc.evaluate(vector, trace)
        
        # 9. Explanation Building
        explanation = self.explanation_svc.build(contributions, score)
        
        # 10. Assemble Report
        report = PriorityReport(
            document_uuid=vector.document_uuid,
            score=score,
            explanation=explanation,
            confidence=confidence,
            governance=governance,
            decision_trace=trace,
            processing_time_ms=(time.time() - start_time) * 1000
        )
        
        # 11. Persistence (Snapshot & History)
        self.history_svc.save_snapshot(report)
        
        logger.info("JPI Calculation complete.")
        
        return JudicialPriorityIndex(
            document_uuid=vector.document_uuid,
            priority_report=report
        )
