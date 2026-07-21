from app.governance.service.governance_services import IConfigurationRegistry
from typing import Dict, Any

class ConfigurationRegistry(IConfigurationRegistry):
    def __init__(self):
        self._configs: Dict[str, Dict[str, Any]] = {}
        
    def register(self, config: Dict[str, Any]):
        config_id = config.get("config_id", "default")
        self._configs[config_id] = config
