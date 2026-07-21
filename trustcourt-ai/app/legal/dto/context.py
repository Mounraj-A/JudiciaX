from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any

class ExtractionContext(BaseModel):
    """
    Wraps an extracted value with its full source context, preserving OCR 
    metadata and extraction confidence for future UI highlighting and validation.
    """
    value: Any
    page_number: Optional[int] = Field(None, description="The page number where this entity was found.")
    paragraph_number: Optional[int] = Field(None, description="The paragraph number.")
    line_number: Optional[int] = Field(None, description="The line number.")
    char_offset_start: Optional[int] = Field(None, description="Starting character offset in the document.")
    char_offset_end: Optional[int] = Field(None, description="Ending character offset in the document.")
    bounding_box: Optional[List[int]] = Field(None, description="[x_min, y_min, x_max, y_max]")
    ocr_confidence: Optional[float] = Field(None, description="Original OCR confidence for this text segment.")
    extraction_method: str = Field(..., description="E.g., REGEX, SPACY_NER, DICTIONARY, RAPIDFUZZ, HEURISTIC")
    extraction_rule: str = Field(..., description="The specific rule or model label that triggered this extraction.")
    validation_status: str = Field(default="PENDING", description="Status: PENDING, VALID, CONFLICT, INVALID")
    
    class Config:
        frozen = True # Make it immutable
