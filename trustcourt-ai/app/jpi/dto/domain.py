from pydantic import BaseModel, Field
from typing import Dict, Any, List, Optional
from datetime import datetime
from enum import Enum
import uuid

class PriorityLevel(str, Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"
    EMERGENCY = "EMERGENCY"

class PriorityRule(BaseModel):
    rule_id: str
    rule_name: str
    rule_category: str
    is_active: bool
    version: str
    description: str
    legal_reference: str

class PriorityWeight(BaseModel):
    weight_id: str
    factor_name: str
    base_weight: float
    multiplier: float = 1.0

class PriorityContribution(BaseModel):
    feature_name: str
    applied_rule_id: Optional[str]
    applied_weight: float
    intermediate_score: float
    percentage_contribution: float

class PriorityExplanation(BaseModel):
    generated_because: List[str]
    contributions: List[PriorityContribution]

class PriorityDecisionTrace(BaseModel):
    trace_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())
    engine_version: str = "1.0.0"
    policy_version: str = "1.0.0"
    evaluated_features: List[str]
    triggered_rules: List[str]
    applied_weights: Dict[str, float]

class PriorityGovernance(BaseModel):
    governance_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    conflict_resolved_count: int
    overrides_applied: bool
    policy_compliant: bool
    validation_messages: List[str]

class PriorityConfidence(BaseModel):
    feature_completeness: float
    rule_coverage: float
    overall_confidence: float

class PriorityScore(BaseModel):
    raw_score: float
    weighted_score: float
    normalized_score: float # 0 to 100
    level: PriorityLevel

class PriorityReport(BaseModel):
    report_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    document_uuid: str
    score: PriorityScore
    explanation: PriorityExplanation
    confidence: PriorityConfidence
    governance: PriorityGovernance
    decision_trace: PriorityDecisionTrace
    processing_time_ms: float

class JudicialPriorityIndex(BaseModel):
    document_uuid: str
    priority_report: PriorityReport

class PrioritySnapshot(BaseModel):
    snapshot_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())
    report: PriorityReport

class PriorityHistory(BaseModel):
    history_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    document_uuid: str
    old_level: Optional[PriorityLevel]
    new_level: PriorityLevel
    reason_for_change: str
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())

class PriorityPolicy(BaseModel):
    policy_id: str
    policy_level: str # SC, HC, DC
    active: bool
    version: str

class PrioritySimulation(BaseModel):
    simulation_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    what_if_scenario: str
    simulated_report: PriorityReport

class PriorityStatistics(BaseModel):
    total_calculated: int
    critical_percentage: float
    emergency_percentage: float
    average_score: float

class PriorityConfiguration(BaseModel):
    version: str = "1.0.0"
    rules: List[PriorityRule]
    weights: List[PriorityWeight]
    policies: List[PriorityPolicy]
