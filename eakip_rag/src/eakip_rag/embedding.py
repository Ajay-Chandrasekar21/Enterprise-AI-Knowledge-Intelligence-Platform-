import os
import math
import logging
import redis

logger = logging.getLogger(__name__)

class MockEmbeddingService:
    def __init__(self):
        redis_url = os.getenv("REDIS_URL", "redis://localhost:6379/0")
        try:
            self.redis_client = redis.from_url(redis_url, decode_responses=True)
        except Exception:
            self.redis_client = None
        self.prefix = "eakip:embedding:"

    def generate_embedding(self, text: str) -> list[float]:
        # Simple text hashing
        text_hash = abs(hash(text))
        cache_key = f"{self.prefix}{text_hash}"
        
        if self.redis_client:
            try:
                cached = self.redis_client.get(cache_key)
                if cached:
                    logger.debug("Embedding cache hit in Redis")
                    return [float(x) for x in cached.split(",")]
            except Exception as e:
                logger.warning(f"Redis get failed: {str(e)}")

        logger.debug("Generating mock embedding vector for text chunk")
        vector = []
        for i in range(128):
            # Same mock formula: sin(code + i) * 0.5 + 0.5
            val = math.sin(text_hash + i) * 0.5 + 0.5
            vector.append(val)

        if self.redis_client:
            try:
                vector_str = ",".join(str(x) for x in vector)
                self.redis_client.setex(cache_key, 86400, vector_str) # 24 Hours cache duration
            except Exception as e:
                logger.warning(f"Redis set failed: {str(e)}")

        return vector
