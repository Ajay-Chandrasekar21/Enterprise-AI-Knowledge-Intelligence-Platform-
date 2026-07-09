from typing import Optional
from pydantic import BaseModel, Field, ConfigDict, EmailStr
from pydantic.alias_generators import to_camel

class CamelCaseModel(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_camel,
        populate_by_name=True,
        from_attributes=True
    )

class RegisterRequest(CamelCaseModel):
    username: str = Field(min_length=3, max_length=50)
    email: EmailStr
    password: str = Field(min_length=6, max_length=100)
    first_name: str = Field(max_length=50)
    last_name: str = Field(max_length=50)
    department: Optional[str] = Field(default=None, max_length=100)
    role: Optional[str] = None

class LoginRequest(CamelCaseModel):
    username: str
    password: str

class TokenRefreshRequest(CamelCaseModel):
    refresh_token: str

class JwtResponse(CamelCaseModel):
    access_token: str
    refresh_token: str
    username: str
    role: str
