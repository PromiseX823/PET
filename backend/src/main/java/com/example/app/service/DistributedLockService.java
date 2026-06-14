package com.example.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DistributedLockService {

    // 使用真正的Redis服务（如果Redis可用），否则使用Mock
    @Autowired(required = false)
    private RealRedisService realRedisService;

    @Autowired
    private RedisService mockRedisService;

    private RedisOperations getRedisService() {
        return realRedisService != null ? realRedisService : mockRedisService;
    }

    public String tryLock(String key, long expireSeconds) {
        String lockValue = UUID.randomUUID().toString();
        boolean acquired = getRedisService().setIfAbsent(key, lockValue, expireSeconds);
        if (acquired) {
            log.debug("Lock acquired: key={}, value={}", key, lockValue);
            return lockValue;
        }
        return null;
    }

    public String tryLockWithRetry(String key, long expireSeconds, int maxRetries, long retryIntervalMs) {
        for (int i = 0; i < maxRetries; i++) {
            String lockValue = tryLock(key, expireSeconds);
            if (lockValue != null) {
                return lockValue;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(retryIntervalMs);
            } catch (InterruptedException e) {
                log.warn("Lock retry interrupted: key={}", key);
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.warn("Failed to acquire lock after {} retries: key={}", maxRetries, key);
        return null;
    }

    public void releaseLock(String key, String lockValue) {
        if (lockValue == null) {
            return;
        }
        String currentValue = getRedisService().get(key);
        if (lockValue.equals(currentValue)) {
            getRedisService().delete(key);
            log.debug("Lock released: key={}, value={}", key, lockValue);
        } else {
            log.warn("Lock value mismatch, cannot release: key={}, expected={}, actual={}", key, lockValue, currentValue);
        }
    }
}