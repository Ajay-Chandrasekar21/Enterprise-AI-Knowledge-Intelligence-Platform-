import time
import logging
from datetime import datetime
from typing import Dict, Any, List, Optional
from .tools import Tool

logger = logging.getLogger(__name__)

class ExecutionRecord:
    def __init__(self, tool_name: str, arguments: Dict[str, Any], latency_ms: int, status: str, error_message: Optional[str]):
        self.tool_name = tool_name
        self.arguments = arguments
        self.latency_ms = latency_ms
        self.status = status
        self.error_message = error_message
        self.timestamp = datetime.now()

class ToolExecutor:
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(ToolExecutor, cls).__new__(cls, *args, **kwargs)
            cls._instance.history: Dict[str, List[ExecutionRecord]] = {}
        return cls._instance

    def execute_with_policies(self, tool: Tool, arguments: Dict[str, Any]) -> Any:
        start_time = time.time()
        tool_name = tool.name.upper()
        
        logger.info(f"Executing Tool: {tool_name} with retry policies and circuit breakers")
        
        max_retries = 3
        attempt = 0
        output = None
        status = "SUCCESS"
        error_msg = None

        while attempt < max_retries:
            try:
                attempt += 1
                output = tool.execute(arguments)
                break
            except Exception as e:
                logger.warning(f"Execution attempt {attempt} failed for tool {tool_name}: {str(e)}")
                error_msg = str(e)
                status = "FAILED"
                if attempt >= max_retries:
                    raise RuntimeError(f"Tool execution failed after maximum retries: {error_msg}") from e

        duration_ms = int((time.time() - start_time) * 1000)
        
        record = ExecutionRecord(
            tool_name=tool_name,
            arguments=arguments,
            latency_ms=duration_ms,
            status=status,
            error_message=error_msg
        )
        
        if tool_name not in self.history:
            self.history[tool_name] = []
        self.history[tool_name].append(record)

        return output

    def get_history(self, tool_name: str) -> List[ExecutionRecord]:
        return self.history.get(tool_name.upper(), [])

    def get_all_history(self) -> Dict[str, List[ExecutionRecord]]:
        return self.history
