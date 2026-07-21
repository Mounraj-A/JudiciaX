# TrustCourt Enterprise OCR Execution Layer (FastAPI)

## Overview
This is Phase 5 of the TrustCourt Framework. This FastAPI microservice acts as the dedicated Enterprise OCR Execution Layer. It is designed to be called asynchronously by the Spring Boot backend (`court-ai-backend`) after the OCR Intelligence engine routes a document to the OCR pipeline.

## Integration with Spring Boot

The OCR Execution Layer runs independently (typically on port 8000) and communicates with the Spring Boot backend (typically on port 8080).

### Sequence Flow
1. Spring Boot (`court-ai-backend`) determines a document requires OCR.
2. Spring Boot sends an HTTP POST request to `http://<fastapi-host>:8000/ocr/process` containing:
   - `documentUuid` (Form data)
   - `correlationId` (Form data)
   - `engine` (Optional Form data, e.g., "tesseract", "paddleocr")
   - `files` (Multipart file upload)
3. FastAPI intercepts the request via `RequestContextMiddleware` for tracing.
4. FastAPI saves the file to a temporary location.
5. FastAPI executes `PreprocessingPipeline` (using Pillow).
6. FastAPI factory resolves the `OCREngine` and executes the text extraction (Simulated in this phase).
7. FastAPI runs `PostprocessingNormalizer` and `OCRValidator`.
8. FastAPI returns a structured JSON `OCRResponse` back to Spring Boot.

## Local Deployment & Testing

### 1. Setup Virtual Environment
```bash
python -m venv venv
# Windows
venv\Scripts\activate
# Linux/Mac
source venv/bin/activate
```

### 2. Install Dependencies
```bash
pip install -r requirements.txt
```

### 3. Run FastAPI Application
```bash
uvicorn app.main:app --reload --port 8000
```
Swagger UI will be available at: http://localhost:8000/docs
Redoc will be available at: http://localhost:8000/redoc

### 4. Health Checks
Check if the service is running:
```bash
curl http://localhost:8000/ocr/health
```

## Production Deployment (Docker)

```bash
docker build -t trustcourt-ocr-layer .
docker run -d -p 8000:8000 --name trustcourt-ocr trustcourt-ocr-layer
```
