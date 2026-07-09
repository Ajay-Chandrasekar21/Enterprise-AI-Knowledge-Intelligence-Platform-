from typing import Dict, Any
from fastapi import APIRouter, Depends
from eakip_core.common.dto import ApiResponse
from eakip_core.domain.models import User
from ..config.security import get_current_user

router = APIRouter(prefix="/api/v1/ai", tags=["AI Agent Runtime Orchestrator API"])

class QueryRequest(ValueError):
    # Wait, we can use simple BaseModel or raw request dict.
    # To keep code clean and robust:
    pass

from pydantic import BaseModel
class QueryRequestModel(BaseModel):
    query: str

@router.post("/query", response_model=ApiResponse[dict])
def submit_query(
    request: QueryRequestModel,
    user: User = Depends(get_current_user)
):
    from eakip_agent_orchestrator.orchestrator import AgentOrchestrator
    orchestrator = AgentOrchestrator()
    result = orchestrator.process_query(user.id, request.query)
    
    # Map result details
    res_dict = {
        "query": result.query,
        "response": result.response,
        "confidenceScore": result.confidence_score,
        "executionSteps": result.execution_steps
    }
    return ApiResponse.success_response(res_dict, "Query orchestrated successfully")

@router.get("/metrics", response_model=ApiResponse[dict])
def get_telemetry():
    from eakip_agent_orchestrator.observability import AiMetricsCollector
    metrics = AiMetricsCollector()
    data = {
        "totalTokens": metrics.get_total_tokens(),
        "requestsCount": metrics.get_requests_count(),
        "failuresCount": metrics.get_failures_count(),
        "averageLatencyMs": metrics.get_average_latency_ms()
    }
    return ApiResponse.success_response(data, "Telemetry logs loaded")
