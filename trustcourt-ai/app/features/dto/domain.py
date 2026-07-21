from pydantic import BaseModel, Field
from typing import Dict, Any, List, Optional
from datetime import datetime
import uuid

class FeatureMetadata(BaseModel):
    feature_name: str
    feature_type: str # NUMERICAL, CATEGORICAL, BOOLEAN, TEXT
    source: str
    description: str

class FeatureGroup(BaseModel):
    """
    A collection of related features (e.g. Case Features, Party Features).
    """
    group_name: str
    features: Dict[str, Any] = Field(default_factory=dict)
    metadata: Dict[str, FeatureMetadata] = Field(default_factory=dict)

class FeatureAudit(BaseModel):
    created_at: str = Field(default_factory=lambda: datetime.utcnow().isoformat())
    created_by: str = "FeaturePipelineService"
    pipeline_version: str = "1.0.0"

class FeatureVersion(BaseModel):
    schema_version: str = "1.0.0"
    extraction_version: str = "1.0.0"
    transformation_version: str = "1.0.0"

class FeatureStatistics(BaseModel):
    total_features: int
    missing_values: int
    completeness_score: float

class FeatureVector(BaseModel):
    """
    The canonical, dense vector output mapping all structured groups.
    """
    vector_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    document_uuid: str
    
    # 17 Categories of Features
    case_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Case Features"))
    party_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Party Features"))
    document_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Document Features"))
    legal_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Legal Features"))
    timeline_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Timeline Features"))
    medical_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Medical Features"))
    risk_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Risk Features"))
    procedural_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Procedural Features"))
    trust_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Trust Features"))
    urgency_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Urgency Features"))
    social_features: FeatureGroup = Field(default_factory=lambda: FeatureGroup(group_name="Social Features"))
    
    # Pre-computed dense arrays for direct ML consumption
    dense_numerical: List[float] = Field(default_factory=list)
    dense_categorical: List[int] = Field(default_factory=list)
    dense_boolean: List[int] = Field(default_factory=list)
    
    audit: FeatureAudit = Field(default_factory=FeatureAudit)
    version: FeatureVersion = Field(default_factory=FeatureVersion)
    statistics: Optional[FeatureStatistics] = None

class FeatureStoreRecord(BaseModel):
    vector_id: str
    document_uuid: str
    feature_vector: FeatureVector
    stored_at: str = Field(default_factory=lambda: datetime.utcnow().isoformat())
