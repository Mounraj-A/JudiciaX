from app.ocr.engines.base import OCREngine
from app.ocr.engines.tesseract import TesseractEngine
from app.ocr.engines.paddle import PaddleOCREngine
from app.ocr.engines.easyocr import EasyOCREngine
from loguru import logger

class OCREngineFactory:
    _engines = {
        "tesseract": TesseractEngine(),
        "paddleocr": PaddleOCREngine(),
        "easyocr": EasyOCREngine()
    }
    
    @classmethod
    def get_engine(cls, name: str = None) -> OCREngine:
        engine_name = name or "tesseract"
        engine = cls._engines.get(engine_name.lower())
        
        if not engine:
            logger.warning(f"Engine {engine_name} not found. Falling back to tesseract.")
            engine = cls._engines["tesseract"]
            
        if not engine.is_available():
            logger.warning(f"Engine {engine.name} is not available. Falling back to tesseract.")
            engine = cls._engines["tesseract"]
            
        return engine
    
    @classmethod
    def list_available_engines(cls) -> list[str]:
        return [name for name, engine in cls._engines.items() if engine.is_available()]
