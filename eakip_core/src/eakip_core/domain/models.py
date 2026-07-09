import uuid
from datetime import datetime
from decimal import Decimal
from typing import List, Optional, Set
from sqlalchemy import (
    Column, Table, ForeignKey, String, Integer, Boolean, 
    DateTime, Numeric, Text, ForeignKeyConstraint
)
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship

class Base(DeclarativeBase):
    pass

class BaseEntity(Base):
    __abstract__ = True
    
    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    created_by: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    created_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    last_modified_by: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    last_modified_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    version: Mapped[Optional[int]] = mapped_column(Integer, default=1)

class User(BaseEntity):
    __tablename__ = "users"
    
    username: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)
    email: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)
    password_hash: Mapped[str] = mapped_column(String(100), nullable=False)
    role: Mapped[str] = mapped_column(String(20), nullable=False) # e.g. MEMBER, LIBRARIAN
    
    profile: Mapped["Profile"] = relationship("Profile", back_populates="user", uselist=False, cascade="all, delete-orphan")
    borrowings: Mapped[List["Borrowing"]] = relationship("Borrowing", back_populates="user")
    reservations: Mapped[List["Reservation"]] = relationship("Reservation", back_populates="user")
    reading_sessions: Mapped[List["ReadingSession"]] = relationship("ReadingSession", back_populates="user")

class Profile(Base):
    __tablename__ = "profiles"
    
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.id"), primary_key=True)
    first_name: Mapped[str] = mapped_column(String(50), nullable=False)
    last_name: Mapped[str] = mapped_column(String(50), nullable=False)
    library_card_number: Mapped[str] = mapped_column(String(30), unique=True, nullable=False)
    department: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    preferences: Mapped[Optional[str]] = mapped_column(Text, nullable=True) # JSON string
    
    user: Mapped["User"] = relationship("User", back_populates="profile")

class Category(BaseEntity):
    __tablename__ = "categories"
    
    name: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)
    description: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    deleted: Mapped[bool] = mapped_column(Boolean, default=False)

class Publisher(BaseEntity):
    __tablename__ = "publishers"
    
    name: Mapped[str] = mapped_column(String(150), unique=True, nullable=False)
    address: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    deleted: Mapped[bool] = mapped_column(Boolean, default=False)

class Author(BaseEntity):
    __tablename__ = "authors"
    
    name: Mapped[str] = mapped_column(String(150), nullable=False)
    bio: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    deleted: Mapped[bool] = mapped_column(Boolean, default=False)
    
    books: Mapped[List["Book"]] = relationship("Book", secondary="book_authors", back_populates="authors")

book_authors = Table(
    "book_authors",
    Base.metadata,
    Column("book_id", ForeignKey("books.id"), primary_key=True),
    Column("author_id", ForeignKey("authors.id"), primary_key=True)
)

class Book(BaseEntity):
    __tablename__ = "books"
    
    title: Mapped[str] = mapped_column(String(255), nullable=False)
    isbn: Mapped[str] = mapped_column(String(20), unique=True, nullable=False)
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    publisher_id: Mapped[Optional[uuid.UUID]] = mapped_column(ForeignKey("publishers.id"), nullable=True)
    category_id: Mapped[Optional[uuid.UUID]] = mapped_column(ForeignKey("categories.id"), nullable=True)
    edition: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    language: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    shelf: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    rack: Mapped[Optional[str]] = mapped_column(String(50), nullable=True)
    cover_image_url: Mapped[Optional[str]] = mapped_column(String(500), nullable=True)
    total_copies: Mapped[int] = mapped_column(Integer, default=1)
    available_copies: Mapped[int] = mapped_column(Integer, default=1)
    deleted: Mapped[bool] = mapped_column(Boolean, default=False)
    
    authors: Mapped[List["Author"]] = relationship("Author", secondary=book_authors, back_populates="books")
    borrowings: Mapped[List["Borrowing"]] = relationship("Borrowing", back_populates="book")
    reservations: Mapped[List["Reservation"]] = relationship("Reservation", back_populates="book")
    reading_sessions: Mapped[List["ReadingSession"]] = relationship("ReadingSession", back_populates="book")
    reviews: Mapped[List["BookReview"]] = relationship("BookReview", back_populates="book")

class BookReview(BaseEntity):
    __tablename__ = "book_reviews"
    
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.id"), nullable=False)
    book_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("books.id"), nullable=False)
    rating: Mapped[int] = mapped_column(Integer, nullable=False) # 1 to 5
    comment: Mapped[Optional[str]] = mapped_column(String(1000), nullable=True)
    review_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    
    book: Mapped["Book"] = relationship("Book", back_populates="reviews")

class Borrowing(BaseEntity):
    __tablename__ = "borrowings"
    
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.id"), nullable=False)
    book_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("books.id"), nullable=False)
    borrow_date: Mapped[datetime] = mapped_column(DateTime, nullable=False)
    due_date: Mapped[datetime] = mapped_column(DateTime, nullable=False)
    return_date: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True)
    status: Mapped[str] = mapped_column(String(20), default="BORROWED") # BORROWED, RETURNED, OVERDUE
    renewal_count: Mapped[int] = mapped_column(Integer, default=0)
    
    user: Mapped["User"] = relationship("User", back_populates="borrowings")
    book: Mapped["Book"] = relationship("Book", back_populates="borrowings")
    fine: Mapped[Optional["Fine"]] = relationship("Fine", back_populates="borrowing", uselist=False)

class Reservation(BaseEntity):
    __tablename__ = "reservations"
    
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.id"), nullable=False)
    book_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("books.id"), nullable=False)
    reservation_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    status: Mapped[str] = mapped_column(String(20), default="PENDING") # PENDING, FULFILLED, CANCELLED
    queue_position: Mapped[int] = mapped_column(Integer, nullable=False)
    
    user: Mapped["User"] = relationship("User", back_populates="reservations")
    book: Mapped["Book"] = relationship("Book", back_populates="reservations")

class Fine(BaseEntity):
    __tablename__ = "fines"
    
    borrowing_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("borrowings.id"), unique=True, nullable=False)
    amount: Mapped[Decimal] = mapped_column(Numeric(10, 2), nullable=False)
    status: Mapped[str] = mapped_column(String(20), default="UNPAID") # UNPAID, PAID
    generated_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    payment_date: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True)
    
    borrowing: Mapped["Borrowing"] = relationship("Borrowing", back_populates="fine")

class ReadingSession(BaseEntity):
    __tablename__ = "reading_sessions"
    
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.id"), nullable=False)
    book_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("books.id"), nullable=False)
    start_time: Mapped[datetime] = mapped_column(DateTime, nullable=False)
    end_time: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True)
    pages_read: Mapped[int] = mapped_column(Integer, default=0)
    progress_percentage: Mapped[int] = mapped_column(Integer, default=0)
    
    user: Mapped["User"] = relationship("User", back_populates="reading_sessions")
    book: Mapped["Book"] = relationship("Book", back_populates="reading_sessions")

class Tenant(BaseEntity):
    __tablename__ = "saas_tenants"

    name: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)
    branding_primary_color: Mapped[str] = mapped_column(String(50), default="#4F46E5")
    branding_logo_url: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    is_active: Mapped[bool] = mapped_column(Boolean, default=True)

class Subscription(BaseEntity):
    __tablename__ = "saas_subscriptions"

    tenant_id: Mapped[uuid.UUID] = mapped_column(unique=True, nullable=False)
    plan_tier: Mapped[str] = mapped_column(String(50), default="FREE")
    max_token_quota: Mapped[int] = mapped_column(Integer, default=1000000)
    consumed_tokens: Mapped[int] = mapped_column(Integer, default=0)
    rate_limit_per_min: Mapped[int] = mapped_column(Integer, default=60)
    cost_per_million_tokens: Mapped[float] = mapped_column(Numeric(10, 2), default=15.00)

