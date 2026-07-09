import os
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session
from typing import Generator

# Read DATABASE_URL from environment variables or default to local fallback
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://postgres:postgres@localhost:5432/eakip")

# Clean JDBC prefix if present (legacy Java settings migration safeguard)
if DATABASE_URL.startswith("jdbc:"):
    DATABASE_URL = DATABASE_URL.replace("jdbc:", "", 1)

engine = create_engine(DATABASE_URL, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def get_db() -> Generator[Session, None, None]:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
