from typing import List
from fastapi import APIRouter, Query, Body
from eakip_core.common.dto import ApiResponse

router = APIRouter(prefix="/api/v1/planning", tags=["Agent Planning & Reasoning API"])

@router.get("/graph", response_model=ApiResponse[dict])
def get_task_graph(query: str):
    from eakip_agent_orchestrator.planning import AgentPlanner
    planner = AgentPlanner()
    graph = planner.generate_plan(query)
    
    # Map graph elements to json serializable dict
    result = {
        "nodes": [{"id": n.id, "label": n.label, "agentName": n.agent_name} for n in graph.nodes],
        "edges": [{"source": e.source, "target": e.target} for e in graph.edges]
    }
    return ApiResponse.success_response(result, "Task Graph generated successfully")

@router.post("/reason", response_model=ApiResponse[dict])
def execute_reasoning(query: str = Body(...)):
    from eakip_agent_orchestrator.reasoning import ReasoningEngine
    reasoning_engine = ReasoningEngine()
    result = reasoning_engine.execute_reasoning(query)
    
    return ApiResponse.success_response({
        "steps": result.steps,
        "thoughtChain": result.thought_chain,
        "finalAnswer": result.final_answer
    }, "Reasoning chain executed")

@router.post("/reflect", response_model=ApiResponse[dict])
def reflect(content: str = Body(...)):
    from eakip_agent_orchestrator.reflection import ReflectionEngine
    reflection_engine = ReflectionEngine()
    result = reflection_engine.evaluate(content)
    
    return ApiResponse.success_response({
        "accuracyScore": result.accuracy_score,
        "consistencyScore": result.consistency_score,
        "feedback": result.feedback
    }, "Self-reflection completed")

@router.post("/consensus", response_model=ApiResponse[str])
def resolve_consensus(agent_outputs: List[str] = Body(...)):
    from eakip_agent_orchestrator.consensus import ConsensusEngine
    from eakip_agent_orchestrator.runtime import AgentResult
    consensus_engine = ConsensusEngine()
    
    results = []
    for idx, output in enumerate(agent_outputs):
        results.append(AgentResult(
            agent_name=f"Agent_{idx}",
            output_content=output,
            state="COMPLETED",
            confidence=0.85 + (idx * 0.05)
        ))
    merged = consensus_engine.resolve_consensus(results)
    return ApiResponse.success_response(merged, "Consensus merged response")
