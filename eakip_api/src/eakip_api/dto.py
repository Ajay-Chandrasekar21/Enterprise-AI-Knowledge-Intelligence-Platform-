import uuid
from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, Field, ConfigDict
from pydantic.alias_generators import to_camel

class CamelCaseModel(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_camel,
        populate_by_name=True,
        from_attributes=True
    )

class BookRequest(CamelCaseModel):
    title: str = Field(..., min_length=1)
    isbn: str = Field(..., min_length=1)
    description: Optional[str] = None
    publisher_id: Optional[uuid.UUID] = None
    category_id: Optional[uuid.UUID] = None
    author_ids: Optional[List[uuid.UUID]] = None
    edition: Optional[str] = None
    language: Optional[str] = None
    shelf: Optional[str] = None
    rack: Optional[str] = None
    total_copies: int = Field(..., ge=1)

class BookResponse(CamelCaseModel):
    id: uuid.UUID
    title: str
    isbn: str
    description: Optional[str] = None
    publisher_name: Optional[str] = None
    category_name: Optional[str] = None
    author_names: List[str] = Field(default_factory=list)
    edition: Optional[str] = None
    language: Optional[str] = None
    shelf: Optional[str] = None
    rack: Optional[str] = None
    total_copies: int
    available_copies: int

class BorrowResponse(CamelCaseModel):
    id: uuid.UUID
    username: str
    book_title: str
    isbn: str
    borrow_date: datetime
    due_date: datetime
    return_date: Optional[datetime] = None
    status: str
    renewal_count: int

class ReservationResponse(CamelCaseModel):
    id: uuid.UUID
    username: str
    book_title: str
    reservation_date: datetime
    status: str
    queue_position: int
