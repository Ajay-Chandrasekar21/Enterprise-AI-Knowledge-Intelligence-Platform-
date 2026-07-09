from typing import List, Optional
from eakip_core.domain.models import Book, Borrowing, Reservation
from .dto import BookResponse, BorrowResponse, ReservationResponse

class DomainMapper:
    @staticmethod
    def to_book_response(book: Optional[Book]) -> Optional[BookResponse]:
        if not book:
            return None
        author_names = [a.name for a in book.authors] if book.authors else []
        publisher_name = book.publisher.name if book.publisher else None
        category_name = book.category.name if book.category else None
        
        return BookResponse(
            id=book.id,
            title=book.title,
            isbn=book.isbn,
            description=book.description,
            publisher_name=publisher_name,
            category_name=category_name,
            author_names=author_names,
            edition=book.edition,
            language=book.language,
            shelf=book.shelf,
            rack=book.rack,
            total_copies=book.total_copies,
            available_copies=book.available_copies
        )

    @staticmethod
    def to_borrow_response(borrowing: Optional[Borrowing]) -> Optional[BorrowResponse]:
        if not borrowing:
            return None
        return BorrowResponse(
            id=borrowing.id,
            username=borrowing.user.username,
            book_title=borrowing.book.title,
            isbn=borrowing.book.isbn,
            borrow_date=borrowing.borrow_date,
            due_date=borrowing.due_date,
            return_date=borrowing.return_date,
            status=borrowing.status,
            renewal_count=borrowing.renewal_count
        )

    @staticmethod
    def to_reservation_response(reservation: Optional[Reservation]) -> Optional[ReservationResponse]:
        if not reservation:
            return None
        return ReservationResponse(
            id=reservation.id,
            username=reservation.user.username,
            book_title=reservation.book.title,
            reservation_date=reservation.reservation_date,
            status=reservation.status,
            queue_position=reservation.queue_position
        )
