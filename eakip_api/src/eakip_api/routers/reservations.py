import uuid
from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from eakip_core.common.dto import ApiResponse
from eakip_core.domain.models import User
from eakip_core.domain.repositories import BookRepository, ReservationRepository, UserRepository
from eakip_core.usecase.services import ReservationService
from ..dto import ReservationResponse
from ..mapper import DomainMapper
from ..config.db import get_db
from ..config.security import get_current_user

router = APIRouter(prefix="/api/v1/reservations", tags=["Reservation Hold API"])

def get_reservation_service(db: Session = Depends(get_db)) -> ReservationService:
    return ReservationService(
        reservation_repo=ReservationRepository(db),
        book_repo=BookRepository(db),
        user_repo=UserRepository(db)
    )

@router.post("/books/{book_id}/reserve", response_model=ApiResponse[ReservationResponse])
def reserve_book(
    book_id: uuid.UUID,
    user: User = Depends(get_current_user),
    service: ReservationService = Depends(get_reservation_service)
):
    reservation = service.reserve_book(user.id, book_id)
    response = DomainMapper.to_reservation_response(reservation)
    return ApiResponse.success_response(response, "Book reserved successfully")

@router.post("/{reservation_id}/cancel", response_model=ApiResponse[None])
def cancel_reservation(
    reservation_id: uuid.UUID,
    service: ReservationService = Depends(get_reservation_service)
):
    service.cancel_reservation(reservation_id)
    return ApiResponse.empty_success("Reservation cancelled successfully")
