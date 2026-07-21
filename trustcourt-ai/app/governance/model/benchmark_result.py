from pydantic import BaseModel, Field
from typing import Dict

class BenchmarkResult(BaseModel):
    benchmark_id: str
    case_uuid: str
    pipeline_version: str
    latency_ms_per_stage: Dict[str, float] = Field(default_factory=dict, description="Latency in ms per engine (OCR, JPI, CTS, etc)")
    total_latency_ms: float
    memory_usage_mb: float = 0.0
    cpu_utilization_pct: float = 0.0
