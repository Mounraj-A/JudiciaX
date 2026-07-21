from abc import ABC, abstractmethod
from app.legal.dto.domain import LegalDocument
from app.features.dto.domain import FeatureGroup, FeatureMetadata

class BaseFeatureExtractor(ABC):
    """
    Abstract base class for extracting a specific group of features 
    from the structured LegalDocument.
    """
    
    @abstractmethod
    def extract(self, document: LegalDocument) -> FeatureGroup:
        pass
        
    def _add_feature(self, group: FeatureGroup, name: str, value: any, type_: str, desc: str):
        group.features[name] = value
        group.metadata[name] = FeatureMetadata(
            feature_name=name,
            feature_type=type_,
            source=self.__class__.__name__,
            description=desc
        )
