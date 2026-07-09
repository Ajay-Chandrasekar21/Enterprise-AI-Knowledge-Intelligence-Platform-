import sys
import os

# Dynamic PYTHONPATH injection for portable deployments (e.g. Vercel, docker, CI/CD)
root_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../.."))
sys.path.append(os.path.join(root_dir, "eakip_core/src"))
sys.path.append(os.path.join(root_dir, "eakip_security/src"))
sys.path.append(os.path.join(root_dir, "eakip_document_processing/src"))
sys.path.append(os.path.join(root_dir, "eakip_rag/src"))
sys.path.append(os.path.join(root_dir, "eakip_agent_orchestrator/src"))
sys.path.append(os.path.join(root_dir, "eakip_analytics/src"))

import logging
from fastapi import FastAPI, Request, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware
from starlette.middleware.base import BaseHTTPMiddleware

from eakip_core.common.saas import TenantContext

from .config.exceptions import register_exception_handlers
from .logging import RequestResponseLoggingMiddleware
from .websocket.handler import notification_handler

# Import all routers
from .routers.auth import router as auth_router
from .routers.books import router as books_router
from .routers.borrowings import router as borrowings_router
from .routers.reservations import router as reservations_router
from .routers.reports import router as reports_router
from .routers.saas import router as saas_router
from .routers.rag import router as rag_router
from .routers.memory import router as memory_router
from .routers.planning import router as planning_router
from .routers.workflow import router as workflow_router
from .routers.tools import router as tools_router
from .routers.mcp import router as mcp_router
from .routers.ai import router as ai_router

import uuid
from contextlib import asynccontextmanager
from apscheduler.schedulers.background import BackgroundScheduler

# Configure root logger
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s - %(message)s")
logger = logging.getLogger(__name__)

scheduler = BackgroundScheduler()

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup Events: Initialize APScheduler
    logger.info("Initializing APScheduler Background Jobs...")
    
    # Mock user compression job triggered hourly
    mock_user_uuid = uuid.UUID("11111111-1111-1111-1111-111111111111")
    def periodic_memory_compression():
        try:
            logger.info("APScheduler: Triggering periodic memory compression task...")
            from eakip_agent_orchestrator.memory import MemoryManager
            manager = MemoryManager()
            manager.compress_memories(mock_user_uuid)
            logger.info("APScheduler: Memory compression task completed.")
        except Exception as e:
            logger.error(f"APScheduler: Memory compression failed: {str(e)}")

    scheduler.add_job(periodic_memory_compression, "interval", hours=1, id="memory_compress")
    scheduler.start()
    logger.info("APScheduler started successfully.")
    
    yield
    
    # Shutdown Events: Stop Scheduler
    logger.info("Shutting down APScheduler...")
    scheduler.shutdown()
    logger.info("APScheduler shutdown completed.")

app = FastAPI(
    title="Enterprise AI Knowledge Intelligence Platform (EAKIP)",
    version="1.0.0",
    description="FastAPI port of the EAKIP Spring Boot Monolith",
    lifespan=lifespan
)

# CORS middleware mapping standard open origins
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 1. Tenant Filter Middleware (replicates TenantFilter.java)
class TenantMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        tenant_id = request.headers.get("X-Tenant-ID", "").strip()
        if tenant_id:
            TenantContext.set_current_tenant(tenant_id)
        else:
            TenantContext.set_current_tenant("default-tenant")
            
        try:
            return await call_next(request)
        finally:
            TenantContext.clear()

app.add_middleware(TenantMiddleware)

# 2. Trace Logging Middleware
app.add_middleware(RequestResponseLoggingMiddleware)

# 3. Register Custom Exception Translators
register_exception_handlers(app)

# 4. Include Routers
app.include_router(auth_router)
app.include_router(books_router)
app.include_router(borrowings_router)
app.include_router(reservations_router)
app.include_router(reports_router)
app.include_router(saas_router)
app.include_router(rag_router)
app.include_router(memory_router)
app.include_router(planning_router)
app.include_router(workflow_router)
app.include_router(tools_router)
app.include_router(mcp_router)
app.include_router(ai_router)

# 5. WebSocket Notification endpoint (replicates WebSocketConfig.java / NotificationWebSocketHandler)
@app.websocket("/ws/notifications")
async def websocket_endpoint(websocket: WebSocket):
    await notification_handler.connect(websocket)
    try:
        while True:
            data = await websocket.receive_text()
            await notification_handler.handle_message(websocket, data)
    except WebSocketDisconnect:
        notification_handler.disconnect(websocket)
    except Exception as e:
        logger.error(f"WebSocket transport error: {str(e)}")
        notification_handler.disconnect(websocket)

@app.get("/")
def read_root():
    return {"status": "UP", "platform": "EAKIP SaaS Backend"}
