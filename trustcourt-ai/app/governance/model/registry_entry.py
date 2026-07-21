from pydantic import BaseModel
from typing import List, Dict, Any, Optional

class RegistryEntry(BaseModel):
    registry_id: str
    name: str
    version: str
    type: str = "MODEL" # e.g. MODEL, PIPELINE
    engine_versions: Dict[str, str] = {}
    dependencies: List[str] = []
    metadata: Dict[str, Any] = {}
