import logging
import uuid
from fastapi import Request, status
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from eakip_core.domain.exception import EntityNotFoundException, ResourceConflictException, UnauthorizedException
from eakip_core.common.dto import ApiErrorResponse

logger = logging.getLogger(__name__)

def register_exception_handlers(app):
    @app.exception_handler(EntityNotFoundException)
    async def entity_not_found_handler(request: Request, exc: EntityNotFoundException):
        logger.warning(f"Resource not found: {exc.message}")
        correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
        error = ApiErrorResponse(
            error_code="RESOURCE_NOT_FOUND",
            message=exc.message,
            correlation_id=correlation_id,
            details=[exc.message]
        )
        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content=error.model_dump(by_alias=True)
        )

    @app.exception_handler(ResourceConflictException)
    async def resource_conflict_handler(request: Request, exc: ResourceConflictException):
        logger.warning(f"Conflict detected: {exc.message}")
        correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
        error = ApiErrorResponse(
            error_code="RESOURCE_CONFLICT",
            message=exc.message,
            correlation_id=correlation_id,
            details=[exc.message]
        )
        return JSONResponse(
            status_code=status.HTTP_409_CONFLICT,
            content=error.model_dump(by_alias=True)
        )

    @app.exception_handler(UnauthorizedException)
    async def unauthorized_handler(request: Request, exc: UnauthorizedException):
        logger.warning(f"Unauthorized access attempt: {exc.message}")
        correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
        error = ApiErrorResponse(
            error_code="UNAUTHORIZED",
            message=exc.message,
            correlation_id=correlation_id,
            details=[exc.message]
        )
        return JSONResponse(
            status_code=status.HTTP_401_UNAUTHORIZED,
            content=error.model_dump(by_alias=True)
        )

    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(request: Request, exc: RequestValidationError):
        errors_list = [f"{'.'.join(str(p) for p in err['loc'])}: {err['msg']}" for err in exc.errors()]
        logger.warning(f"Validation failure: {errors_list}")
        correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
        error = ApiErrorResponse(
            error_code="VALIDATION_FAILED",
            message="Request fields validation failed",
            correlation_id=correlation_id,
            details=errors_list
        )
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content=error.model_dump(by_alias=True)
        )

    @app.exception_handler(Exception)
    async def general_exception_handler(request: Request, exc: Exception):
        logger.error("Internal server error occurred: ", exc_info=exc)
        correlation_id = request.headers.get("X-Correlation-ID", str(uuid.uuid4()))
        error = ApiErrorResponse(
            error_code="INTERNAL_SERVER_ERROR",
            message="An unexpected error occurred on the server",
            correlation_id=correlation_id,
            details=[str(exc)]
        )
        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content=error.model_dump(by_alias=True)
        )
