class DomainException(Exception):
    """Base domain exception for EAKIP application."""
    def __init__(self, message: str):
        super().__init__(message)
        self.message = message

class EntityNotFoundException(DomainException):
    """Exception thrown when a requested resource/entity is not found."""
    pass

class ResourceConflictException(DomainException):
    """Exception thrown when there is a logical validation conflict."""
    pass

class UnauthorizedException(DomainException):
    """Exception thrown when access is unauthorized or denied."""
    pass
