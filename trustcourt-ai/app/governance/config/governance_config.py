from pydantic import BaseSettings

class GovernanceConfig(BaseSettings):
    global_policy_version: str = "2.0.0"
    enforce_strict_lineage: bool = True
    enable_auto_publication: bool = False
    
    class Config:
        env_prefix = "GOV_"

governance_config = GovernanceConfig()
