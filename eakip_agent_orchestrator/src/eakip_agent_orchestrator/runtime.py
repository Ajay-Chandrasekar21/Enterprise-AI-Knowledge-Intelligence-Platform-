from typing import Optional

class AgentResult:
    def __init__(
        self,
        agent_name: str,
        output_content: str,
        state: str,
        confidence: float,
        error_message: Optional[str] = None,
        execution_time_ms: Optional[int] = None
    ):
        self.agent_name = agent_name
        self.output_content = output_content
        self.state = state
        self.confidence = confidence
        self.error_message = error_message
        self.execution_time_ms = execution_time_ms

    def get_agent_name(self) -> str:
        return self.agent_name

    def get_output_content(self) -> str:
        return self.output_content

    def get_confidence(self) -> float:
        return self.confidence
