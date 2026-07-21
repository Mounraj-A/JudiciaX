from fastapi import Request
from starlette.middleware.base import BaseHTTPMiddleware
import uuid
from loguru import logger

class RequestContextMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        request_id = request.headers.get("X-Request-ID", str(uuid.uuid4()))
        correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
        
        # Bind context to logger
        with logger.contextualize(request_id=request_id, correlation_id=correlation_id):
            logger.info(f"Incoming Request: {request.method} {request.url.path}")
            response = await call_next(request)
            logger.info(f"Outgoing Response: {response.status_code}")
            
            response.headers["X-Request-ID"] = request_id
            response.headers["X-Correlation-ID"] = correlation_id
            return response
