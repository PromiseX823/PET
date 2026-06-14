package com.example.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock Redis服务（当Redis不可用时使用内存缓存）
 */
@Slf4j
@Service
public class RedisService implements RedisOperations {

    private final Map<String, String> inMemoryCache = new ConcurrentHashMap<>();

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    public void set(String key, String value) {
        log.debug("Mock Redis set: {} = {}", key, value);
        inMemoryCache.put(key, value);
    }

    public void setWithExpire(String key, String value, long expireSeconds) {
        log.debug("Mock Redis setWithExpire: {} = {} ({}s)", key, value, expireSeconds);
        inMemoryCache.put(key, value);
    }

    public String get(String key) {
        String value = inMemoryCache.get(key);
        log.debug("Mock Redis get: {} = {}", key, value);
        return value;
    }

    public void delete(String key) {
        log.debug("Mock Redis delete: {}", key);
        inMemoryCache.remove(key);
    }

    public boolean hasKey(String key) {
        return inMemoryCache.containsKey(key);
    }

    public boolean setIfAbsent(String key, String value, long expireSeconds) {
        log.debug("Mock Redis setIfAbsent: {} = {}", key, value);
        return inMemoryCache.putIfAbsent(key, value) == null;
    }
}