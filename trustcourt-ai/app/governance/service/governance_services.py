from abc import ABC, abstractmethod
from typing import Dict, Any, List
from app.governance.model.governance_policy import GovernancePolicy
from app.governance.model.benchmark_result import BenchmarkResult
from app.governance.model.research_metrics import ResearchMetrics
from app.governance.model.experiment_record import ExperimentRecord
from app.governance.model.registry_entry import RegistryEntry
from app.governance.model.publication_report import PublicationReport
from app.governance.dto.request.governance_request import GovernanceRequest

# Registries
class IPipelineRegistry(ABC):
    @abstractmethod
    def register(self, entry: RegistryEntry): pass
class IModelRegistry(ABC):
    @abstractmethod
    def register(self, entry: RegistryEntry): pass
class IConfigurationRegistry(ABC):
    @abstractmethod
    def register(self, config: Dict[str, Any]): pass

# Evaluation & Metrics
class IEvaluationEngine(ABC):
    @abstractmethod
    def evaluate(self, request: GovernanceRequest) -> Dict[str, Any]: pass
class IMetricsEngine(ABC):
    @abstractmethod
    def calculate_metrics(self) -> ResearchMetrics: pass
class IFairnessEngine(ABC):
    @abstractmethod
    def evaluate_fairness(self, request: GovernanceRequest) -> Dict[str, Any]: pass
class IBiasAnalysisEngine(ABC):
    @abstractmethod
    def analyze_bias(self, request: GovernanceRequest) -> Dict[str, Any]: pass
class IRobustnessEngine(ABC):
    @abstractmethod
    def test_robustness(self, config: Dict[str, Any]) -> Dict[str, Any]: pass
class ISensitivityAnalysisEngine(ABC):
    @abstractmethod
    def analyze_sensitivity(self) -> Dict[str, Any]: pass
class IAblationEngine(ABC):
    @abstractmethod
    def perform_ablation(self, config: Dict[str, Any]) -> Dict[str, Any]: pass

# Governance & Core
class IGovernanceEngine(ABC):
    @abstractmethod
    def enforce(self, request: GovernanceRequest) -> bool: pass
class IBenchmarkEngine(ABC):
    @abstractmethod
    def measure(self, request: GovernanceRequest) -> BenchmarkResult: pass
class IExperimentEngine(ABC):
    @abstractmethod
    def start_experiment(self, config: Dict[str, Any]) -> ExperimentRecord: pass
class IDatasetEngine(ABC):
    @abstractmethod
    def track_dataset(self, metadata: Dict[str, Any]) -> str: pass
class IPolicyEngine(ABC):
    @abstractmethod
    def validate_policy(self, policy: GovernancePolicy) -> bool: pass

# Lineage & History
class IVersionEngine(ABC):
    @abstractmethod
    def generate_version(self, inputs: List[str]) -> str: pass
class IHistoryEngine(ABC):
    @abstractmethod
    def log_history(self, record: Any): pass
class ISnapshotEngine(ABC):
    @abstractmethod
    def create_snapshot(self, state: Dict[str, Any]) -> str: pass
class ILineageEngine(ABC):
    @abstractmethod
    def trace_lineage(self, case_uuid: str) -> Dict[str, Any]: pass
class IReproducibilityEngine(ABC):
    @abstractmethod
    def verify(self, experiment_id: str) -> bool: pass

# Publication
class IPublicationReportEngine(ABC):
    @abstractmethod
    def generate(self, experiment_id: str) -> PublicationReport: pass
class IResearchArtifactEngine(ABC):
    @abstractmethod
    def generate_artifacts(self, report: PublicationReport): pass
class IStatisticsEngine(ABC):
    @abstractmethod
    def calculate_stats(self, metrics: ResearchMetrics) -> Dict[str, Any]: pass
