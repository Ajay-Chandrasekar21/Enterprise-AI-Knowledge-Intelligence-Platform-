import uuid
from typing import Optional, List
from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session
from eakip_core.common.dto import ApiResponse, PageResponse
from eakip_core.domain.models import Book
from eakip_core.domain.repositories import BookRepository, AuthorRepository, PublisherRepository, CategoryRepository
from eakip_core.usecase.services import BookService
from ..dto import BookRequest, BookResponse
from ..mapper import DomainMapper
from ..config.db import get_db
from ..config.security import require_role

router = APIRouter(prefix="/api/v1/books", tags=["Book Catalog API"])

def get_book_service(db: Session = Depends(get_db)) -> BookService:
    return BookService(
        book_repo=BookRepository(db),
        author_repo=AuthorRepository(db),
        publisher_repo=PublisherRepository(db),
        category_repo=CategoryRepository(db)
    )

@router.get("/search", response_model=ApiResponse[PageResponse[BookResponse]])
def search(
    title: Optional[str] = None,
    category_id: Optional[uuid.UUID] = None,
    page: int = Query(default=0, ge=0),
    size: int = Query(default=10, ge=1, le=100),
    sort_by: str = Query(default="title"),
    direction: str = Query(default="ASC"),
    service: BookService = Depends(get_book_service)
):
    books = service.search_books(title, category_id, page, size, sort_by, direction)
    responses = [DomainMapper.to_book_response(b) for b in books]
    
    # Calculate mock total pages & elements count for uniform envelope mapping
    total_elements = len(responses)
    total_pages = 1 if total_elements > 0 else 0
    
    page_res = PageResponse(
        content=responses,
        page_number=page,
        page_size=size,
        total_elements=total_elements,
        total_pages=total_pages,
        is_first=page == 0,
        is_last=True
    )
    return ApiResponse.success_response(page_res, "Search books completed")

@router.post("", status_code=status.HTTP_201_CREATED, response_model=ApiResponse[BookResponse], dependencies=[Depends(require_role(["ADMIN", "LIBRARIAN"]))])
def create(request: BookRequest, service: BookService = Depends(get_book_service)):
    book = Book(
        title=request.title,
        isbn=request.isbn,
        description=request.description,
        edition=request.edition,
        language=request.language,
        shelf=request.shelf,
        rack=request.rack,
        total_copies=request.total_copies,
        available_copies=request.total_copies
    )
    saved_book = service.create_book(book, request.author_ids, request.publisher_id, request.category_id)
    response = DomainMapper.to_book_response(saved_book)
    return ApiResponse.success_response(response, "Book created successfully")

@router.put("/{book_id}", response_model=ApiResponse[BookResponse], dependencies=[Depends(require_role(["ADMIN", "LIBRARIAN"]))])
def update(book_id: uuid.UUID, request: BookRequest, service: BookService = Depends(get_book_service)):
    book_details = Book(
        title=request.title,
        description=request.description,
        edition=request.edition,
        language=request.language,
        shelf=request.shelf,
        rack=request.rack,
        total_copies=request.total_copies,
        available_copies=request.total_copies
    )
    updated_book = service.update_book(book_id, book_details, request.author_ids, request.publisher_id, request.category_id)
    response = DomainMapper.to_book_response(updated_book)
    return ApiResponse.success_response(response, "Book updated successfully")

@router.delete("/{book_id}", response_model=ApiResponse[None], dependencies=[Depends(require_role(["ADMIN", "LIBRARIAN"]))])
def delete(book_id: uuid.UUID, service: BookService = Depends(get_book_service)):
    service.delete_book(book_id)
    return ApiResponse.empty_success("Book deleted successfully")
