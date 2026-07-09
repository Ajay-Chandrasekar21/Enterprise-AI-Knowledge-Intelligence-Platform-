import uuid
from datetime import datetime
from typing import Optional
from sqlalchemy import Column, String, Text, Integer, DateTime, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from eakip_core.domain.models import Base, BaseEntity

class MemoryNode(BaseEntity):
    __tablename__ = "memory_nodes"

    user_id: Mapped[uuid.UUID] = mapped_column(nullable=False)
    memory_type: Mapped[str] = mapped_column(String(50), nullable=False) # EPISODIC, PREFERENCE etc.
    content: Mapped[str] = mapped_column(Text, nullable=False)
    relevance_score: Mapped[float] = mapped_column(default=1.0)
    created_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    last_accessed_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)
    retention_days: Mapped[int] = mapped_column(Integer, default=30)

class WorkflowDefinition(BaseEntity):
    __tablename__ = "workflow_definitions"

    name: Mapped[str] = mapped_column(String(150), nullable=False)
    description: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    workflow_version: Mapped[int] = mapped_column(Integer, default=1)
    trigger_type: Mapped[str] = mapped_column(String(50), nullable=False) # MANUAL etc.
    nodes_json: Mapped[str] = mapped_column(Text, nullable=False)
    created_date: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)

    instances: Mapped[list["WorkflowInstance"]] = relationship("WorkflowInstance", back_populates="definition", cascade="all, delete-orphan")

class WorkflowInstance(BaseEntity):
    __tablename__ = "workflow_instances"

    definition_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("workflow_definitions.id"), nullable=False)
    current_node_id: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    status: Mapped[str] = mapped_column(String(50), default="RUNNING") # RUNNING, COMPLETED etc.
    variables_json: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    error_message: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    last_updated: Mapped[datetime] = mapped_column(DateTime, default=datetime.now)

    definition: Mapped["WorkflowDefinition"] = relationship("WorkflowDefinition", back_populates="instances")
