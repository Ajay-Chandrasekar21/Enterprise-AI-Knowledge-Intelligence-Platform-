import uuid
from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from eakip_core.common.dto import ApiResponse
from eakip_core.domain.models import User
from eakip_core.domain.repositories import BookRepository, BorrowingRepository, UserRepository, FineRepository
from eakip_core.usecase.services import BorrowingService
from ..dto import BorrowResponse
from ..mapper import DomainMapper
from ..config.db import get_db
from ..config.security import get_current_user

router = APIRouter(prefix="/api/v1/borrowings", tags=["Borrowing Operations API"])

def get_borrowing_service(db: Session = Depends(get_db)) -> BorrowingService:
    return BorrowingService(
        borrowing_repo=BorrowingRepository(db),
        book_repo=BookRepository(db),
        user_repo=UserRepository(db),
        fine_repo=FineRepository(db)
    )

@router.post("/books/{book_id}/borrow", response_model=ApiResponse[BorrowResponse])
def borrow_book(
    book_id: uuid.UUID,
    user: User = Depends(get_current_user),
    service: BorrowingService = Depends(get_borrowing_service)
):
    borrowing = service.borrow_book(user.id, book_id)
    response = DomainMapper.to_borrow_response(borrowing)
    return ApiResponse.success_response(response, "Book borrowed successfully")

@router.post("/{borrowing_id}/return", response_model=ApiResponse[BorrowResponse])
def return_book(
    borrowing_id: uuid.UUID,
    service: BorrowingService = Depends(get_borrowing_service)
):
    borrowing = service.return_book(borrowing_id)
    response = DomainMapper.to_borrow_response(borrowing)
    return ApiResponse.success_response(response, "Book returned successfully")

@router.post("/{borrowing_id}/renew", response_model=ApiResponse[BorrowResponse])
def renew_book(
    borrowing_id: uuid.UUID,
    service: BorrowingService = Depends(get_borrowing_service)
):
    borrowing = service.renew_book(borrowing_id)
    response = DomainMapper.to_borrow_response(borrowing)
    return ApiResponse.success_response(response, "Book renewed successfully")
