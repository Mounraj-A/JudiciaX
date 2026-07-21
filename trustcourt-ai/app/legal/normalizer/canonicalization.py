import re
from loguru import logger
from app.legal.dto.domain import LegalDocument

class CanonicalizationService:
    """
    Normalizes extracted entities into standard enterprise formats.
    e.g. "Sec.302 IPC" -> Act: "Indian Penal Code", Section: "302"
    """
    
    IPC_MAPPING = {
        "ipc": "Indian Penal Code",
        "crpc": "Code of Criminal Procedure",
        "iea": "Indian Evidence Act"
    }

    def process(self, document: LegalDocument) -> LegalDocument:
        logger.info("Running CanonicalizationService on LegalDocument")
        
        # In a real implementation, we'd canonicalize sections, court names, etc. here.
        # This is a deterministic rule-based mapping phase.
        
        for party in document.parties:
            party.canonical_name = party.canonical_name.upper().strip()
            
        for judge in document.judges:
            judge.canonical_name = judge.canonical_name.title().strip()
            
        return document
