package com.enterprise.eakip.agent.ai.planning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AgentPlanner {

    public TaskGraph generatePlan(String userQuery) {
        log.info("Generating dynamic execution Task Graph for query: {}", userQuery);
        String graphId = UUID.randomUUID().toString();
        
        List<TaskGraph.Node> nodes = new ArrayList<>();
        List<TaskGraph.Edge> edges = new ArrayList<>();

        if (userQuery.toLowerCase().contains("interview") || userQuery.toLowerCase().contains("prepare")) {
            // Complex multi-agent task graph setup
            nodes.add(new TaskGraph.Node("n1", "Fetch Java Books", "SEQUENTIAL", "BOOK_DISCOVERY_AGENT", "PENDING"));
            nodes.add(new TaskGraph.Node("n2", "Generate Custom Recommendations", "PARALLEL", "RECOMMENDATION_AGENT", "PENDING"));
            nodes.add(new TaskGraph.Node("n3", "Inspect Borrow Statistics", "PARALLEL", "ANALYTICS_AGENT", "PENDING"));
            nodes.add(new TaskGraph.Node("n4", "Trigger Reminders Alerts", "CONDITIONAL", "NOTIFICATION_AGENT", "PENDING"));

            edges.add(new TaskGraph.Edge("n1", "n2", null));
            edges.add(new TaskGraph.Edge("n1", "n3", null));
            edges.add(new TaskGraph.Edge("n2", "n4", "has_matches"));
            edges.add(new TaskGraph.Edge("n3", "n4", null));
        } else {
            // Default simple task graph mapping
            nodes.add(new TaskGraph.Node("n1", "Identify Catalog Results", "SEQUENTIAL", "BOOK_DISCOVERY_AGENT", "PENDING"));
            nodes.add(new TaskGraph.Node("n2", "Run Semantic Rank check", "SEQUENTIAL", "SEMANTIC_SEARCH_AGENT", "PENDING"));
            edges.add(new TaskGraph.Edge("n1", "n2", null));
        }

        return TaskGraph.builder()
                .graphId(graphId)
                .nodes(nodes)
                .edges(edges)
                .build();
    }
}
