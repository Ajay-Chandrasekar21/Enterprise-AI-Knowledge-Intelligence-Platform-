import uuid
import json
import logging
from datetime import datetime
from typing import Optional, List
from sqlalchemy import select
from sqlalchemy.orm import Session
from .models import WorkflowDefinition, WorkflowInstance
from .db_helper import get_dynamic_session

logger = logging.getLogger(__name__)

class WorkflowDefinitionRepository:
    def __init__(self, session: Optional[Session] = None):
        self._session = session

    @property
    def session(self) -> Session:
        if self._session is None:
            self._session = get_dynamic_session()
        return self._session

    def find_all(self) -> List[WorkflowDefinition]:
        stmt = select(WorkflowDefinition)
        return list(self.session.execute(stmt).scalars().all())

    def find_by_id(self, def_id: uuid.UUID) -> Optional[WorkflowDefinition]:
        return self.session.get(WorkflowDefinition, def_id)

    def save(self, definition: WorkflowDefinition) -> WorkflowDefinition:
        self.session.add(definition)
        self.session.commit()
        return definition

class WorkflowInstanceRepository:
    def __init__(self, session: Optional[Session] = None):
        self._session = session

    @property
    def session(self) -> Session:
        if self._session is None:
            self._session = get_dynamic_session()
        return self._session

    def find_all(self) -> List[WorkflowInstance]:
        stmt = select(WorkflowInstance)
        return list(self.session.execute(stmt).scalars().all())

    def find_by_id(self, inst_id: uuid.UUID) -> Optional[WorkflowInstance]:
        return self.session.get(WorkflowInstance, inst_id)

    def save(self, instance: WorkflowInstance) -> WorkflowInstance:
        self.session.add(instance)
        self.session.commit()
        return instance

class AutonomousWorkflowExecutor:
    def __init__(
        self,
        instance_repo: Optional[WorkflowInstanceRepository] = None,
        def_repo: Optional[WorkflowDefinitionRepository] = None
    ):
        self.instance_repo = instance_repo if instance_repo else WorkflowInstanceRepository()
        self.def_repo = def_repo if def_repo else WorkflowDefinitionRepository()

    def start_execution(self, definition_id: uuid.UUID) -> WorkflowInstance:
        logger.info(f"Starting autonomous workflow execution. DefID={definition_id}")
        definition = self.def_repo.find_by_id(definition_id)
        if not definition:
            raise ValueError(f"No workflow schema matches: {definition_id}")

        instance = WorkflowInstance(
            definition=definition,
            current_node_id="node_1",
            status="RUNNING",
            variables_json="{}"
        )
        instance = self.instance_repo.save(instance)
        self._execute_next(instance)
        return instance

    def approve_step(self, instance_id: uuid.UUID) -> None:
        logger.info(f"Human approval recorded for workflow instance: {instance_id}")
        instance = self.instance_repo.find_by_id(instance_id)
        if not instance:
            raise ValueError(f"No instance matches: {instance_id}")

        if instance.status.upper() == "WAITING_APPROVAL":
            instance.status = "RUNNING"
            instance.current_node_id = "node_3" # proceed to next step
            self.instance_repo.save(instance)
            self._execute_next(instance)

    def _execute_next(self, instance: WorkflowInstance) -> None:
        node = instance.current_node_id
        logger.info(f"Executing node: {node} inside instance: {instance.id}")

        if not node:
            return

        node_upper = node.upper()
        if node_upper == "NODE_1":
            # Check out book (Tool Node) -> Proceed to node_2
            instance.current_node_id = "node_2"
            instance.last_updated = datetime.now()
            self.instance_repo.save(instance)
            self._execute_next(instance)
        elif node_upper == "NODE_2":
            # Needs Librarian Review (Approval Node) -> Pause
            instance.status = "WAITING_APPROVAL"
            instance.last_updated = datetime.now()
            self.instance_repo.save(instance)
            logger.info("Workflow paused. Waiting for human approval on node_2.")
        elif node_upper == "NODE_3":
            # Send Alert notification (Notification Node) -> Complete
            instance.status = "COMPLETED"
            instance.current_node_id = None
            instance.last_updated = datetime.now()
            self.instance_repo.save(instance)
            logger.info("Workflow execution completed successfully.")
