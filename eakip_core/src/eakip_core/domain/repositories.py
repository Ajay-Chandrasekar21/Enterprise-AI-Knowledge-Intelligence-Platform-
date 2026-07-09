from sqlalchemy.orm import Session
from sqlalchemy import select, func
from typing import Optional, List
import uuid
from datetime import datetime
from .models import Book, User, Borrowing, Reservation, Category, Publisher, Author, Fine, ReadingSession, BookReview, Tenant, Subscription

class BookRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, book_id: uuid.UUID) -> Optional[Book]:
        return self.session.get(Book, book_id)

    def find_by_isbn(self, isbn: str) -> Optional[Book]:
        stmt = select(Book).where(Book.isbn == isbn, Book.deleted == False)
        return self.session.execute(stmt).scalar_one_or_none()

    def exists_by_isbn(self, isbn: str) -> bool:
        stmt = select(func.count(Book.id)).where(Book.isbn == isbn, Book.deleted == False)
        return self.session.execute(stmt).scalar() > 0

    def save(self, book: Book) -> Book:
        self.session.add(book)
        self.session.flush()
        return book

    def find_all(self) -> List[Book]:
        stmt = select(Book).where(Book.deleted == False)
        return list(self.session.execute(stmt).scalars().all())

class UserRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, user_id: uuid.UUID) -> Optional[User]:
        return self.session.get(User, user_id)

    def find_by_username(self, username: str) -> Optional[User]:
        stmt = select(User).where(User.username == username)
        return self.session.execute(stmt).scalar_one_or_none()

    def find_by_email(self, email: str) -> Optional[User]:
        stmt = select(User).where(User.email == email)
        return self.session.execute(stmt).scalar_one_or_none()

    def exists_by_username(self, username: str) -> bool:
        stmt = select(func.count(User.id)).where(User.username == username)
        return self.session.execute(stmt).scalar() > 0

    def exists_by_email(self, email: str) -> bool:
        stmt = select(func.count(User.id)).where(User.email == email)
        return self.session.execute(stmt).scalar() > 0

    def save(self, user: User) -> User:
        self.session.add(user)
        self.session.flush()
        return user

class BorrowingRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, borrowing_id: uuid.UUID) -> Optional[Borrowing]:
        return self.session.get(Borrowing, borrowing_id)

    def find_by_user_id(self, user_id: uuid.UUID) -> List[Borrowing]:
        stmt = select(Borrowing).where(Borrowing.user_id == user_id)
        return list(self.session.execute(stmt).scalars().all())

    def find_by_user_id_and_status(self, user_id: uuid.UUID, status: str) -> List[Borrowing]:
        stmt = select(Borrowing).where(Borrowing.user_id == user_id, Borrowing.status == status)
        return list(self.session.execute(stmt).scalars().all())

    def count_by_user_id_and_status(self, user_id: uuid.UUID, status: str) -> int:
        stmt = select(func.count(Borrowing.id)).where(Borrowing.user_id == user_id, Borrowing.status == status)
        return self.session.execute(stmt).scalar() or 0

    def find_by_book_id_and_status(self, book_id: uuid.UUID, status: str) -> List[Borrowing]:
        stmt = select(Borrowing).where(Borrowing.book_id == book_id, Borrowing.status == status)
        return list(self.session.execute(stmt).scalars().all())

    def find_overdue_borrowings(self, now: datetime) -> List[Borrowing]:
        stmt = select(Borrowing).where(Borrowing.status == "BORROWED", Borrowing.due_date < now)
        return list(self.session.execute(stmt).scalars().all())

    def save(self, borrowing: Borrowing) -> Borrowing:
        self.session.add(borrowing)
        self.session.flush()
        return borrowing

class ReservationRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, reservation_id: uuid.UUID) -> Optional[Reservation]:
        return self.session.get(Reservation, reservation_id)

    def find_by_user_id(self, user_id: uuid.UUID) -> List[Reservation]:
        stmt = select(Reservation).where(Reservation.user_id == user_id)
        return list(self.session.execute(stmt).scalars().all())

    def find_by_user_id_and_status(self, user_id: uuid.UUID, status: str) -> List[Reservation]:
        stmt = select(Reservation).where(Reservation.user_id == user_id, Reservation.status == status)
        return list(self.session.execute(stmt).scalars().all())

    def find_by_book_id_and_status_order_by_date_asc(self, book_id: uuid.UUID, status: str) -> List[Reservation]:
        stmt = select(Reservation).where(
            Reservation.book_id == book_id, 
            Reservation.status == status
        ).order_by(Reservation.reservation_date.asc())
        return list(self.session.execute(stmt).scalars().all())

    def find_by_book_and_user_and_status(self, book_id: uuid.UUID, user_id: uuid.UUID, status: str) -> Optional[Reservation]:
        stmt = select(Reservation).where(
            Reservation.book_id == book_id,
            Reservation.user_id == user_id,
            Reservation.status == status
        )
        return self.session.execute(stmt).scalar_one_or_none()

    def find_max_queue_position_by_book_id(self, book_id: uuid.UUID) -> int:
        stmt = select(func.max(Reservation.queue_position)).where(
            Reservation.book_id == book_id,
            Reservation.status == "PENDING"
        )
        val = self.session.execute(stmt).scalar()
        return val if val is not None else 0

    def save(self, reservation: Reservation) -> Reservation:
        self.session.add(reservation)
        self.session.flush()
        return reservation

class CategoryRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, category_id: uuid.UUID) -> Optional[Category]:
        return self.session.get(Category, category_id)

    def save(self, category: Category) -> Category:
        self.session.add(category)
        self.session.flush()
        return category

class PublisherRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, publisher_id: uuid.UUID) -> Optional[Publisher]:
        return self.session.get(Publisher, publisher_id)

    def save(self, publisher: Publisher) -> Publisher:
        self.session.add(publisher)
        self.session.flush()
        return publisher

class AuthorRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, author_id: uuid.UUID) -> Optional[Author]:
        return self.session.get(Author, author_id)

    def save(self, author: Author) -> Author:
        self.session.add(author)
        self.session.flush()
        return author

class FineRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, fine_id: uuid.UUID) -> Optional[Fine]:
        return self.session.get(Fine, fine_id)

    def find_by_borrowing_id(self, borrowing_id: uuid.UUID) -> Optional[Fine]:
        stmt = select(Fine).where(Fine.borrowing_id == borrowing_id)
        return self.session.execute(stmt).scalar_one_or_none()

    def save(self, fine: Fine) -> Fine:
        self.session.add(fine)
        self.session.flush()
        return fine

class ReadingSessionRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, session_id: uuid.UUID) -> Optional[ReadingSession]:
        return self.session.get(ReadingSession, session_id)

    def find_by_user_id(self, user_id: uuid.UUID) -> List[ReadingSession]:
        stmt = select(ReadingSession).where(ReadingSession.user_id == user_id)
        return list(self.session.execute(stmt).scalars().all())

    def find_by_user_id_and_book_id_and_end_time_is_null(self, user_id: uuid.UUID, book_id: uuid.UUID) -> Optional[ReadingSession]:
        stmt = select(ReadingSession).where(
            ReadingSession.user_id == user_id,
            ReadingSession.book_id == book_id,
            ReadingSession.end_time == None
        )
        return self.session.execute(stmt).scalar_one_or_none()

    def save(self, rsession: ReadingSession) -> ReadingSession:
        self.session.add(rsession)
        self.session.flush()
        return rsession

class BookReviewRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, review_id: uuid.UUID) -> Optional[BookReview]:
        return self.session.get(BookReview, review_id)

    def find_by_book_id(self, book_id: uuid.UUID) -> List[BookReview]:
        stmt = select(BookReview).where(BookReview.book_id == book_id)
        return list(self.session.execute(stmt).scalars().all())

    def save(self, review: BookReview) -> BookReview:
        self.session.add(review)
        self.session.flush()
        return review

class TenantRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, tenant_id: uuid.UUID) -> Optional[Tenant]:
        return self.session.get(Tenant, tenant_id)

    def find_by_name(self, name: str) -> Optional[Tenant]:
        stmt = select(Tenant).where(Tenant.name == name)
        return self.session.execute(stmt).scalar_one_or_none()

    def save(self, tenant: Tenant) -> Tenant:
        self.session.add(tenant)
        self.session.flush()
        return tenant

class SubscriptionRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, subscription_id: uuid.UUID) -> Optional[Subscription]:
        return self.session.get(Subscription, subscription_id)

    def find_by_tenant_id(self, tenant_id: uuid.UUID) -> Optional[Subscription]:
        stmt = select(Subscription).where(Subscription.tenant_id == tenant_id)
        return self.session.execute(stmt).scalar_one_or_none()

    def save(self, subscription: Subscription) -> Subscription:
        self.session.add(subscription)
        self.session.flush()
        return subscription

