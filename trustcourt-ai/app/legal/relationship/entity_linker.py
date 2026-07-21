from app.legal.dto.domain import LegalDocument, Party
from typing import List, Dict
from rapidfuzz import fuzz
from loguru import logger

class EntityLinkingService:
    """
    Resolves multiple mentions of the same entity into a single canonical entity.
    Maintains aliases.
    """
    
    SIMILARITY_THRESHOLD = 85.0
    
    def process(self, document: LegalDocument) -> LegalDocument:
        logger.info("Running EntityLinkingService for Coreference Resolution")
        
        document.parties = self._link_parties(document.parties)
        return document
        
    def _link_parties(self, parties: List[Party]) -> List[Party]:
        linked_parties: List[Party] = []
        
        for party in parties:
            merged = False
            for existing in linked_parties:
                # Use rapidfuzz to check similarity
                similarity = fuzz.ratio(party.canonical_name.lower(), existing.canonical_name.lower())
                
                if similarity >= self.SIMILARITY_THRESHOLD or party.role.value == existing.role.value:
                    # Merge logic: append the raw name as an alias
                    existing.aliases.append(party.raw_name)
                    merged = True
                    logger.debug(f"Merged {party.canonical_name} into {existing.canonical_name}")
                    break
                    
            if not merged:
                linked_parties.append(party)
                
        return linked_parties
