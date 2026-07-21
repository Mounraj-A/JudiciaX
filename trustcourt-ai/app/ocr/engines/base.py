from abc import ABC, abstractmethod
from typing import List, Any
from app.dto.response import OCRPage

class OCREngine(ABC):
    
    @abstractmethod
    def is_available(self) -> bool:
        """Check if the OCR engine dependencies are installed and available."""
        pass

    @abstractmethod
    async def extract_text(self, image_paths: List[str], language: str = "eng") -> List[OCRPage]:
        """
        Extract text from a list of preprocessed image paths.
        Returns structured page results.
        """
        pass

    @property
    @abstractmethod
    def name(self) -> str:
        """Return the name of the OCR engine."""
        pass
