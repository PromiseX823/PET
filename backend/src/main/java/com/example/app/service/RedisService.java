
package com.example.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long DEFAULT_EXPIRE_SECONDS = 3600;
    private static final long SHORT_EXPIRE_SECONDS = 600;

    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
        log.debug("Set cache: key={}, expire={} {}", key, timeout, unit);
    }

    public String get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        log.debug("Get cache: key={}, exists={}", key, value != null);
        return value;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("Delete cache: key={}", key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setWithExpire(String key, String value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    public void setWithDefaultExpire(String key, String value) {
        set(key, value, DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    public void setWithShortExpire(String key, String value) {
        set(key, value, SHORT_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    public <T> void setObject(String key, T obj) {
        try {
            String value = objectMapper.writeValueAsString(obj);
            setWithDefaultExpire(key, value);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
        }
    }

    public <T> void setObject(String key, T obj, long seconds) {
        try {
            String value = objectMapper.writeValueAsString(obj);
            setWithExpire(key, value, seconds);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to object", e);
            return null;
        }
    }

    public void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public Long getCounter(String key) {
        String value = get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public void hashSet(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public String hashGet(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    public void hashDelete(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    public void flushAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        log.info("Flushed all cache");
    }
}
