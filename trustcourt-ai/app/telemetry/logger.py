from loguru import logger
import sys
from app.config.settings import settings

def setup_logging():
    logger.remove()
    
    log_format = (
        "<green>{time:YYYY-MM-DD HH:mm:ss.SSS}</green> | "
        "<level>{level: <8}</level> | "
        "<cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> | "
        "<yellow>[{extra[request_id]}]</yellow> - <level>{message}</level>"
    )
    
    logger.configure(extra={"request_id": "NO-REQ-ID"})
    logger.add(sys.stdout, format=log_format, level=settings.LOG_LEVEL, enqueue=True)
    logger.info("Enterprise Telemetry Initialized")
