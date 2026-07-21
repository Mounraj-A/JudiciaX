from app.governance.service.governance_services import (
    IGovernanceEngine, IBenchmarkEngine, IExperimentEngine,
    IEvaluationEngine, IMetricsEngine, IPublicationReportEngine
)
from app.governance.governance.governance_engine import GovernanceEngine
from app.governance.benchmark.benchmark_engine import BenchmarkEngine
from app.governance.experiment.experiment_engine import ExperimentEngine
from app.governance.evaluation.evaluation_engine import EvaluationEngine
from app.governance.metrics.metrics_engine import MetricsEngine
from app.governance.report.publication_report_engine import PublicationReportEngine

class GovernancePlatformFacade:
    """
    Centralized service providing unified access to all Governance platform sub-systems.
    """
    def __init__(self):
        self.governance_engine: IGovernanceEngine = GovernanceEngine()
        self.benchmark_engine: IBenchmarkEngine = BenchmarkEngine()
        self.experiment_engine: IExperimentEngine = ExperimentEngine()
        self.evaluation_engine: IEvaluationEngine = EvaluationEngine()
        self.metrics_engine: IMetricsEngine = MetricsEngine()
        self.publication_engine: IPublicationReportEngine = PublicationReportEngine()
