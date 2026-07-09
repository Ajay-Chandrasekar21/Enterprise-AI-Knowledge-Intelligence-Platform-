package com.enterprise.eakip.agent.ai.memory;

import java.util.List;

public interface MemoryService {
    void saveMessage(String sessionId, String role, String content);
    List<String> getChatHistory(String sessionId);
    void clearSession(String sessionId);
    void saveLongTermMemory(String userId, String key, String value);
    String getLongTermMemory(String userId, String key);
}
