import sys
import os
import uuid
import traceback
import asyncio
from datetime import datetime

# Setup PYTHONPATH dynamically relative to script file path
root_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
sys.path.append(os.path.join(root_dir, "eakip_core/src"))
sys.path.append(os.path.join(root_dir, "eakip_security/src"))
sys.path.append(os.path.join(root_dir, "eakip_api/src"))
sys.path.append(os.path.join(root_dir, "eakip_document_processing/src"))
sys.path.append(os.path.join(root_dir, "eakip_rag/src"))
sys.path.append(os.path.join(root_dir, "eakip_agent_orchestrator/src"))
sys.path.append(os.path.join(root_dir, "eakip_analytics/src"))

errors = []

async def run_checks():
    print("=== 1. Checking Module Imports ===")
    modules_to_test = [
        "eakip_core.common.saas",
        "eakip_core.common.dto",
        "eakip_core.common.validation",
        "eakip_core.domain.models",
        "eakip_core.domain.repositories",
        "eakip_core.usecase.services",
        "eakip_security.jwt",
        "eakip_security.service",
        "eakip_api.main",
        "eakip_document_processing.chunking",
        "eakip_document_processing.parser",
        "eakip_rag.models",
        "eakip_rag.repositories",
        "eakip_rag.vector",
        "eakip_agent_orchestrator.models",
        "eakip_agent_orchestrator.memory",
        "eakip_agent_orchestrator.planning",
        "eakip_agent_orchestrator.reasoning",
        "eakip_agent_orchestrator.reflection",
        "eakip_agent_orchestrator.consensus",
        "eakip_agent_orchestrator.workflow",
        "eakip_agent_orchestrator.tools",
        "eakip_agent_orchestrator.executor",
        "eakip_agent_orchestrator.mcp",
        "eakip_agent_orchestrator.observability",
        "eakip_agent_orchestrator.orchestrator",
        "eakip_analytics"
    ]

    for mod_name in modules_to_test:
        try:
            __import__(mod_name)
            print(f"Import {mod_name}: OK")
        except Exception as e:
            errors.append(f"Import {mod_name} failed: {str(e)}\n{traceback.format_exc()}")
            print(f"Import {mod_name}: FAILED")

    print("\n=== 2. Checking SQLAlchemy Models ===")
    try:
        from sqlalchemy.orm import configure_mappers
        import eakip_core.domain.models
        import eakip_rag.models
        import eakip_agent_orchestrator.models
        configure_mappers()
        print("SQLAlchemy relationships verification: OK")
    except Exception as e:
        errors.append(f"SQLAlchemy configure_mappers failed: {str(e)}\n{traceback.format_exc()}")
        print("SQLAlchemy relationships verification: FAILED")

    print("\n=== 3. Checking JWT Engine ===")
    try:
        from eakip_security.jwt import JwtTokenProvider
        provider = JwtTokenProvider()
        token = provider.generate_access_token("testuser")
        user = provider.get_username_from_token(token)
        assert user == "testuser", f"Expected testuser, got {user}"
        assert provider.validate_token(token, "testuser"), "Token validation failed"
        print("JWT token sign/verify checks: OK")
    except Exception as e:
        errors.append(f"JWT validation failed: {str(e)}")
        print("JWT token sign/verify checks: FAILED")

    print("\n=== 4. Checking Alembic Config ===")
    if os.path.exists(os.path.join(root_dir, "alembic.ini")) and os.path.exists(os.path.join(root_dir, "alembic/env.py")):
        print("Alembic configuration directories: OK")
    else:
        errors.append("Alembic ini or env script files are missing")
        print("Alembic configuration directories: FAILED")

    print("\n=== 5. Checking WebSockets route ===")
    try:
        from eakip_api.main import app
        ws_route = None
        for route in app.routes:
            if getattr(route, "path", None) == "/ws/notifications":
                ws_route = route
                break
        assert ws_route is not None, "WebSocket notification route missing"
        print("WebSocket route registration: OK")
    except Exception as e:
        errors.append(f"WS checks failed: {str(e)}")
        print("WebSocket route registration: FAILED")

    print("\n=== 6. Checking APScheduler Jobs & Lifespan context ===")
    try:
        from eakip_api.main import app, scheduler
        # Spin up lifespan context to run startup events
        async with app.router.lifespan_context(app):
            jobs = scheduler.get_jobs()
            job_ids = [j.id for j in jobs]
            assert "memory_compress" in job_ids, f"Expected memory_compress job, got: {job_ids}"
        print("APScheduler jobs configuration and Lifespan events startup/shutdown: OK")
    except Exception as e:
        errors.append(f"APScheduler checks failed: {str(e)}")
        print("APScheduler jobs configuration and Lifespan events startup/shutdown: FAILED")

    print("\n=== 7. Checking Docker files ===")
    if os.path.exists(os.path.join(root_dir, "Dockerfile")) and os.path.exists(os.path.join(root_dir, "docker-compose.yml")):
        dockerfile_content = open(os.path.join(root_dir, "Dockerfile")).read()
        assert "uvicorn" in dockerfile_content, "Uvicorn startup not found in Dockerfile"
        print("Dockerfile and docker-compose configurations: OK")
    else:
        errors.append("Dockerfile or docker-compose.yml missing from root workspace")
        print("Dockerfile and docker-compose configurations: FAILED")

    print("\n=== 8. Checking RAG Pipeline Ingestion ===")
    try:
        # Setup temporary SQLite session for pipeline check
        from sqlalchemy import create_engine
        from sqlalchemy.orm import sessionmaker
        
        engine = create_engine("sqlite:///:memory:")
        SessionLocal = sessionmaker(bind=engine)
        db = SessionLocal()
        
        # Create all tables in temp DB
        import eakip_rag.models
        eakip_rag.models.Base.metadata.create_all(bind=engine)
        
        from eakip_rag.vector import RagPipelineService, VectorStoreService
        import io
        
        pipeline = RagPipelineService(db)
        mock_file = io.BytesIO(b"Clean Architecture is written by Robert C. Martin. It is published by Prentice Hall.")
        
        doc = pipeline.ingest_document("test.txt", "text/plain", mock_file)
        assert doc.processing_status == "COMPLETED", f"Expected COMPLETED processing status, got: {doc.processing_status}"
        
        # Query vector similarity checks
        store = VectorStoreService(db)
        results = store.search("architecture martin", 2)
        assert len(results) > 0, "No RAG search results returned"
        print(f"RAG document parser and vector indexing: OK (Top score: {results[0].score:.3f})")
        db.close()
    except Exception as e:
        errors.append(f"RAG pipeline test failed: {str(e)}\n{traceback.format_exc()}")
        print("RAG document parser and vector indexing: FAILED")

    print("\n=== 9. Checking AI Agents & Orchestrator ===")
    try:
        from eakip_agent_orchestrator.orchestrator import AgentOrchestrator
        user_uuid = uuid.uuid4()
        result = AgentOrchestrator.process_query(user_uuid, "Explain interview preparations steps")
        assert "BOOK_DISCOVERY_AGENT" in result.response
        assert result.confidence_score == 0.94
        print("AI Multi-Agent orchestrator responses: OK")
    except Exception as e:
        errors.append(f"AI Agents check failed: {str(e)}")
        print("AI Multi-Agent orchestrator responses: FAILED")

    print("\n=== Integration Summary ===")
    if errors:
        print(f"Integration validation failed with {len(errors)} error(s):")
        for idx, err in enumerate(errors):
            print(f"\n--- Error {idx+1} ---")
            print(err)
        sys.exit(1)
    else:
        print("ALL INTEGRATION VERIFICATION TASKS COMPLETED WITH ZERO ERRORS (100% SUCCESS)!")
        sys.exit(0)

if __name__ == "__main__":
    asyncio.run(run_checks())
