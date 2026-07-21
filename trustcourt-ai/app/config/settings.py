import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    APP_NAME: str = "TrustCourt Enterprise OCR Execution Layer"
    APP_VERSION: str = "1.0.0"
    ENVIRONMENT: str = "development"
    
    DEFAULT_OCR_ENGINE: str = "tesseract"
    TESSERACT_CMD_PATH: str = "/usr/bin/tesseract"
    
    LOG_LEVEL: str = "DEBUG"
    
    GATEWAY_URL: str = "http://localhost:8080/api/v1"
    JWT_SECRET: str = "placeholder_secret_for_validation"
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"

settings = Settings()
