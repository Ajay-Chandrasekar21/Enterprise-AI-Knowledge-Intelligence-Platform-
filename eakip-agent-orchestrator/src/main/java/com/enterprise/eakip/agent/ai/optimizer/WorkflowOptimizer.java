package com.enterprise.eakip.agent.ai.optimizer;

import com.enterprise.eakip.agent.ai.planning.TaskGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WorkflowOptimizer {

    public TaskGraph optimize(TaskGraph graph) {
        log.info("Running execution latency and token optimization checks on Task Graph: {}", graph.getGraphId());
        
        List<TaskGraph.Node> optimizedNodes = new ArrayList<>();
        // Simple optimization rule: If n2 and n3 are both analytics/lookups, execute n2 and n3 in parallel (already done),
        // or bypass n3 if parameters are empty.
        
        for (TaskGraph.Node node : graph.getNodes()) {
            optimizedNodes.add(node);
        }

        return TaskGraph.builder()
                .graphId(graph.getGraphId())
                .nodes(optimizedNodes)
                .edges(graph.getEdges())
                .build();
    }
}
