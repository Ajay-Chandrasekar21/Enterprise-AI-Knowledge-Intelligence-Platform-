from datetime import datetime
from typing import Generic, TypeVar, Optional, List, Any
from pydantic import BaseModel, Field, ConfigDict
from pydantic.alias_generators import to_camel

T = TypeVar('T')

class CamelCaseModel(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_camel,
        populate_by_name=True,
        from_attributes=True
    )

class ApiResponse(CamelCaseModel, Generic[T]):
    success: bool
    message: str
    data: Optional[T] = None
    timestamp: datetime = Field(default_factory=datetime.utcnow)

    @classmethod
    def success_response(cls, data: T, message: str = "Operation completed successfully") -> "ApiResponse[T]":
        return cls(success=True, message=message, data=data)

    @classmethod
    def empty_success(cls, message: str) -> "ApiResponse[None]":
        return cls(success=True, message=message, data=None)

class ApiErrorResponse(CamelCaseModel):
    success: bool = False
    error_code: str
    message: str
    correlation_id: str
    details: List[str] = Field(default_factory=list)
    timestamp: datetime = Field(default_factory=datetime.utcnow)

class SearchRequest(CamelCaseModel):
    query: str = ""
    page_number: int = Field(default=0, ge=0)
    page_size: int = Field(default=10, ge=1, le=100)
    sort_by: str = "id"
    sort_direction: str = "ASC"

class PageResponse(CamelCaseModel, Generic[T]):
    content: List[T]
    page_number: int
    page_size: int
    total_elements: int
    total_pages: int
    is_first: bool
    is_last: bool
