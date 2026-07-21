import asyncio
from loguru import logger

class MemoryQueueManager:
    def __init__(self):
        self.queue = asyncio.Queue()
        
    async def enqueue(self, task: dict):
        logger.debug(f"Task enqueued: {task.get('id')}")
        await self.queue.put(task)
        
    def get_size(self) -> int:
        return self.queue.qsize()

# Singleton instance
queue_manager = MemoryQueueManager()
