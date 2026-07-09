import uuid
import logging
from typing import List, Optional

logger = logging.getLogger(__name__)

class TaskGraph:
    class Node:
        def __init__(self, id: str, label: str, type: str, assigned_agent: str, status: str):
            self.id = id
            self.label = label
            self.type = type
            self.assigned_agent = assigned_agent
            self.status = status
            
        @property
        def agent_name(self) -> str:
            return self.assigned_agent

    class Edge:
        def __init__(self, source: str, target: str, condition: Optional[str] = None):
            self.source = source
            self.target = target
            self.condition = condition

    def __init__(self, graph_id: str, nodes: List[Node], edges: List[Edge]):
        self.graph_id = graph_id
        self.nodes = nodes
        self.edges = edges

class AgentPlanner:
    @staticmethod
    def generate_plan(user_query: str) -> TaskGraph:
        logger.info(f"Generating dynamic execution Task Graph for query: {user_query}")
        graph_id = str(uuid.uuid4())
        
        nodes = []
        edges = []

        if "interview" in user_query.lower() or "prepare" in user_query.lower():
            # Complex multi-agent task graph setup
            nodes.append(TaskGraph.Node("n1", "Fetch Java Books", "SEQUENTIAL", "BOOK_DISCOVERY_AGENT", "PENDING"))
            nodes.append(TaskGraph.Node("n2", "Generate Custom Recommendations", "PARALLEL", "RECOMMENDATION_AGENT", "PENDING"))
            nodes.append(TaskGraph.Node("n3", "Inspect Borrow Statistics", "PARALLEL", "ANALYTICS_AGENT", "PENDING"))
            nodes.append(TaskGraph.Node("n4", "Trigger Reminders Alerts", "CONDITIONAL", "NOTIFICATION_AGENT", "PENDING"))

            edges.append(TaskGraph.Edge("n1", "n2", None))
            edges.append(TaskGraph.Edge("n1", "n3", None))
            edges.append(TaskGraph.Edge("n2", "n4", "has_matches"))
            edges.append(TaskGraph.Edge("n3", "n4", None))
        else:
            # Default simple task graph mapping
            nodes.append(TaskGraph.Node("n1", "Identify Catalog Results", "SEQUENTIAL", "BOOK_DISCOVERY_AGENT", "PENDING"))
            nodes.append(TaskGraph.Node("n2", "Run Semantic Rank check", "SEQUENTIAL", "SEMANTIC_SEARCH_AGENT", "PENDING"))
            edges.append(TaskGraph.Edge("n1", "n2", None))

        return TaskGraph(graph_id=graph_id, nodes=nodes, edges=edges)
