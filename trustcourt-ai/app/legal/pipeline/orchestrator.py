from app.legal.dto.domain import LegalDocument, ExtractionMetadata
from app.legal.services.hybrid_extraction import HybridExtractionService
from app.legal.normalizer.canonicalization import CanonicalizationService
from app.legal.relationship.entity_linker import EntityLinkingService
from app.legal.validation.extraction_validator import ExtractionValidationService
from app.legal.graph.engine import NetworkXKnowledgeGraphEngine
from app.legal.services.rule_engine import LegalRuleEngine
from loguru import logger
import time
import uuid

class LegalExtractionPipelineService:
    """
    Orchestrates the entire Enterprise Legal Information Extraction Engine pipeline.
    Produces the canonical LegalDocument domain model.
    """
    
    def __init__(self):
        self.hybrid_extraction = HybridExtractionService()
        self.canonicalization = CanonicalizationService()
        self.entity_linker = EntityLinkingService()
        self.validator = ExtractionValidationService()
        self.graph_engine = NetworkXKnowledgeGraphEngine()
        self.rule_engine = LegalRuleEngine()
        
    def process_ocr_text(self, document_uuid: str, raw_text: str) -> LegalDocument:
        logger.info(f"Starting Legal Extraction Pipeline for {document_uuid}")
        start_time = time.time()
        
        # 1. Hybrid Extraction
        document = self.hybrid_extraction.execute(raw_text)
        
        # 2. Canonicalization
        document = self.canonicalization.process(document)
        
        # 3. Entity Linking (Coreference Resolution)
        document = self.entity_linker.process(document)
        
        # 4. Validation & Confidence Scoring
        document = self.validator.process(document)
        
        # 5. Relationship Resolution (Mocked within linking for now)
        # 6. Knowledge Graph Generation
        self.graph_engine.build_graph(document)
        document.knowledge_graph = self.graph_engine.serialize()
        
        # 7. Legal Rule Mapping
        document = self.rule_engine.process(document)
        
        # Finalize Metadata
        document.extraction_metadata = ExtractionMetadata(
            document_uuid=document_uuid,
            processing_time_ms=(time.time() - start_time) * 1000,
            language_detected="en",
            language_confidence=0.99,
            pipeline_version="1.0.0"
        )
        
        logger.info(f"Pipeline completed successfully in {document.extraction_metadata.processing_time_ms:.2f}ms")
        return document
