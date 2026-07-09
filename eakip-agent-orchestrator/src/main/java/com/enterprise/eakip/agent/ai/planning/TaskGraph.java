package com.enterprise.eakip.agent.ai.planning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGraph {
    
    private String graphId;
    @Builder.Default
    private List<Node> nodes = new ArrayList<>();
    @Builder.Default
    private List<Edge> edges = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Node {
        private String id;
        private String label;
        private String type; // SEQUENTIAL, PARALLEL, CONDITIONAL, RECURSIVE
        private String assignedAgent;
        private String status; // PENDING, RUNNING, COMPLETED, FAILED
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Edge {
        private String source;
        private String target;
        private String condition; // optional conditional branch labels
    }
}
