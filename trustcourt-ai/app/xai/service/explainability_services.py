from abc import ABC, abstractmethod
from typing import List, Dict, Any
from app.xai.dto.request.explain_request import ExplainRequest
from app.xai.model.explainability_report import ExplainabilityReport
from app.xai.model.feature_importance import FeatureImportance
from app.xai.model.rule_contribution import RuleContribution
from app.xai.model.weight_contribution import WeightContribution
from app.xai.model.unified_decision_trace import UnifiedDecisionTrace
from app.xai.model.reasoning_graph import ReasoningGraph

class IFeatureImportanceService(ABC):
    @abstractmethod
    def calculate_feature_importance(self, request: ExplainRequest) -> List[FeatureImportance]:
        pass

class IRuleExplanationService(ABC):
    @abstractmethod
    def explain_rules(self, request: ExplainRequest) -> List[RuleContribution]:
        pass

class IWeightExplanationService(ABC):
    @abstractmethod
    def explain_weights(self, request: ExplainRequest) -> List[WeightContribution]:
        pass

class IDecisionTraceService(ABC):
    @abstractmethod
    def generate_unified_trace(self, request: ExplainRequest) -> UnifiedDecisionTrace:
        pass

class IReasoningGraphService(ABC):
    @abstractmethod
    def build_graph(self, trace: UnifiedDecisionTrace) -> ReasoningGraph:
        pass

class IConfidenceExplanationService(ABC):
    @abstractmethod
    def explain_confidence(self, request: ExplainRequest) -> Dict[str, Any]:
        pass

class IGovernanceExplanationService(ABC):
    @abstractmethod
    def explain_governance(self, request: ExplainRequest) -> Dict[str, Any]:
        pass

# Persona Services
class IJudgeExplanationService(ABC):
    @abstractmethod
    def generate(self, data: Dict[str, Any]) -> str:
        pass

class ICitizenExplanationService(ABC):
    @abstractmethod
    def generate(self, data: Dict[str, Any]) -> str:
        pass

class ITechnicalExplanationService(ABC):
    @abstractmethod
    def generate(self, data: Dict[str, Any]) -> str:
        pass

class IResearchExplanationService(ABC):
    @abstractmethod
    def generate(self, data: Dict[str, Any]) -> str:
        pass

# Main Facade
class IExplainabilityEngineService(ABC):
    @abstractmethod
    def generate_report(self, request: ExplainRequest) -> ExplainabilityReport:
        pass
