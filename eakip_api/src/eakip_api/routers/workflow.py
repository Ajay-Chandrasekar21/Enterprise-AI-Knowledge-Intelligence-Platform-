import uuid
from typing import List
from fastapi import APIRouter, Query, Depends
from eakip_core.common.dto import ApiResponse

router = APIRouter(prefix="/api/v1/workflow", tags=["Autonomous Workflow Engine API"])

@router.get("/definitions", response_model=ApiResponse[List[dict]])
def get_definitions():
    from eakip_agent_orchestrator.workflow import WorkflowDefinitionRepository
    repo = WorkflowDefinitionRepository()
    definitions = repo.find_all()
    
    result = [{
        "id": str(d.id),
        "name": d.name,
        "triggerType": d.trigger_type,
        "nodesJson": d.nodes_json
    } for d in definitions]
    return ApiResponse.success_response(result, "Workflow definitions loaded")

@router.post("/definitions", response_model=ApiResponse[dict])
def create_definition(name: str, trigger_type: str):
    from eakip_agent_orchestrator.workflow import WorkflowDefinitionRepository, WorkflowDefinition
    repo = WorkflowDefinitionRepository()
    
    nodes_json = (
        '[{"id":"node_1","type":"TOOL","name":"LibraryBorrowTool"},'
        '{"id":"node_2","type":"APPROVAL","name":"Librarian Review"},'
        '{"id":"node_3","type":"NOTIFICATION","name":"Send Receipt"}]'
    )
    definition = WorkflowDefinition(
        name=name,
        trigger_type=trigger_type,
        nodes_json=nodes_json
    )
    definition = repo.save(definition)
    
    result = {
        "id": str(definition.id),
        "name": definition.name,
        "triggerType": definition.trigger_type,
        "nodesJson": definition.nodes_json
    }
    return ApiResponse.success_response(result, "Workflow definition saved successfully")

@router.post("/execute", response_model=ApiResponse[dict])
def execute_workflow(definition_id: uuid.UUID):
    from eakip_agent_orchestrator.workflow import AutonomousWorkflowExecutor
    executor = AutonomousWorkflowExecutor()
    instance = executor.start_execution(definition_id)
    
    result = {
        "id": str(instance.id),
        "definitionId": str(instance.definition_id),
        "currentNodeId": instance.current_node_id,
        "status": instance.status,
        "errorMessage": instance.error_message
    }
    return ApiResponse.success_response(result, "Workflow execution launched successfully")

@router.post("/approve", response_model=ApiResponse[str])
def approve_step(instance_id: uuid.UUID):
    from eakip_agent_orchestrator.workflow import AutonomousWorkflowExecutor
    executor = AutonomousWorkflowExecutor()
    executor.approve_step(instance_id)
    return ApiResponse.success_response(str(instance_id), "Pending step approved. Workflow resumed.")

@router.get("/instances", response_model=ApiResponse[List[dict]])
def get_instances():
    from eakip_agent_orchestrator.workflow import WorkflowInstanceRepository
    repo = WorkflowInstanceRepository()
    instances = repo.find_all()
    
    result = [{
        "id": str(i.id),
        "definitionId": str(i.definition_id),
        "currentNodeId": i.current_node_id,
        "status": i.status,
        "errorMessage": i.error_message
    } for i in instances]
    return ApiResponse.success_response(result, "Workflow instances loaded")
