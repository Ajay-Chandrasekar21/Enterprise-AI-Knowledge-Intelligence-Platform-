from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from eakip_core.common.dto import ApiResponse
from eakip_core.domain.models import User
from eakip_security.dto import RegisterRequest, LoginRequest, TokenRefreshRequest, JwtResponse
from eakip_security.service import AuthService
from ..config.db import get_db

router = APIRouter(prefix="/api/v1/auth", tags=["Authentication Gateway"])

@router.post("/register", status_code=status.HTTP_201_CREATED, response_model=ApiResponse[dict])
def register(request: RegisterRequest, db: Session = Depends(get_db)):
    auth_service = AuthService(db)
    user = auth_service.register_user(request)
    # Serialize registered user details back to client
    user_data = {
        "id": str(user.id),
        "username": user.username,
        "email": user.email,
        "role": user.role
    }
    return ApiResponse.success_response(user_data, "User registered successfully")

@router.post("/login", response_model=ApiResponse[JwtResponse])
def login(request: LoginRequest, db: Session = Depends(get_db)):
    auth_service = AuthService(db)
    response = auth_service.authenticate_user(request)
    return ApiResponse.success_response(response, "Login successful")

@router.post("/refresh", response_model=ApiResponse[JwtResponse])
def refresh(request: TokenRefreshRequest, db: Session = Depends(get_db)):
    auth_service = AuthService(db)
    response = auth_service.refresh_access_token(request)
    return ApiResponse.success_response(response, "Token refreshed successfully")
