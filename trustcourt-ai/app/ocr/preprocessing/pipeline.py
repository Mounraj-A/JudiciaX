from PIL import Image, ImageEnhance, ImageOps
import io
from typing import List
from loguru import logger

class PreprocessingPipeline:
    
    @staticmethod
    def process_image(image_path: str) -> str:
        """
        Simulates the execution of the preprocessing pipeline using Pillow.
        - Grayscale conversion
        - Contrast enhancement
        - Deskew/Rotation (simulated by PIL rotation if needed)
        """
        logger.debug(f"Preprocessing image: {image_path}")
        
        try:
            with Image.open(image_path) as img:
                # 1. Convert to grayscale
                img = ImageOps.grayscale(img)
                
                # 2. Enhance contrast
                enhancer = ImageEnhance.Contrast(img)
                img = enhancer.enhance(1.5)
                
                # 3. Simulate noise reduction / thresholding
                img = img.point(lambda p: p > 128 and 255)
                
                # We would normally save this to a temporary path and return the path.
                # Since we are mocking OCR execution engine internally, we will just return the original path for now.
                logger.debug(f"Preprocessing complete for {image_path}")
                return image_path
        except Exception as e:
            logger.error(f"Failed to preprocess image {image_path}: {e}")
            return image_path # Fallback to original
