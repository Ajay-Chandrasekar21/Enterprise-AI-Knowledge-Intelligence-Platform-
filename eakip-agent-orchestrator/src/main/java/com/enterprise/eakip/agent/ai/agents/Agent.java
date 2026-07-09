package com.enterprise.eakip.agent.ai.agents;

import com.enterprise.eakip.agent.ai.runtime.AgentResult;
import com.enterprise.eakip.agent.ai.runtime.AgentSession;

public interface Agent {
    AgentResult execute(AgentSession session);
    AgentMetadata getMetadata();
}
