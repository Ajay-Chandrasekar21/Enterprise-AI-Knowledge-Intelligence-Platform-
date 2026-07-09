import logging

logger = logging.getLogger(__name__)

class AiMetricsCollector:
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(AiMetricsCollector, cls).__new__(cls, *args, **kwargs)
            cls._instance.total_tokens_consumed = 0
            cls._instance.total_requests = 0
            cls._instance.failed_requests = 0
            cls._instance.total_latency_ms = 0
        return cls._instance

    def log_metrics(self, provider: str, input_tokens: int, output_tokens: int, latency_ms: int) -> None:
        self.total_tokens_consumed += (input_tokens + output_tokens)
        self.total_requests += 1
        self.total_latency_ms += latency_ms
        logger.info(f"LLM Metrics Logged - Provider: {provider}, Input Tokens: {input_tokens}, Output Tokens: {output_tokens}, Latency: {latency_ms} ms")

    def log_failure(self, provider: str, error: str) -> None:
        self.failed_requests += 1
        self.total_requests += 1
        logger.error(f"LLM Provider Failure Logged - Provider: {provider}, Error: {error}")

    def get_total_tokens(self) -> int:
        return self.total_tokens_consumed

    def get_requests_count(self) -> int:
        return self.total_requests

    def get_failures_count(self) -> int:
        return self.failed_requests

    def get_average_latency_ms(self) -> float:
        reqs = self.total_requests
        return 0.0 if reqs == 0 else float(self.total_latency_ms) / reqs
