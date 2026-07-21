from app.governance.service.governance_services import IVersionEngine
from typing import List
import hashlib

class VersionEngine(IVersionEngine):
    def generate_version(self, inputs: List[str]) -> str:
        """
        Deterministically generate a version hash from multiple input strings (e.g., config hashes).
        """
        combined = "".join(sorted(inputs))
        return hashlib.sha256(combined.encode()).hexdigest()[:12]
