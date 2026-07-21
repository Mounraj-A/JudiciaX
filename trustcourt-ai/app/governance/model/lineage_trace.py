from pydantic import BaseModel
from typing import List, Dict, Any

class LineageTrace(BaseModel):
    case_uuid: str
    document_hashes: List[str]
    ocr_version: str
    legal_extraction_version: str
    feature_version: str
    jpi_version: str
    cts_version: str
    xai_version: str
    jdse_version: str
    research_report_ids: List[str]
