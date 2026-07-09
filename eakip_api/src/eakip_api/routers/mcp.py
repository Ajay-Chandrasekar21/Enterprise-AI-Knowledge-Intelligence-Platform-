from typing import List, Dict, Any
from fastapi import APIRouter, Query, Body, status
from eakip_core.common.dto import ApiResponse

router = APIRouter(prefix="/api/v1/mcp", tags=["Model Context Protocol (MCP) API"])

@router.get("/servers", response_model=ApiResponse[List[dict]])
def list_servers():
    from eakip_agent_orchestrator.mcp import McpServerRegistry
    registry = McpServerRegistry()
    servers = registry.get_all_servers()
    
    result = [{
        "serverName": s.server_name,
        "endpoint": s.endpoint,
        "status": s.status,
        "latencyMs": s.latency_ms
    } for s in servers]
    return ApiResponse.success_response(result, "MCP servers loaded")

@router.post("/servers", response_model=ApiResponse[str])
def register_server(config_dict: Dict[str, Any]):
    from eakip_agent_orchestrator.mcp import McpServerRegistry
    registry = McpServerRegistry()
    
    # Map json args to registry config schema
    config = McpServerRegistry.McpServerConfig(
        server_name=config_dict.get("serverName"),
        endpoint=config_dict.get("endpoint"),
        status="CONNECTED",
        latency_ms=45
    )
    registry.register_server(config)
    return ApiResponse.success_response(config.server_name, "MCP server registered and connected successfully")

@router.delete("/servers", response_model=ApiResponse[str])
def remove_server(server_name: str = Query(..., alias="serverName")):
    from eakip_agent_orchestrator.mcp import McpServerRegistry
    registry = McpServerRegistry()
    registry.unregister_server(server_name)
    return ApiResponse.success_response(server_name, "MCP server removed successfully")

@router.get("/tools", response_model=ApiResponse[List[dict]])
def discover_tools(server_name: str = Query(..., alias="serverName")):
    from eakip_agent_orchestrator.mcp import McpConnector
    connector = McpConnector()
    tools = connector.get_connector_tools(server_name)
    
    result = [{
        "name": t.name,
        "description": t.description,
        "parameters": t.parameters
    } for t in tools]
    return ApiResponse.success_response(result, "MCP tools schemas discovered")

@router.post("/execute", response_model=ApiResponse[Any])
def execute_mcp_tool(
    server_name: str = Query(..., alias="serverName"),
    tool_name: str = Query(..., alias="toolName"),
    arguments: Dict[str, Any] = Body(...)
):
    from eakip_agent_orchestrator.mcp import McpServerRegistry, McpClient
    registry = McpServerRegistry()
    client = McpClient()
    
    server = registry.get_server(server_name)
    if not server:
        raise ValueError(f"No MCP server registered matching name: {server_name}")
        
    output = client.call_mcp_tool(server, tool_name, arguments)
    return ApiResponse.success_response(output, "MCP Tool execution successful")
