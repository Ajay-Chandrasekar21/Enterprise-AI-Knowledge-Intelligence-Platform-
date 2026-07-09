from sqlalchemy.orm import Session
from sqlalchemy import select
from typing import Optional, List
import uuid
from .models import DocumentNode, ChunkNode, GraphRelation

class DocumentNodeRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, doc_id: uuid.UUID) -> Optional[DocumentNode]:
        return self.session.get(DocumentNode, doc_id)

    def save(self, doc: DocumentNode) -> DocumentNode:
        self.session.add(doc)
        self.session.flush()
        return doc

    def find_all(self) -> List[DocumentNode]:
        stmt = select(DocumentNode)
        return list(self.session.execute(stmt).scalars().all())

class ChunkNodeRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, chunk_id: uuid.UUID) -> Optional[ChunkNode]:
        return self.session.get(ChunkNode, chunk_id)

    def save(self, chunk: ChunkNode) -> ChunkNode:
        self.session.add(chunk)
        self.session.flush()
        return chunk

    def find_all(self) -> List[ChunkNode]:
        stmt = select(ChunkNode)
        return list(self.session.execute(stmt).scalars().all())

class GraphRelationRepository:
    def __init__(self, session: Session):
        self.session = session

    def find_by_id(self, rel_id: uuid.UUID) -> Optional[GraphRelation]:
        return self.session.get(GraphRelation, rel_id)

    def save(self, rel: GraphRelation) -> GraphRelation:
        self.session.add(rel)
        self.session.flush()
        return rel

    def find_all(self) -> List[GraphRelation]:
        stmt = select(GraphRelation)
        return list(self.session.execute(stmt).scalars().all())
