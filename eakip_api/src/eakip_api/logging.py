import time
import uuid
import logging
from fastapi import Request
from starlette.middleware.base import BaseHTTPMiddleware

logger = logging.getLogger(__name__)

class RequestResponseLoggingMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        # Generate correlation ID
        correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
        
        # Inject correlation id to extra context for format loggers
        logger.info(
            f"Incoming HTTP Request: method={request.method}, uri={request.url.path}, correlationId={correlation_id}"
        )
        
        start_time = time.time()
        try:
            response = await call_next(request)
            duration = int((time.time() - start_time) * 1000)
            logger.info(
                f"Outgoing HTTP Response: method={request.method}, uri={request.url.path}, status={response.status_code}, duration={duration}ms, correlationId={correlation_id}"
            )
            # Add correlation id header to response
            response.headers["X-Correlation-ID"] = correlation_id
            return response
        except Exception as e:
            duration = int((time.time() - start_time) * 1000)
            logger.error(
                f"Failed HTTP Response: method={request.method}, uri={request.url.path}, error={str(e)}, duration={duration}ms, correlationId={correlation_id}"
            )
            raise e
