from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session
from eakip_security.jwt import JwtTokenProvider
from eakip_core.domain.models import User
from eakip_core.domain.repositories import UserRepository
from .db import get_db

security = HTTPBearer()

def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db)
) -> User:
    token = credentials.credentials
    provider = JwtTokenProvider()
    try:
        username = provider.get_username_from_token(token)
        if provider.validate_token(token, username):
            user_repo = UserRepository(db)
            user = user_repo.find_by_username(username)
            if user:
                return user
    except Exception:
        pass
    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Invalid or expired authentication credentials"
    )

def require_role(roles: list[str]):
    def dependency(user: User = Depends(get_current_user)):
        # Strip "ROLE_" prefix if present, compare role case-insensitively
        user_role = user.role.replace("ROLE_", "").upper()
        if user_role not in [r.upper() for r in roles]:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You do not have permission to access this resource"
            )
        return user
    return dependency
