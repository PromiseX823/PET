package com.example.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁的键
     * @param expireSeconds 锁的过期时间（秒）
     * @return 锁的值（用于释放锁），如果获取失败返回null
     */
    public String tryLock(String lockKey, long expireSeconds) {
        String lockValue = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(LOCK_PREFIX + lockKey, lockValue, expireSeconds, TimeUnit.SECONDS);
        
        if (Boolean.TRUE.equals(success)) {
            log.debug("Successfully acquired lock: {}, value: {}", lockKey, lockValue);
            return lockValue;
        }
        
        log.debug("Failed to acquire lock: {}", lockKey);
        return null;
    }

    /**
     * 释放分布式锁（使用Lua脚本保证原子性）
     * @param lockKey 锁的键
     * @param lockValue 锁的值（用于验证）
     * @return 是否成功释放
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        String script = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";
        
        Long result = redisTemplate.execute(
                new org.springframework.data.redis.core.script.DefaultRedisScript<>(script, Long.class),
                java.util.Collections.singletonList(LOCK_PREFIX + lockKey),
                lockValue
        );
        
        boolean released = result != null && result == 1;
        if (released) {
            log.debug("Successfully released lock: {}", lockKey);
        } else {
            log.warn("Failed to release lock: {}, value mismatch or lock expired", lockKey);
        }
        return released;
    }

    /**
     * 尝试获取锁并执行操作
     * @param lockKey 锁的键
     * @param expireSeconds 锁的过期时间
     * @param action 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果，如果获取锁失败返回null
     */
    public <T> T tryLockAndExecute(String lockKey, long expireSeconds, Supplier<T> action) {
        String lockValue = tryLock(lockKey, expireSeconds);
        if (lockValue == null) {
            log.warn("Could not acquire lock for key: {}", lockKey);
            return null;
        }
        
        try {
            return action.get();
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 尝试获取锁，等待重试
     * @param lockKey 锁的键
     * @param expireSeconds 锁的过期时间
     * @param maxRetries 最大重试次数
     * @param retryIntervalMs 重试间隔（毫秒）
     * @return 锁的值，如果获取失败返回null
     */
    public String tryLockWithRetry(String lockKey, long expireSeconds, int maxRetries, long retryIntervalMs) {
        for (int i = 0; i <= maxRetries; i++) {
            String lockValue = tryLock(lockKey, expireSeconds);
            if (lockValue != null) {
                return lockValue;
            }
            
            if (i < maxRetries) {
                try {
                    Thread.sleep(retryIntervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 延长锁的过期时间
     * @param lockKey 锁的键
     * @param lockValue 锁的值（用于验证）
     * @param additionalSeconds 延长的秒数
     * @return 是否成功延长
     */
    public boolean extendLock(String lockKey, String lockValue, long additionalSeconds) {
        String script = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('expire', KEYS[1], ARGV[2]) " +
            "else " +
            "    return 0 " +
            "end";
        
        Long result = redisTemplate.execute(
                new org.springframework.data.redis.core.script.DefaultRedisScript<>(script, Long.class),
                java.util.Collections.singletonList(LOCK_PREFIX + lockKey),
                lockValue,
                String.valueOf(additionalSeconds)
        );
        
        return result != null && result == 1;
    }

    /**
     * 检查锁是否被持有
     * @param lockKey 锁的键
     * @return 是否被持有
     */
    public boolean isLocked(String lockKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(LOCK_PREFIX + lockKey));
    }
}