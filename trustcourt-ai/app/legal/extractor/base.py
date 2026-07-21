from abc import ABC, abstractmethod
from typing import List, Dict, Any, TypeVar, Generic
from app.legal.dto.context import ExtractionContext
import uuid

T = TypeVar('T')

class BaseExtractor(ABC, Generic[T]):
    """
    Abstract Base Class for all legal extractors.
    Enforces a strict contract that all extractors must preserve context.
    """
    
    @abstractmethod
    def extract(self, text: str) -> List[T]:
        """
        Executes the extraction logic on the raw text and returns a list of typed Domain objects
        or raw ExtractionContext objects, depending on the extractor implementation.
        """
        pass
        
    def _create_context(self, value: Any, method: str, rule: str, confidence: float = 1.0) -> ExtractionContext:
        """Helper to consistently generate ExtractionContext."""
        return ExtractionContext(
            value=value,
            extraction_method=method,
            extraction_rule=rule,
            ocr_confidence=confidence, # Mocked to 1.0 for now
            validation_status="PENDING"
        )
        
    def _generate_id(self, prefix: str) -> str:
        return f"{prefix}_{uuid.uuid4().hex[:8]}"
