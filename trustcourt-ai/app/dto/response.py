from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

class OCRWord(BaseModel):
    text: str
    confidence: float
    bbox: List[int] = Field(default_factory=list, description="[x_min, y_min, x_max, y_max]")

class OCRLine(BaseModel):
    text: str
    confidence: float
    words: List[OCRWord] = Field(default_factory=list)
    bbox: List[int] = Field(default_factory=list)

class OCRParagraph(BaseModel):
    text: str
    lines: List[OCRLine] = Field(default_factory=list)
    bbox: List[int] = Field(default_factory=list)

class OCRPage(BaseModel):
    pageNumber: int
    text: str
    width: int
    height: int
    averageConfidence: float
    paragraphs: List[OCRParagraph] = Field(default_factory=list)

class OCRValidation(BaseModel):
    isValid: bool
    missingPages: List[int] = Field(default_factory=list)
    unreadablePages: List[int] = Field(default_factory=list)
    confidenceScore: float
    issues: List[str] = Field(default_factory=list)

class OCRHealth(BaseModel):
    status: str
    version: str
    memoryUsageMB: float
    activeQueueSize: int
    enginesAvailable: List[str]

class OCRStatistics(BaseModel):
    totalProcessed: int
    totalFailures: int
    averageProcessingTimeMs: float

class OCRResponse(BaseModel):
    requestId: str
    correlationId: str
    documentUuid: str
    engineUsed: str
    language: str
    totalTimeMs: float
    averageConfidence: float
    pages: List[OCRPage]
    fullText: str
    validation: OCRValidation
    warnings: List[str] = Field(default_factory=list)
