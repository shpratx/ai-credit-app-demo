package com.lloyds.creditcoach.creditscore.infrastructure.cache;

import com.lloyds.creditcoach.creditscore.application.dto.ScoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);

    // In production: use RedisTemplate<String, ScoreResponse>
    private final Map<String, ScoreResponse> cache = new ConcurrentHashMap<>();

    public ScoreResponse getScore(UUID customerId) {
        String key = buildKey(customerId);
        ScoreResponse cached = cache.get(key);
        if (cached != null) {
            log.debug("Cache hit for customerId={}", customerId);
        }
        return cached;
    }

    public ScoreResponse getStaleScore(UUID customerId) {
        // In production: Redis allows reading expired keys with custom logic
        return cache.get(buildKey(customerId));
    }

    public void putScore(UUID customerId, ScoreResponse response) {
        cache.put(buildKey(customerId), response);
        log.debug("Cached score for customerId={}", customerId);
    }

    public void evict(UUID customerId) {
        cache.remove(buildKey(customerId));
    }

    private String buildKey(UUID customerId) {
        return "credit-coach:score:" + customerId + ":EXPERIAN";
    }
}
