from app.governance.service.governance_services import IPipelineRegistry
from app.governance.model.registry_entry import RegistryEntry
from typing import Dict

class PipelineRegistry(IPipelineRegistry):
    def __init__(self):
        self._registry: Dict[str, RegistryEntry] = {}
        
    def register(self, entry: RegistryEntry):
        self._registry[entry.version] = entry
        
    def get_pipeline(self, version: str) -> RegistryEntry:
        return self._registry.get(version)
