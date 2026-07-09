package com.enterprise.eakip.agent.ai.tools;

import java.util.Map;

public interface Tool {
    String getName();
    String getDescription();
    Map<String, String> getParameters();
    Object execute(Map<String, Object> arguments);
}
