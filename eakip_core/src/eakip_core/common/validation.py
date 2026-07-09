import re

ISBN_PATTERN = re.compile(
    r"^(?:ISBN(?:-10)?:?\s*)?([0-9Xx]{10})$|^(?:ISBN(?:-13)?:?\s*)?([0-9]{13})$"
)

def validate_isbn(value: str) -> bool:
    if not value or not value.strip():
        return True
    clean_value = re.sub(r"[- ]", "", value)
    return bool(ISBN_PATTERN.match(clean_value))
