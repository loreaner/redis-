package com.example.redis.demos.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DefaultRedisScript<Long> lockScript;

    @Autowired
    private DefaultRedisScript<Long> unlockScript;

    public boolean tryLock(String key, String value, long expireTime, TimeUnit timeUnit) {
        Long result = redisTemplate.execute(lockScript, Collections.singletonList(key), value, String.valueOf(timeUnit.toMillis(expireTime)));
        return result != null && result == 1;
    }

    public boolean unlock(String key, String value) {
        Long result = redisTemplate.execute(unlockScript, Collections.singletonList(key), value);
        return result != null && result == 1;
    }

    // 新增方法：模拟抢票操作，确保原子性
    public boolean grabTicket(String ticketKey, String userKey, String value, long expireTime, TimeUnit timeUnit) {
        if (tryLock(ticketKey, value, expireTime, timeUnit)) {
            try {
                // 模拟抢票逻辑，这里可以替换为实际的业务逻辑
                int remainingTickets = Integer.parseInt(redisTemplate.opsForValue().get(ticketKey));
                if (remainingTickets > 0) {
                    redisTemplate.opsForValue().set(ticketKey, String.valueOf(remainingTickets - 1));
                    redisTemplate.opsForValue().set(userKey, "Ticket acquired by " + value);
                    return true;
                }
            } finally {
                unlock(ticketKey, value);
            }
        }
        return false;
    }
}