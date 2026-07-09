import time
import logging
from typing import Dict, Any, List, Optional

logger = logging.getLogger(__name__)

class McpServerConfig:
    def __init__(
        self,
        server_name: str,
        endpoint: str,
        status: str = "DISCONNECTED",
        latency_ms: int = 0,
        auth_type: Optional[str] = None,
        api_key: Optional[str] = None
    ):
        self.server_name = server_name
        self.endpoint_url = endpoint
        self.status = status
        self.latency_ms = latency_ms
        self.auth_type = auth_type
        self.api_key = api_key

    @property
    def endpoint(self) -> str:
        return self.endpoint_url

    @endpoint.setter
    def endpoint(self, val: str) -> None:
        self.endpoint_url = val

class McpServerRegistry:
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(McpServerRegistry, cls).__new__(cls, *args, **kwargs)
            cls._instance.registry: Dict[str, McpServerConfig] = {}
        return cls._instance

    # To satisfy dynamic config mappings instantiation
    McpServerConfig = McpServerConfig

    def register_server(self, config: McpServerConfig) -> None:
        self.registry[config.server_name.upper()] = config

    def unregister_server(self, server_name: str) -> None:
        if server_name.upper() in self.registry:
            del self.registry[server_name.upper()]

    def getAllServers(self) -> List[McpServerConfig]:
        # Also map snake case
        return self.get_all_servers()

    def get_all_servers(self) -> List[McpServerConfig]:
        return list(self.registry.values())

    def getServer(self, server_name: str) -> Optional[McpServerConfig]:
        return self.get_server(server_name)

    def get_server(self, server_name: str) -> Optional[McpServerConfig]:
        return self.registry.get(server_name.upper())

class McpToolSchema:
    def __init__(self, name: str, description: str, parameters: Dict[str, str]):
        self.name = name
        self.description = description
        self.parameters = parameters

class McpConnector:
    @staticmethod
    def get_connector_tools(server_name: str) -> List[McpToolSchema]:
        schemas = []
        name_lower = server_name.lower()
        if name_lower == "github":
            schemas.append(McpToolSchema("createIssue", "Creates a GitHub issue in the target repository", {"repo": "String", "title": "String"}))
            schemas.append(McpToolSchema("listPRs", "Lists open pull requests", {"repo": "String"}))
        elif name_lower == "jira":
            schemas.append(McpToolSchema("createTicket", "Creates a Jira ticket", {"project": "String", "summary": "String"}))
        elif name_lower == "slack":
            schemas.append(McpToolSchema("postMessage", "Dispatches alert messages to Slack channels", {"channel": "String", "text": "String"}))
        else:
            schemas.append(McpToolSchema("queryData", "Executes data query operations", {"query": "String"}))
        return schemas

class McpClient:
    @staticmethod
    def callMcpTool(server: McpServerConfig, tool_name: str, arguments: Dict[str, Any]) -> Any:
        return McpClient.call_mcp_tool(server, tool_name, arguments)

    @staticmethod
    def call_mcp_tool(server: McpServerConfig, tool_name: str, arguments: Dict[str, Any]) -> Any:
        logger.info(f"Dispatching MCP JSON-RPC request to server: {server.server_name}, endpoint: {server.endpoint_url}")
        logger.debug(f"Authenticating using credentials mapping: {server.auth_type}")

        # Simulate network latency
        time.sleep(max(10, server.latency_ms) / 1000.0)

        result = {
            "tool": tool_name,
            "content": f"Successful payload output returned from external MCP host {server.server_name}"
        }
        return {
            "jsonrpc": "2.0",
            "status": "SUCCESS",
            "result": result
        }
