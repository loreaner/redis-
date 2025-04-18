package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class Hash {

    private static final String KEY = "USER";

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    public Hash(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    // 添加/更新字段
    public void put(String field, Object value) {
        hashOperations.put(KEY, field, value);
    }

    // 获取单个字段
    public Object get(String field) {
        return hashOperations.get(KEY, field);
    }

    // 获取整个Hash
    public Map<String, Object> getAll() {
        return hashOperations.entries(KEY);
    }

    // 删除字段
    public void delete(String... fields) {
        hashOperations.delete(KEY, (Object[]) fields);
    }

    // 检查字段是否存在
    public boolean hasKey(String field) {
        return hashOperations.hasKey(KEY, field);
    }
}