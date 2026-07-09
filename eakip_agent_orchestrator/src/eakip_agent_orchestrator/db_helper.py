import logging
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

logger = logging.getLogger(__name__)

def get_dynamic_session():
    try:
        from eakip_api.config.db import SessionLocal
        return SessionLocal()
    except Exception:
        # Fallback to in-memory sqlite session for standalone validations
        logger.warning("FastAPI DB config not found. Falling back to temporary SQLite engine.")
        engine = create_engine("sqlite:///:memory:")
        SessionLocal = sessionmaker(bind=engine)
        return SessionLocal()
