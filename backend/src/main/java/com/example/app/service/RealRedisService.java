package com.example.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 真正的Redis缓存服务实现
 * 使用Spring Data Redis的StringRedisTemplate进行缓存操作
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class RealRedisService implements RedisOperations {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 设置缓存值
     */
    public void set(String key, String value) {
        log.debug("Redis set: {} = {}", key, value);
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存值并指定过期时间
     */
    public void setWithExpire(String key, String value, long expireSeconds) {
        log.debug("Redis setWithExpire: {} = {} ({}s)", key, value, expireSeconds);
        stringRedisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存值
     */
    public String get(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        log.debug("Redis get: {} = {}", key, value);
        return value;
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        log.debug("Redis delete: {}", key);
        stringRedisTemplate.delete(key);
    }

    /**
     * 检查key是否存在
     */
    public boolean hasKey(String key) {
        Boolean exists = stringRedisTemplate.hasKey(key);
        return exists != null && exists;
    }

    /**
     * 设置缓存值（如果key不存在才设置）- 用于分布式锁
     * @return true表示设置成功（获取锁成功），false表示key已存在
     */
    public boolean setIfAbsent(String key, String value, long expireSeconds) {
        log.debug("Redis setIfAbsent: {} = {}", key, value);
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, value, expireSeconds, TimeUnit.SECONDS);
        return result != null && result;
    }

    /**
     * 获取缓存过期时间（秒）
     */
    public long getExpire(String key) {
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire != null ? expire : -1;
    }

    /**
     * 设置缓存过期时间
     */
    public void expire(String key, long expireSeconds) {
        stringRedisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 批量删除缓存
     */
    public void deleteAll(String... keys) {
        if (keys != null && keys.length > 0) {
            stringRedisTemplate.delete(java.util.Arrays.asList(keys));
        }
    }

    /**
     * 自增操作
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增指定值
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减操作
     */
    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }
}