import uuid
from typing import List, Dict, Any, Optional
from fastapi import APIRouter, Query, Body
from eakip_core.common.dto import ApiResponse

router = APIRouter(prefix="/api/v1/memory", tags=["Agent Memory & Learning API"])

USER_UUID = uuid.UUID("11111111-1111-1111-1111-111111111111")

@router.get("/timeline", response_model=ApiResponse[List[dict]])
def get_timeline():
    from eakip_agent_orchestrator.memory import MemoryManager, MemoryNodeRepository
    memory_manager = MemoryManager()
    memory_repository = MemoryNodeRepository()
    
    memory_manager.compress_memories(USER_UUID)
    memories = memory_repository.find_by_user_id(USER_UUID)
    
    result = [{
        "id": str(m.id),
        "userId": str(m.user_id),
        "memoryType": m.memory_type,
        "content": m.content,
        "relevanceScore": m.relevance_score,
        "createdDate": m.created_date.isoformat()
    } for m in memories]
    
    return ApiResponse.success_response(result, "Timeline loaded successfully")

@router.get("/search", response_model=ApiResponse[List[dict]])
def search_memories(query: str):
    from eakip_agent_orchestrator.memory import MemoryManager
    memory_manager = MemoryManager()
    memories = memory_manager.retrieve_memories(USER_UUID, query, 5)
    
    result = [{
        "id": str(m.id),
        "userId": str(m.user_id),
        "memoryType": m.memory_type,
        "content": m.content,
        "relevanceScore": m.relevance_score,
        "createdDate": m.created_date.isoformat()
    } for m in memories]
    return ApiResponse.success_response(result, "Semantic memory search complete")

@router.post("/feedback", response_model=ApiResponse[str])
def submit_feedback(
    target: str,
    rating: int,
    comment: Optional[str] = Body(default=None)
):
    from eakip_agent_orchestrator.memory import LearningEngine
    engine = LearningEngine()
    engine.record_feedback(USER_UUID, target, rating, comment)
    return ApiResponse.success_response(target, "Feedback submitted and preference memory updated")

@router.post("/preferences", response_model=ApiResponse[dict])
def update_preferences(pref_map: Dict[str, str]):
    from eakip_agent_orchestrator.memory import MemoryManager
    memory_manager = MemoryManager()
    content = f"User set favorite categories interest tags: {pref_map.get('interests')}"
    node = memory_manager.save_memory(USER_UUID, "PREFERENCE", content, 1.0)
    
    result = {
        "id": str(node.id),
        "userId": str(node.user_id),
        "memoryType": node.memory_type,
        "content": node.content,
        "relevanceScore": node.relevance_score
    }
    return ApiResponse.success_response(result, "Explicit preference registered")
