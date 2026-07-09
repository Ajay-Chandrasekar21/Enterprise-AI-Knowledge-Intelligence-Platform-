import uuid
from datetime import datetime
from typing import Optional, List
from sqlalchemy import Column, String, Text, Integer, Numeric, DateTime, ForeignKey, Boolean
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship
from eakip_core.domain.models import Base, BaseEntity

class DocumentNode(BaseEntity):
    __tablename__ = "document_nodes"

    file_name: Mapped[str] = mapped_column(String(255), nullable=False)
    content_type: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    parsed_text: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    processing_status: Mapped[str] = mapped_column(String(50), default="PROCESSING") # PROCESSING, COMPLETED, FAILED

    chunks: Mapped[List["ChunkNode"]] = relationship("ChunkNode", back_populates="document", cascade="all, delete-orphan")
    relations: Mapped[List["GraphRelation"]] = relationship("GraphRelation", back_populates="document", cascade="all, delete-orphan")

class ChunkNode(BaseEntity):
    __tablename__ = "chunk_nodes"

    document_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("document_nodes.id"), nullable=False)
    chunk_index: Mapped[int] = mapped_column(Integer, nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    embedding_vector: Mapped[Optional[str]] = mapped_column(Text, nullable=True) # comma separated vector numbers

    document: Mapped["DocumentNode"] = relationship("DocumentNode", back_populates="chunks")

class GraphRelation(BaseEntity):
    __tablename__ = "graph_relations"

    document_id: Mapped[Optional[uuid.UUID]] = mapped_column(ForeignKey("document_nodes.id"), nullable=True)
    source_entity: Mapped[str] = mapped_column(String(150), nullable=False)
    relation_type: Mapped[str] = mapped_column(String(100), nullable=False)
    target_entity: Mapped[str] = mapped_column(String(150), nullable=False)
    confidence: Mapped[float] = mapped_column(Numeric(5, 2), default=1.0)

    document: Mapped[Optional["DocumentNode"]] = relationship("DocumentNode", back_populates="relations")
