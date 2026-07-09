import bcrypt

class PasswordEncoder:
    @staticmethod
    def encode(password: str) -> str:
        # Generate salt and hash the password
        salt = bcrypt.gensalt()
        hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
        return hashed.decode('utf-8')

    @staticmethod
    def matches(raw_password: str, hashed_password: str) -> bool:
        # Check password against hash
        try:
            return bcrypt.checkpw(
                raw_password.encode('utf-8'), 
                hashed_password.encode('utf-8')
            )
        except Exception:
            return False
