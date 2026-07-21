from pydantic import BaseModel, Field
from typing import Dict, Any, List, Optional
from datetime import datetime
from enum import Enum
import uuid

class TrustLevel(str, Enum):
    VERY_LOW = "VERY_LOW"
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    VERY_HIGH = "VERY_HIGH"

class ReadinessLevel(str, Enum):
    NOT_READY = "NOT_READY"
    PARTIAL = "PARTIAL"
    READY = "READY"

class TrustRule(BaseModel):
    rule_id: str
    rule_name: str
    rule_category: str
    is_active: bool
    version: str
    description: str
    legal_reference: str
    priority: int

class TrustWeight(BaseModel):
    weight_id: str
    factor_name: str
    base_weight: float
    multiplier: float = 1.0

class TrustContribution(BaseModel):
    feature_name: str
    applied_rule_id: Optional[str]
    applied_weight: float
    intermediate_score: float
    percentage_contribution: float
    confidence: float

class TrustLineage(BaseModel):
    feature_source: str
    feature_uuid: str
    document_source: str
    document_uuid: str
    extraction_confidence: float
    feature_version: str
    pipeline_version: str
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())

class TrustDecisionTrace(BaseModel):
    trace_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())
    engine_version: str = "1.0.0"
    policy_version: str = "1.0.0"
    configuration_version: str
    evaluated_features: List[str]
    triggered_rules: List[str]
    applied_weights: Dict[str, float]
    readiness_impact: float

class TrustGovernance(BaseModel):
    governance_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    conflict_resolved_count: int
    overrides_applied: bool
    policy_compliant: bool
    validation_messages: List[str]

class TrustConfidence(BaseModel):
    evidence_confidence: float
    document_confidence: float
    verification_confidence: float
    integrity_confidence: float
    overall_confidence: float

class TrustSection(BaseModel):
    score: float
    confidence: float
    status: str
    reason: str

class TrustProfile(BaseModel):
    overall_trust: TrustSection
    document_trust: TrustSection
    evidence_trust: TrustSection
    identity_trust: TrustSection
    integrity_trust: TrustSection
    procedural_trust: TrustSection
    workflow_trust: TrustSection
    ai_readiness: TrustSection
    verification_trust: TrustSection
    metadata_trust: TrustSection

class ReadinessScore(BaseModel):
    raw_score: float
    normalized_score: float
    level: ReadinessLevel

class TrustScore(BaseModel):
    raw_score: float
    weighted_score: float
    normalized_score: float # 0 to 100
    level: TrustLevel

class TrustReport(BaseModel):
    report_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    document_uuid: str
    score: TrustScore
    readiness: ReadinessScore
    profile: TrustProfile
    confidence: TrustConfidence
    governance: TrustGovernance
    decision_trace: TrustDecisionTrace
    lineage: TrustLineage
    processing_time_ms: float
    snapshot_id: str
    history_id: str

class CaseTrustScore(BaseModel):
    document_uuid: str
    trust_report: TrustReport

class TrustSnapshot(BaseModel):
    snapshot_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())
    report: TrustReport

class TrustDrift(BaseModel):
    drift_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    document_uuid: str
    previous_score: float
    current_score: float
    drift_value: float
    trend: str # e.g. "UP", "DOWN", "STABLE"
    changed_factors: List[str]
    reason: str
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())

class TrustHistory(BaseModel):
    history_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    document_uuid: str
    old_level: Optional[TrustLevel]
    new_level: TrustLevel
    reason_for_change: str
    timestamp: str = Field(default_factory=lambda: datetime.utcnow().isoformat())

class TrustPolicy(BaseModel):
    policy_id: str
    policy_level: str
    active: bool
    version: str

class TrustSimulation(BaseModel):
    simulation_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    what_if_scenario: str
    simulated_report: TrustReport

class TrustConfiguration(BaseModel):
    version: str = "1.0.0"
    hash: str = ""
    timestamp: str = ""
    rules: List[TrustRule]
    weights: List[TrustWeight]
    policies: List[TrustPolicy]
