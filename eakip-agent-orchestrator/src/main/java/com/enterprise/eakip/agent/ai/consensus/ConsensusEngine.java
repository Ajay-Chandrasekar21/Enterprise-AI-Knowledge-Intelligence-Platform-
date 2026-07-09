package com.enterprise.eakip.agent.ai.consensus;

import com.enterprise.eakip.agent.ai.runtime.AgentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConsensusEngine {

    public String resolveConsensus(List<AgentResult> results) {
        log.info("Merging multiple agents responses using ConsensusEngine rank filters");
        
        if (results == null || results.isEmpty()) {
            return "No responses to resolve";
        }

        // Rank by confidence score descending
        List<AgentResult> ranked = results.stream()
                .sorted(Comparator.comparingDouble(AgentResult::getConfidence).reversed())
                .collect(Collectors.toList());

        StringBuilder merged = new StringBuilder("Consensus Resolution (Ranks by confidence):\n\n");
        for (int i = 0; i < ranked.size(); i++) {
            AgentResult res = ranked.get(i);
            merged.append(String.format("[%d] Agent: %s (Confidence: %.2f)\n%s\n\n",
                    i + 1, res.getAgentName(), res.getConfidence(), res.getOutputContent()));
        }

        return merged.toString();
    }
}
