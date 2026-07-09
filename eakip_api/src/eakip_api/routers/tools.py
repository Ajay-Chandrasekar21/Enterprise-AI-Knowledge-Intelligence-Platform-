from typing import List, Dict, Any
from fastapi import APIRouter, Query, Body
from eakip_core.common.dto import ApiResponse

router = APIRouter(prefix="/api/v1/tools", tags=["Agent Tools Ecosystem API"])

@router.get("", response_model=ApiResponse[List[dict]])
def list_tools():
    from eakip_agent_orchestrator.tools import ToolRegistry
    registry = ToolRegistry()
    manifests = []
    
    for tool in registry.get_all_tools():
        category = "AI_UTILITY"
        name = tool.name.lower()
        if any(w in name for w in ["library", "borrow", "return", "reservation", "fine"]):
            category = "LIBRARY"
        elif any(w in name for w in ["knowledge", "citation", "summary", "rag"]):
            category = "KNOWLEDGE"
        elif any(w in name for w in ["analytics", "report", "statistics"]):
            category = "ANALYTICS"
        elif any(w in name for w in ["user", "profile", "history"]):
            category = "USER"
        elif any(w in name for w in ["communication", "email", "webhook", "notification"]):
            category = "COMMUNICATION"
            
        manifests.append({
            "name": tool.name,
            "description": tool.description,
            "category": category,
            "parameters": tool.parameters,
            "requiredPermission": "USER",
            "version": "1.0.0"
        })
    return ApiResponse.success_response(manifests, "Tools registry loaded")

@router.post("/execute", response_model=ApiResponse[Any])
def execute_tool(
    tool_name: str = Query(..., alias="toolName"),
    arguments: Dict[str, Any] = Body(...)
):
    from eakip_agent_orchestrator.tools import ToolRegistry
    from eakip_agent_orchestrator.executor import ToolExecutor
    
    registry = ToolRegistry()
    executor = ToolExecutor()
    
    tool = registry.get_tool(tool_name)
    if not tool:
        raise ValueError(f"No tool found matching name: {tool_name}")
        
    output = executor.execute_with_policies(tool, arguments)
    return ApiResponse.success_response(output, "Tool execution completed successfully")

@router.get("/history", response_model=ApiResponse[Dict[str, List[dict]]])
def get_history():
    from eakip_agent_orchestrator.executor import ToolExecutor
    executor = ToolExecutor()
    history = executor.get_all_history()
    
    # Map execution record list elements to dict serializable models
    result = {}
    for key, records in history.items():
        result[key] = [{
            "toolName": r.tool_name,
            "status": r.status,
            "latencyMs": r.latency_ms,
            "errorMessage": r.error_message,
            "timestamp": r.timestamp.isoformat()
        } for r in records]
        
    return ApiResponse.success_response(result, "Execution history loaded")
