from pydantic import BaseModel, Field
from typing import Dict, Any

class PublicationReport(BaseModel):
    report_id: str
    experiment_id: str
    research_summary: str
    architecture_summary: str
    pipeline_summary: str
    experiment_summary: str
    evaluation_summary: str
    governance_summary: str
    benchmark_report: Dict[str, Any]
    publication_tables: Dict[str, Any]
    research_figures_metadata: Dict[str, Any]
