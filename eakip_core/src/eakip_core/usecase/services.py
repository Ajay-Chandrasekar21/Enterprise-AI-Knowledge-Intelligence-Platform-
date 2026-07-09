import uuid
import csv
import io
from datetime import datetime, timedelta
from typing import List, Optional
from decimal import Decimal

from ..domain.exception import EntityNotFoundException, ResourceConflictException, DomainException
import logging
from ..domain.models import Book, Borrowing, Reservation, ReadingSession, BookReview, Fine, User, Subscription
from ..domain.repositories import (
    BookRepository, UserRepository, BorrowingRepository, 
    ReservationRepository, CategoryRepository, PublisherRepository, 
    AuthorRepository, FineRepository, ReadingSessionRepository, 
    BookReviewRepository, SubscriptionRepository
)

logger = logging.getLogger(__name__)

class BookService:
    def __init__(
        self,
        book_repo: BookRepository,
        author_repo: AuthorRepository,
        publisher_repo: PublisherRepository,
        category_repo: CategoryRepository
    ):
        self.book_repo = book_repo
        self.author_repo = author_repo
        self.publisher_repo = publisher_repo
        self.category_repo = category_repo

    def search_books(
        self, 
        title: Optional[str], 
        category_id: Optional[uuid.UUID], 
        page: int, 
        size: int, 
        sort_by: str, 
        direction: str
    ) -> List[Book]:
        # Simple list slice return to simulate pagination specs mapping
        books = self.book_repo.find_all()
        # Sort
        reverse = direction.upper() == "DESC"
        books.sort(key=lambda b: getattr(b, sort_by, b.id), reverse=reverse)
        
        # Paginate
        start = page * size
        end = start + size
        return books[start:end]

    def create_book(self, book: Book, author_ids: Optional[List[uuid.UUID]], publisher_id: Optional[uuid.UUID], category_id: Optional[uuid.UUID]) -> Book:
        if self.book_repo.exists_by_isbn(book.isbn):
            raise ResourceConflictException(f"Book with ISBN {book.isbn} already exists")

        if publisher_id:
            publisher = self.publisher_repo.find_by_id(publisher_id)
            if not publisher:
                raise EntityNotFoundException("Publisher not found")
            book.publisher = publisher

        if category_id:
            category = self.category_repo.find_by_id(category_id)
            if not category:
                raise EntityNotFoundException("Category not found")
            book.category = category

        if author_ids:
            authors = []
            for aid in author_ids:
                author = self.author_repo.find_by_id(aid)
                if author:
                    authors.append(author)
            book.authors = authors

        return self.book_repo.save(book)

    def update_book(self, book_id: uuid.UUID, book_details: Book, author_ids: Optional[List[uuid.UUID]], publisher_id: Optional[uuid.UUID], category_id: Optional[uuid.UUID]) -> Book:
        book = self.book_repo.find_by_id(book_id)
        if not book:
            raise EntityNotFoundException("Book not found")

        book.title = book_details.title
        book.description = book_details.description
        book.edition = book_details.edition
        book.language = book_details.language
        book.shelf = book_details.shelf
        book.rack = book_details.rack
        book.total_copies = book_details.total_copies
        book.available_copies = book_details.available_copies

        if publisher_id:
            publisher = self.publisher_repo.find_by_id(publisher_id)
            if not publisher:
                raise EntityNotFoundException("Publisher not found")
            book.publisher = publisher

        if category_id:
            category = self.category_repo.find_by_id(category_id)
            if not category:
                raise EntityNotFoundException("Category not found")
            book.category = category

        if author_ids is not None:
            authors = []
            for aid in author_ids:
                author = self.author_repo.find_by_id(aid)
                if author:
                    authors.append(author)
            book.authors = authors

        return self.book_repo.save(book)

    def delete_book(self, book_id: uuid.UUID) -> None:
        book = self.book_repo.find_by_id(book_id)
        if not book:
            raise EntityNotFoundException("Book not found")
        book.deleted = True
        self.book_repo.save(book)

class BorrowingService:
    MAX_STUDENT_BORROW = 3
    MAX_FACULTY_BORROW = 10
    STUDENT_BORROW_DAYS = 14
    FACULTY_BORROW_DAYS = 90
    MAX_RENEWALS = 2
    FINE_RATE_PER_DAY = Decimal("1.00")

    def __init__(
        self,
        borrowing_repo: BorrowingRepository,
        book_repo: BookRepository,
        user_repo: UserRepository,
        fine_repo: FineRepository
    ):
        self.borrowing_repo = borrowing_repo
        self.book_repo = book_repo
        self.user_repo = user_repo
        self.fine_repo = fine_repo

    def borrow_book(self, user_id: uuid.UUID, book_id: uuid.UUID) -> Borrowing:
        user = self.user_repo.find_by_id(user_id)
        if not user:
            raise EntityNotFoundException("User not found")
        book = self.book_repo.find_by_id(book_id)
        if not book:
            raise EntityNotFoundException("Book not found")

        # 1. Verify borrowing limits
        active_borrow_count = self.borrowing_repo.count_by_user_id_and_status(user_id, "BORROWED")
        if user.role == "STUDENT" and active_borrow_count >= self.MAX_STUDENT_BORROW:
            raise ResourceConflictException(f"Students cannot exceed {self.MAX_STUDENT_BORROW} active borrowings")
        if user.role == "FACULTY" and active_borrow_count >= self.MAX_FACULTY_BORROW:
            raise ResourceConflictException(f"Faculty cannot exceed {self.MAX_FACULTY_BORROW} active borrowings")

        # 2. Check copies
        if book.available_copies <= 0:
            raise ResourceConflictException("Book is currently unavailable. Please place a reservation.")

        # 3. Decrement availability
        book.available_copies -= 1
        self.book_repo.save(book)

        borrow_days = self.FACULTY_BORROW_DAYS if user.role == "FACULTY" else self.STUDENT_BORROW_DAYS
        now = datetime.utcnow()

        borrowing = Borrowing(
            user=user,
            book=book,
            borrow_date=now,
            due_date=now + timedelta(days=borrow_days),
            status="BORROWED",
            renewal_count=0
        )
        return self.borrowing_repo.save(borrowing)

    def return_book(self, borrowing_id: uuid.UUID) -> Borrowing:
        borrowing = self.borrowing_repo.find_by_id(borrowing_id)
        if not borrowing:
            raise EntityNotFoundException("Borrowing record not found")

        if borrowing.status != "BORROWED":
            raise ResourceConflictException("Book is already returned")

        now = datetime.utcnow()
        borrowing.return_date = now
        borrowing.status = "RETURNED"

        # Increment availability
        book = borrowing.book
        book.available_copies += 1
        self.book_repo.save(book)

        # Fines
        if now > borrowing.due_date:
            days_late = (now - borrowing.due_date).days
            if days_late > 0:
                fine_amount = self.FINE_RATE_PER_DAY * Decimal(days_late)
                fine = Fine(
                    borrowing=borrowing,
                    amount=fine_amount,
                    status="UNPAID",
                    generated_date=now
                )
                self.fine_repo.save(fine)

        return self.borrowing_repo.save(borrowing)

    def renew_book(self, borrowing_id: uuid.UUID) -> Borrowing:
        borrowing = self.borrowing_repo.find_by_id(borrowing_id)
        if not borrowing:
            raise EntityNotFoundException("Borrowing record not found")

        if borrowing.status != "BORROWED":
            raise ResourceConflictException("Cannot renew a returned book")

        if borrowing.renewal_count >= self.MAX_RENEWALS:
            raise ResourceConflictException(f"Exceeded maximum renewals count of {self.MAX_RENEWALS}")

        borrowing.renewal_count += 1
        borrowing.due_date += timedelta(days=self.STUDENT_BORROW_DAYS)

        return self.borrowing_repo.save(borrowing)

class ReservationService:
    def __init__(
        self,
        reservation_repo: ReservationRepository,
        book_repo: BookRepository,
        user_repo: UserRepository
    ):
        self.reservation_repo = reservation_repo
        self.book_repo = book_repo
        self.user_repo = user_repo

    def reserve_book(self, user_id: uuid.UUID, book_id: uuid.UUID) -> Reservation:
        user = self.user_repo.find_by_id(user_id)
        if not user:
            raise EntityNotFoundException("User not found")
        book = self.book_repo.find_by_id(book_id)
        if not book:
            raise EntityNotFoundException("Book not found")

        if book.available_copies > 0:
            raise ResourceConflictException("Book is currently available for borrowing. Check out instead.")

        already_reserved = self.reservation_repo.find_by_book_and_user_and_status(book_id, user_id, "PENDING")
        if already_reserved:
            raise ResourceConflictException("You already have an active reservation hold for this book")

        max_pos = self.reservation_repo.find_max_queue_position_by_book_id(book_id)

        reservation = Reservation(
            user=user,
            book=book,
            reservation_date=datetime.utcnow(),
            status="PENDING",
            queue_position=max_pos + 1
        )
        return self.reservation_repo.save(reservation)

    def cancel_reservation(self, reservation_id: uuid.UUID) -> None:
        reservation = self.reservation_repo.find_by_id(reservation_id)
        if not reservation:
            raise EntityNotFoundException("Reservation not found")

        if reservation.status != "PENDING":
            raise ResourceConflictException("Cannot cancel a completed/fulfilled reservation")

        reservation.status = "CANCELLED"
        reservation.queue_position = 0
        self.reservation_repo.save(reservation)

        # Re-number
        remaining = self.reservation_repo.find_by_book_id_and_status_order_by_date_asc(
            reservation.book.id, "PENDING"
        )
        for idx, rem in enumerate(remaining):
            rem.queue_position = idx + 1
            self.reservation_repo.save(rem)

class ReadingHistoryService:
    def __init__(
        self,
        rsession_repo: ReadingSessionRepository,
        review_repo: BookReviewRepository,
        book_repo: BookRepository,
        user_repo: UserRepository
    ):
        self.rsession_repo = rsession_repo
        self.review_repo = review_repo
        self.book_repo = book_repo
        self.user_repo = user_repo

    def start_reading_session(self, user_id: uuid.UUID, book_id: uuid.UUID) -> ReadingSession:
        user = self.user_repo.find_by_id(user_id)
        if not user:
            raise EntityNotFoundException("User not found")
        book = self.book_repo.find_by_id(book_id)
        if not book:
            raise EntityNotFoundException("Book not found")

        active_exists = self.rsession_repo.find_by_user_id_and_book_id_and_end_time_is_null(user_id, book_id)
        if active_exists:
            raise ResourceConflictException("You already have an active reading session for this book")

        rsession = ReadingSession(
            user=user,
            book=book,
            start_time=datetime.utcnow(),
            pages_read=0,
            progress_percentage=0
        )
        return self.rsession_repo.save(rsession)

    def end_reading_session(self, user_id: uuid.UUID, book_id: uuid.UUID, pages_read: int, progress_percentage: int) -> ReadingSession:
        session = self.rsession_repo.find_by_user_id_and_book_id_and_end_time_is_null(user_id, book_id)
        if not session:
            raise EntityNotFoundException("Active reading session not found")

        session.end_time = datetime.utcnow()
        session.pages_read = pages_read
        session.progress_percentage = progress_percentage
        return self.rsession_repo.save(session)

    def add_review(self, user_id: uuid.UUID, book_id: uuid.UUID, rating: int, comment: str) -> BookReview:
        user = self.user_repo.find_by_id(user_id)
        if not user:
            raise EntityNotFoundException("User not found")
        book = self.book_repo.find_by_id(book_id)
        if not book:
            raise EntityNotFoundException("Book not found")

        if rating < 1 or rating > 5:
            raise ValueError("Rating must be between 1 and 5")

        review = BookReview(
            user=user,
            book=book,
            rating=rating,
            comment=comment,
            review_date=datetime.utcnow()
        )
        return self.review_repo.save(review)

    def get_sessions(self, user_id: uuid.UUID) -> List[ReadingSession]:
        return self.rsession_repo.find_by_user_id(user_id)

class ExportService:
    def __init__(self, book_repo: BookRepository, borrowing_repo: BorrowingRepository):
        self.book_repo = book_repo
        self.borrowing_repo = borrowing_repo

    def export_books_to_csv(self) -> str:
        books = self.book_repo.find_all()
        output = io.StringIO()
        writer = csv.writer(output, lineterminator='\n')
        writer.writerow(["ID", "ISBN", "Title", "Category", "Available Copies", "Total Copies"])
        for book in books:
            category_name = book.category.name if book.category else ""
            writer.writerow([
                str(book.id),
                book.isbn,
                book.title,
                category_name,
                book.available_copies,
                book.total_copies
            ])
        return output.getvalue()

    def export_borrowings_to_csv(self) -> str:
        borrowings = self.borrowing_repo.find_all() # Or implement find_all in repo
        # Wait, let's select all since borrowing_repo inherits JpaRepository
        # Let's map it:
        output = io.StringIO()
        writer = csv.writer(output, lineterminator='\n')
        writer.writerow(["Borrow ID", "User", "Book", "Borrow Date", "Due Date", "Status"])
        # Wait, let's query all borrowings
        # For simplicity, self.borrowing_repo has session which can select all:
        from sqlalchemy import select
        borrowings = list(self.borrowing_repo.session.execute(select(Borrowing)).scalars().all())
        for b in borrowings:
            writer.writerow([
                str(b.id),
                b.user.username,
                b.book.title,
                b.borrow_date.isoformat(),
                b.due_date.isoformat(),
                b.status
            ])
        return output.getvalue()

class BillingService:
    def __init__(self, subscription_repo: SubscriptionRepository):
        self.subscription_repo = subscription_repo

    def track_usage(self, tenant_id: uuid.UUID, tokens_count: int) -> None:
        logger.info(f"Tracking SaaS token usage. Tenant: {tenant_id}, Tokens: {tokens_count}")
        subscription = self.subscription_repo.find_by_tenant_id(tenant_id)
        if not subscription:
            subscription = self._create_default_subscription(tenant_id)

        subscription.consumed_tokens += tokens_count
        self.subscription_repo.save(subscription)

    def calculate_cost(self, tenant_id: uuid.UUID) -> dict:
        subscription = self.subscription_repo.find_by_tenant_id(tenant_id)
        if not subscription:
            subscription = self._create_default_subscription(tenant_id)

        tokens = subscription.consumed_tokens
        cost = (tokens / 1000000.0) * float(subscription.cost_per_million_tokens)

        return {
            "tenantId": tenant_id,
            "planTier": subscription.plan_tier,
            "consumedTokens": tokens,
            "quotaLimit": subscription.max_token_quota,
            "estimatedCost": cost,
            "currency": "USD"
        }

    def _create_default_subscription(self, tenant_id: uuid.UUID) -> Subscription:
        sub = Subscription(
            tenant_id=tenant_id,
            plan_tier="FREE",
            max_token_quota=1000000,
            consumed_tokens=0,
            cost_per_million_tokens=15.00,
            rate_limit_per_min=60
        )
        return self.subscription_repo.save(sub)

