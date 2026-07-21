from app.governance.service.governance_services import IModelRegistry
from app.governance.model.registry_entry import RegistryEntry
from typing import Dict

class ModelRegistry(IModelRegistry):
    def __init__(self):
        self._models: Dict[str, RegistryEntry] = {}
        
    def register(self, entry: RegistryEntry):
        self._models[entry.registry_id] = entry
        
    def get_model_metadata(self, model_id: str) -> RegistryEntry:
        return self._models.get(model_id)
