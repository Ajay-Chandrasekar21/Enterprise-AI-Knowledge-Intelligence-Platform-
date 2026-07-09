FROM python:3.13-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Install python dependencies first for layer caching
COPY eakip_core/pyproject.toml eakip_core/
COPY eakip_security/pyproject.toml eakip_security/
COPY eakip_api/pyproject.toml eakip_api/
COPY eakip_document_processing/pyproject.toml eakip_document_processing/
COPY eakip_rag/pyproject.toml eakip_rag/
COPY eakip_agent_orchestrator/pyproject.toml eakip_agent_orchestrator/
COPY eakip_analytics/pyproject.toml eakip_analytics/

# Direct pip installations
RUN pip install --no-cache-dir \
    fastapi \
    uvicorn \
    websockets \
    pydantic-settings \
    pika \
    python-multipart \
    psycopg2-binary \
    sqlalchemy \
    pyjwt \
    bcrypt \
    redis \
    apscheduler \
    alembic \
    email-validator


# Copy source directories
COPY eakip_core/src eakip_core/src
COPY eakip_security/src eakip_security/src
COPY eakip_api/src eakip_api/src
COPY eakip_document_processing/src eakip_document_processing/src
COPY eakip_rag/src eakip_rag/src
COPY eakip_agent_orchestrator/src eakip_agent_orchestrator/src
COPY eakip_analytics/src eakip_analytics/src

# Set PYTHONPATH environment variables
ENV PYTHONPATH="/app/eakip_core/src:/app/eakip_security/src:/app/eakip_api/src:/app/eakip_document_processing/src:/app/eakip_rag/src:/app/eakip_agent_orchestrator/src:/app/eakip_analytics/src"

# Define default port
ENV PORT=8080
EXPOSE 8080

CMD ["uvicorn", "eakip_api.main:app", "--host", "0.0.0.0", "--port", "8080"]
