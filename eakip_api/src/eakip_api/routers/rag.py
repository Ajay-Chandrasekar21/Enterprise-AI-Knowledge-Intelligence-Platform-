import uuid
from typing import List, Dict, Any
from fastapi import APIRouter, UploadFile, File, Query, Depends, status
from sqlalchemy.orm import Session
from eakip_core.common.dto import ApiResponse
from ..config.db import get_db

router = APIRouter(prefix="/api/v1/rag", tags=["Knowledge RAG API"])

@router.post("/upload", response_model=ApiResponse[dict])
def upload_document(file: UploadFile = File(...)):
    try:
        from eakip_rag.vector import RagPipelineService
        # Simulating loading/resolving from application container config
        pipeline = RagPipelineService()
        result = pipeline.ingest_document(
            file.filename,
            file.content_type,
            file.file
        )
        return ApiResponse.success_response(result, "Document uploaded and queued for processing")
    except Exception as e:
        return ApiResponse(
            success=False,
            message=f"Upload parsing failed: {str(e)}",
            data=None
        )

@router.get("/search", response_model=ApiResponse[List[Dict[str, Any]]])
def search(query: str, top_k: int = Query(default=5, alias="topK")):
    from eakip_rag.vector import VectorStoreService
    vector_store = VectorStoreService()
    search_results = vector_store.search(query, top_k)
    
    response = []
    for res in search_results:
        response.append({
            "chunkId": str(res.chunk_node.id),
            "content": res.chunk_node.content,
            "score": res.score,
            "sourceFile": res.chunk_node.document.file_name
        })
    return ApiResponse.success_response(response, "Semantic search completed")

@router.get("/documents", response_model=ApiResponse[List[dict]])
def get_documents():
    from eakip_rag.repositories import DocumentNodeRepository
    repo = DocumentNodeRepository()
    docs = repo.find_all()
    result = [{
        "id": str(d.id),
        "fileName": d.file_name,
        "contentType": d.content_type,
        "processingStatus": d.processing_status
    } for d in docs]
    return ApiResponse.success_response(result, "Documents loaded")

@router.get("/graph", response_model=ApiResponse[dict])
def get_graph():
    from eakip_rag.repositories import GraphRelationRepository
    repo = GraphRelationRepository()
    relations = repo.find_all()
    
    nodes = set()
    edges = []
    
    for rel in relations:
        nodes.add(rel.source_entity)
        nodes.add(rel.target_entity)
        edges.append({
            "source": rel.source_entity,
            "target": rel.target_entity,
            "type": rel.relation_type,
            "weight": float(rel.confidence)
        })
        
    nodes_list = [{"id": n, "label": n} for n in nodes]
    graph = {
        "nodes": nodes_list,
        "edges": edges
    }
    return ApiResponse.success_response(graph, "Knowledge Graph loaded")
