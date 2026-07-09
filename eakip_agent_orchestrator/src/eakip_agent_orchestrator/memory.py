import uuid
import logging
from datetime import datetime, timedelta
from typing import Optional, List
from sqlalchemy import select, delete
from sqlalchemy.orm import Session
from .models import MemoryNode
from .db_helper import get_dynamic_session

logger = logging.getLogger(__name__)

class MemoryNodeRepository:
    def __init__(self, session: Optional[Session] = None):
        self._session = session

    @property
    def session(self) -> Session:
        if self._session is None:
            self._session = get_dynamic_session()
        return self._session

    def save(self, node: MemoryNode) -> MemoryNode:
        self.session.add(node)
        self.session.commit()
        return node

    def find_by_user_id(self, user_id: uuid.UUID) -> List[MemoryNode]:
        stmt = select(MemoryNode).where(MemoryNode.user_id == user_id)
        return list(self.session.execute(stmt).scalars().all())

    def find_by_user_id_and_memory_type(self, user_id: uuid.UUID, memory_type: str) -> List[MemoryNode]:
        stmt = select(MemoryNode).where(MemoryNode.user_id == user_id, MemoryNode.memory_type == memory_type)
        return list(self.session.execute(stmt).scalars().all())

    def search_memories(self, user_id: uuid.UUID, query: str) -> List[MemoryNode]:
        # SELECT m FROM MemoryNode m WHERE m.userId = :userId AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%'))
        stmt = select(MemoryNode).where(
            MemoryNode.user_id == user_id,
            MemoryNode.content.ilike(f"%{query}%")
        )
        return list(self.session.execute(stmt).scalars().all())

    def delete_all(self, nodes: List[MemoryNode]) -> None:
        for node in nodes:
            self.session.delete(node)
        self.session.commit()

class MemoryManager:
    def __init__(self, repo: Optional[MemoryNodeRepository] = None):
        self.repo = repo if repo else MemoryNodeRepository()

    def save_memory(self, user_id: uuid.UUID, type: str, content: str, score: float) -> MemoryNode:
        logger.info(f"Saving memory node. Type={type}, Score={score}")
        node = MemoryNode(
            user_id=user_id,
            memory_type=type.upper(),
            content=content,
            relevance_score=score
        )
        return self.repo.save(node)

    def retrieve_memories(self, user_id: uuid.UUID, query: str, limit: int) -> List[MemoryNode]:
        logger.info(f"Querying memories. Query={query}, Limit={limit}")
        memories = self.repo.search_memories(user_id, query)
        
        # Rank by relevance score descending and update last accessed date
        memories.sort(key=lambda x: x.relevance_score, reverse=True)
        results = memories[:limit]
        
        for node in results:
            node.last_accessed_date = datetime.now()
            self.repo.save(node)
            
        return results

    def compress_memories(self, user_id: uuid.UUID) -> None:
        logger.info(f"Running memory compression/summarization algorithms over user: {user_id}")
        memories = self.repo.find_by_user_id(user_id)
        
        cutoff = datetime.now()
        expired = []
        for node in memories:
            expire_time = node.created_date + timedelta(days=node.retention_days)
            if expire_time < cutoff:
                expired.append(node)
                
        if expired:
            logger.info(f"Expiring {len(expired)} outdated memory nodes")
            self.repo.delete_all(expired)

class LearningEngine:
    def __init__(self, memory_manager: Optional[MemoryManager] = None):
        self.memory_manager = memory_manager if memory_manager else MemoryManager()

    def record_feedback(self, user_id: uuid.UUID, target_entity: str, rating: int, feedback_msg: Optional[str]) -> None:
        logger.info(f"Recording feedback score. Target: {target_entity}, Rating: {rating}/5")
        relevance_adjustment = (rating - 3) * 0.1
        
        memory_content = f"User rated '{target_entity}' value as {rating}. Feedback comment: '{feedback_msg or 'none'}'"
        self.memory_manager.save_memory(
            user_id,
            "PREFERENCE",
            memory_content,
            0.8 + relevance_adjustment
        )
        logger.info("Preference memory updated based on dynamic learning adjustments")
