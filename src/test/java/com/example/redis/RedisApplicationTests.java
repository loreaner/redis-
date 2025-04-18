package com.example.redis;

import com.example.redis.demos.redis.RedisLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class RedisApplicationTests {

    @Autowired
    private RedisLock redisLock;

    @Test
    void contextLoads() {
    }

    @Test
    void testGrabTicket() throws InterruptedException {
        String ticketKey = "ticket:1";
        String userKey = "user:1";
        redisLock.redisTemplate.opsForValue().set(ticketKey, "10"); // 初始化票数为10

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String userValue = "user" + i;
            executorService.submit(() -> {
                try {
                    boolean result = redisLock.grabTicket(ticketKey, userKey, userValue, 10, TimeUnit.SECONDS);
                    System.out.println(userValue + "抢票结果: " + result);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println("剩余票数: " + redisLock.redisTemplate.opsForValue().get(ticketKey));
    }

    @Test
    void testLockWithFiveMinutesExpiration() throws InterruptedException {
        String lockKey = "lock:1";
        String lockValue = "lockValue";

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                try {
                    boolean result = redisLock.tryLock(lockKey, lockValue, 5, TimeUnit.MINUTES);
                    System.out.println(Thread.currentThread().getName() + " 获取锁结果: " + result);
                    if (result) {
                        // 模拟持有锁5分钟
                        Thread.sleep(300);
                        redisLock.unlock(lockKey, lockValue);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }
}