package com.example.app.service;

/**
 * Redis操作接口
 * 支持真正的Redis和Mock实现
 */
public interface RedisOperations {

    /**
     * 设置缓存值
     */
    void set(String key, String value);

    /**
     * 设置缓存值并指定过期时间
     */
    void setWithExpire(String key, String value, long expireSeconds);

    /**
     * 获取缓存值
     */
    String get(String key);

    /**
     * 删除缓存
     */
    void delete(String key);

    /**
     * 检查key是否存在
     */
    boolean hasKey(String key);

    /**
     * 设置缓存值（如果key不存在才设置）- 用于分布式锁
     * @return true表示设置成功，false表示key已存在
     */
    boolean setIfAbsent(String key, String value, long expireSeconds);
}