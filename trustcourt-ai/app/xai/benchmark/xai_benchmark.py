from typing import Dict, Any
import time

class XAIBenchmarkService:
    @staticmethod
    def measure_latency(func, *args, **kwargs) -> Dict[str, Any]:
        """
        Benchmarks the execution time of XAI engines.
        Crucial for enterprise monitoring.
        """
        start = time.perf_counter()
        result = func(*args, **kwargs)
        end = time.perf_counter()
        
        return {
            "result": result,
            "execution_time_ms": (end - start) * 1000
        }
