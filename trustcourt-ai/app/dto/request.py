from pydantic import BaseModel, Field
from typing import Optional

class OCRRequest(BaseModel):
    documentUuid: str = Field(..., description="Unique identifier of the document from Gateway")
    correlationId: str = Field(..., description="Traceability correlation ID")
    engine: Optional[str] = Field(None, description="Preferred OCR Engine (tesseract, paddle, etc.)")
    language: Optional[str] = Field("eng", description="Language hint for OCR")
