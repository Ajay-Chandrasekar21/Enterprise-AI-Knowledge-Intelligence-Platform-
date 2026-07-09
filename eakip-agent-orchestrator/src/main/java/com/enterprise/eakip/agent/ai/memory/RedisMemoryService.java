package com.enterprise.eakip.agent.ai.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMemoryService implements MemoryService {

    private final StringRedisTemplate redisTemplate;
    
    // In-memory fallback if Redis connection fails
    private final Map<String, List<String>> localCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> localLongTerm = new ConcurrentHashMap<>();

    private static final String CHAT_KEY_PREFIX = "eakip:chat:session:";
    private static final String USER_KEY_PREFIX = "eakip:user:memory:";

    @Override
    public void saveMessage(String sessionId, String role, String content) {
        String messageStr = role + ": " + content;
        try {
            String key = CHAT_KEY_PREFIX + sessionId;
            redisTemplate.opsForList().rightPush(key, messageStr);
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
            log.debug("Saved session message to Redis for session: {}", sessionId);
        } catch (Exception e) {
            log.warn("Redis operations failed. Saving message to local fallback cache.", e);
            localCache.computeIfAbsent(sessionId, k -> Collections.synchronizedList(new ArrayList<>())).add(messageStr);
        }
    }

    @Override
    public List<String> getChatHistory(String sessionId) {
        try {
            String key = CHAT_KEY_PREFIX + sessionId;
            List<String> history = redisTemplate.opsForList().range(key, 0, -1);
            return history != null ? history : new ArrayList<>();
        } catch (Exception e) {
            log.warn("Redis operations failed. Retrieving from local fallback cache.", e);
            return localCache.getOrDefault(sessionId, new ArrayList<>());
        }
    }

    @Override
    public void clearSession(String sessionId) {
        try {
            String key = CHAT_KEY_PREFIX + sessionId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis delete failed. Clearing local cache.", e);
            localCache.remove(sessionId);
        }
    }

    @Override
    public void saveLongTermMemory(String userId, String key, String value) {
        try {
            String redisKey = USER_KEY_PREFIX + userId;
            redisTemplate.opsForHash().put(redisKey, key, value);
        } catch (Exception e) {
            log.warn("Redis operations failed. Saving to local fallback map.", e);
            localLongTerm.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(key, value);
        }
    }

    @Override
    public String getLongTermMemory(String userId, String key) {
        try {
            String redisKey = USER_KEY_PREFIX + userId;
            Object val = redisTemplate.opsForHash().get(redisKey, key);
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            log.warn("Redis operations failed. Querying local fallback map.", e);
            Map<String, String> userMap = localLongTerm.get(userId);
            return userMap != null ? userMap.get(key) : null;
        }
    }
}
