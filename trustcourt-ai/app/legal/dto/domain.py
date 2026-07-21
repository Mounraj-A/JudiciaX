from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from app.legal.dto.context import ExtractionContext

class CaseMetadata(BaseModel):
    case_number: Optional[ExtractionContext] = None
    crime_number: Optional[ExtractionContext] = None
    fir_number: Optional[ExtractionContext] = None
    court_number: Optional[ExtractionContext] = None
    court_code: Optional[ExtractionContext] = None
    court_name: Optional[ExtractionContext] = None
    case_category: Optional[ExtractionContext] = None
    case_type: Optional[ExtractionContext] = None
    case_status: Optional[ExtractionContext] = None

class BaseLegalEntity(BaseModel):
    canonical_id: str = Field(..., description="Unique ID for entity linking")
    raw_name: ExtractionContext
    canonical_name: str
    aliases: List[ExtractionContext] = Field(default_factory=list)
    attributes: Dict[str, ExtractionContext] = Field(default_factory=dict)

class Party(BaseLegalEntity):
    role: ExtractionContext # Petitioner, Respondent, Accused, Victim

class Judge(BaseLegalEntity):
    designation: Optional[ExtractionContext] = None

class Advocate(BaseLegalEntity):
    representing_party_id: Optional[str] = None
    registration_number: Optional[ExtractionContext] = None

class Witness(BaseLegalEntity):
    witness_number: Optional[ExtractionContext] = None # PW-1, DW-1

class Organization(BaseLegalEntity):
    org_type: ExtractionContext # Police Station, Hospital, Govt Dept

class LegalSection(BaseModel):
    act_canonical: str
    section_canonical: str
    raw_act: ExtractionContext
    raw_section: ExtractionContext
    description: Optional[str] = None

class DateEntity(BaseModel):
    date_type: ExtractionContext # Incident Date, Hearing Date, etc.
    raw_date: ExtractionContext
    iso_date: Optional[str] = None

class LocationEntity(BaseModel):
    location_type: ExtractionContext # District, Village, Police Station
    address: ExtractionContext

class Evidence(BaseModel):
    evidence_type: ExtractionContext # Medical Report, FIR, Charge Sheet, Weapon
    description: ExtractionContext

class Relationship(BaseModel):
    source_entity_id: str
    target_entity_id: str
    relation_type: str # E.g., REPRESENTED_BY, ISSUED_BY, INVOLVED_IN
    confidence: float
    method: str

class ConfidenceReport(BaseModel):
    overall_confidence: float
    extraction_methods_used: Dict[str, int]
    conflict_count: int
    validation_errors: List[str]

class ExtractionMetadata(BaseModel):
    document_uuid: str
    processing_time_ms: float
    language_detected: str
    language_confidence: float
    pipeline_version: str

class LegalDocument(BaseModel):
    """
    The canonical, immutable domain model produced by the Legal Information Extraction Engine.
    This replaces raw JSON and serves as the strict contract for downstream AI modules (JPI, CTS, XAI).
    """
    metadata: CaseMetadata = Field(default_factory=CaseMetadata)
    parties: List[Party] = Field(default_factory=list)
    judges: List[Judge] = Field(default_factory=list)
    advocates: List[Advocate] = Field(default_factory=list)
    witnesses: List[Witness] = Field(default_factory=list)
    organizations: List[Organization] = Field(default_factory=list)
    legal_sections: List[LegalSection] = Field(default_factory=list)
    dates: List[DateEntity] = Field(default_factory=list)
    locations: List[LocationEntity] = Field(default_factory=list)
    evidence: List[Evidence] = Field(default_factory=list)
    
    relationships: List[Relationship] = Field(default_factory=list)
    knowledge_graph: Optional[Dict[str, Any]] = Field(None, description="Serialized NetworkX representation")
    
    confidence_report: ConfidenceReport
    extraction_metadata: ExtractionMetadata
