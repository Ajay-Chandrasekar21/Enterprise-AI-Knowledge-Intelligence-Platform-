import uuid
from typing import Optional
from sqlalchemy.orm import Session

from eakip_core.domain.models import User, Profile
from eakip_core.domain.repositories import UserRepository
from eakip_core.domain.exception import ResourceConflictException, UnauthorizedException

from .dto import RegisterRequest, LoginRequest, JwtResponse, TokenRefreshRequest
from .config import PasswordEncoder
from .jwt import JwtTokenProvider

class AuthService:
    def __init__(self, session: Session):
        self.session = session
        self.user_repo = UserRepository(session)
        self.password_encoder = PasswordEncoder()
        self.jwt_token_provider = JwtTokenProvider()

    def register_user(self, request: RegisterRequest) -> User:
        if self.user_repo.exists_by_username(request.username):
            raise ResourceConflictException("Username is already taken")
        if self.user_repo.exists_by_email(request.email):
            raise ResourceConflictException("Email is already in use")

        user_role = "STUDENT"
        if request.role:
            role_upper = request.role.upper()
            if role_upper not in ["STUDENT", "FACULTY", "LIBRARIAN", "ADMIN"]:
                raise ValueError("Invalid role specified. Supported: STUDENT, FACULTY, LIBRARIAN, ADMIN")
            user_role = role_upper

        user = User(
            username=request.username,
            email=request.email,
            password_hash=self.password_encoder.encode(request.password),
            role=user_role
        )

        profile = Profile(
            user=user,
            first_name=request.first_name,
            last_name=request.last_name,
            library_card_number=f"LCRD-{uuid.uuid4().hex[:8].upper()}",
            department=request.department,
            preferences="{}"
        )
        user.profile = profile

        return self.user_repo.save(user)

    def authenticate_user(self, request: LoginRequest) -> JwtResponse:
        user = self.user_repo.find_by_username(request.username)
        if not user or not self.password_encoder.matches(request.password, user.password_hash):
            raise UnauthorizedException("Invalid username or password")

        access_token = self.jwt_token_provider.generate_access_token(user.username)
        refresh_token = self.jwt_token_provider.generate_refresh_token(user.username)

        return JwtResponse(
            access_token=access_token,
            refresh_token=refresh_token,
            username=user.username,
            role=user.role
        )

    def refresh_access_token(self, request: TokenRefreshRequest) -> JwtResponse:
        refresh_token = request.refresh_token
        try:
            username = self.jwt_token_provider.get_username_from_token(refresh_token)
            user = self.user_repo.find_by_username(username)
            if not user:
                raise UnauthorizedException("User not found matching refresh token")

            if self.jwt_token_provider.validate_token(refresh_token, user.username):
                new_access_token = self.jwt_token_provider.generate_access_token(user.username)
                return JwtResponse(
                    access_token=new_access_token,
                    refresh_token=refresh_token,
                    username=user.username,
                    role=user.role
                )
        except Exception as e:
            raise UnauthorizedException(f"Invalid or expired refresh token: {str(e)}")
        raise UnauthorizedException("Invalid refresh token validation")
