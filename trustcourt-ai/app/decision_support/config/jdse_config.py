from pydantic import BaseSettings

class JDSEConfig(BaseSettings):
    engine_version: str = "1.0.0"
    policy_version: str = "1.0.0"
    
    # Thresholds for actionable recommendations
    ready_jpi_threshold: float = 70.0
    ready_cts_threshold: float = 80.0
    emergency_jpi_threshold: float = 90.0
    critical_risk_cts_threshold: float = 40.0
    
    # Flags for future integrations
    enable_calendar_integration: bool = False
    enable_legal_database_integration: bool = False

    class Config:
        env_prefix = "JDSE_"

jdse_config = JDSEConfig()
