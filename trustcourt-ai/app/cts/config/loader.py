import yaml
from pathlib import Path
from app.cts.dto.domain import TrustConfiguration, TrustRule, TrustWeight, TrustPolicy
from loguru import logger
import hashlib
from datetime import datetime

class TrustConfigurationLoader:
    """
    Loads Trust Rules, Weights, and Policies from YAML configuration files.
    """
    
    def __init__(self, config_dir: str = "app/cts/config"):
        self.config_dir = Path(config_dir)
        self._config = None
        self.reload()
        
    def reload(self):
        logger.info(f"Loading YAML configs from {self.config_dir}")
        try:
            rules_path = self.config_dir / "trust_rules.yaml"
            weights_path = self.config_dir / "trust_weights.yaml"
            policies_path = self.config_dir / "trust_policies.yaml"
            
            with open(rules_path, "r") as f:
                rules_data = yaml.safe_load(f)
            with open(weights_path, "r") as f:
                weights_data = yaml.safe_load(f)
            with open(policies_path, "r") as f:
                policies_data = yaml.safe_load(f)
                
            rules = [TrustRule(**r) for r in rules_data.get("rules", [])]
            weights = [TrustWeight(**w) for w in weights_data.get("weights", [])]
            policies = [TrustPolicy(**p) for p in policies_data.get("policies", [])]
            
            # Create a checksum of the config
            config_str = str(rules_data) + str(weights_data) + str(policies_data)
            config_hash = hashlib.sha256(config_str.encode()).hexdigest()
            
            self._config = TrustConfiguration(
                version="1.0.0",
                hash=config_hash,
                timestamp=datetime.utcnow().isoformat(),
                rules=rules,
                weights=weights,
                policies=policies
            )
            logger.info(f"Loaded configuration Hash: {config_hash}")
        except Exception as e:
            logger.error(f"Failed to load Trust Configurations: {e}")
            raise e
            
    def get_configuration(self) -> TrustConfiguration:
        return self._config
