from pydantic import BaseSettings

class XAIConfig(BaseSettings):
    engine_version: str = "1.0.0"
    policy_version: str = "1.0.0"
    threshold_version: str = "1.0.0"
    rule_version: str = "1.0.0"
    weight_version: str = "1.0.0"
    
    # Optional flags for future features
    enable_shap_export: bool = False
    enable_lime_export: bool = False
    
    class Config:
        env_prefix = "XAI_"

xai_config = XAIConfig()
