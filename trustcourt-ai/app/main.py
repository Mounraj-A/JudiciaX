from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config.settings import settings
from app.telemetry.logger import setup_logging
from app.middleware.context import RequestContextMiddleware
# We will import router later

def create_app() -> FastAPI:
    setup_logging()
    
    app = FastAPI(
        title=settings.APP_NAME,
        version=settings.APP_VERSION,
        description="Enterprise OCR Execution Layer for TrustCourt",
    )
    
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"], # In production, restrict this
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    app.add_middleware(RequestContextMiddleware)
    
    # Include routers
    from app.api.ocr_router import router as ocr_router
    from app.api.health_router import router as health_router
    from app.legal.api.controller import router as legal_router
    from app.features.api.controller import router as features_router
    from app.jpi.api.controller import router as jpi_router
    from app.cts.api.controller import router as cts_router
    
    app.include_router(ocr_router)
    app.include_router(health_router)
    app.include_router(legal_router)
    app.include_router(features_router)
    app.include_router(jpi_router)
    app.include_router(cts_router)
    
    @app.get("/")
    async def root():
        return {"service": settings.APP_NAME, "status": "running"}

    return app

app = create_app()
