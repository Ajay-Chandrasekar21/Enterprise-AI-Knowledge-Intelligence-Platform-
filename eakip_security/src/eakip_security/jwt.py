import os
import jwt
from datetime import datetime, timedelta
from typing import Dict, Any, Optional

class JwtTokenProvider:
    def __init__(
        self,
        secret: Optional[str] = None,
        access_token_expire_ms: int = 86400000,   # 24 Hours default
        refresh_token_expire_ms: int = 604800000  # 7 Days default
    ):
        self.secret = secret or os.getenv(
            "JWT_SECRET", 
            "9a4f2c8d3b7a1e6f5c8d9a0b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v"
        )
        self.access_expire = access_token_expire_ms
        self.refresh_expire = refresh_token_expire_ms
        self.algorithm = "HS256"

    def generate_access_token(self, username: str) -> str:
        return self._generate_token(username, self.access_expire)

    def generate_refresh_token(self, username: str) -> str:
        return self._generate_token(username, self.refresh_expire)

    def _generate_token(self, username: str, expire_ms: int) -> str:
        now = datetime.utcnow()
        expiry = now + timedelta(milliseconds=expire_ms)
        payload = {
            "sub": username,
            "iat": now,
            "exp": expiry
        }
        return jwt.encode(payload, self.secret, algorithm=self.algorithm)

    def get_username_from_token(self, token: str) -> str:
        payload = self._get_claims_from_token(token)
        return payload.get("sub", "")

    def _get_claims_from_token(self, token: str) -> Dict[str, Any]:
        try:
            return jwt.decode(token, self.secret, algorithms=[self.algorithm])
        except jwt.PyJWTError as e:
            raise ValueError(f"Invalid JWT Token: {str(e)}")

    def validate_token(self, token: str, username: str) -> bool:
        try:
            token_username = self.get_username_from_token(token)
            # jwt.decode automatically verifies expiration time 'exp'
            return token_username == username
        except Exception:
            return False
