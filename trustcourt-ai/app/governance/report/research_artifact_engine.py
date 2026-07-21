from app.governance.service.governance_services import IResearchArtifactEngine
from app.governance.model.publication_report import PublicationReport

class ResearchArtifactEngine(IResearchArtifactEngine):
    def generate_artifacts(self, report: PublicationReport):
        """
        Serializes tables and figures into markdown/latex. (Mock implementation)
        """
        pass
